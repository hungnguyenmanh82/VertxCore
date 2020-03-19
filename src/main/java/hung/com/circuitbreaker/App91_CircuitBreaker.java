package hung.com.circuitbreaker;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;

import io.netty.util.concurrent.FailedFuture;
import io.vertx.circuitbreaker.CircuitBreaker;
import io.vertx.circuitbreaker.CircuitBreakerOptions;
import io.vertx.circuitbreaker.CircuitBreakerState;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;

/**
https://martinfowler.com/bliki/CircuitBreaker.html
https://vertx.io/docs/vertx-circuit-breaker/java/

+ viec synchronize(object) khi dùng multi thread vơi CircuitBreaker sẽ làm chậm performance của hệ thống nhiều
+ CircuitBreaker ko đưa các handlerAsyncResultResponse về cùng Thread.
+ handlerAsyncResultResponse thread chính là thread của future.handler().

 */
public class App91_CircuitBreaker {

	public static void main(String[] args) throws InterruptedException{
		System.out.println("start main(): thread="+Thread.currentThread().getId());

		//create a new instance Vertx => a worker thread sinh ra để quản lý loop Event, vì thế hàm main() kết thúc nhưng App ko stop
		// gọi vertx.close() để stop thread này
		Vertx vertx = Vertx.vertx();

		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SSS");  
		Date date = new Date();  
		System.out.println(formatter.format(date));  

		// mỗi circuitBreaker sẽ quản lý các connect tới 1 Server cụ thể
		// Loadbalancer sẽ phải quản lý nhiều circuitBreaker
		CircuitBreaker circuitBreaker = CircuitBreaker.create("my-circuit-breaker", vertx,
				new CircuitBreakerOptions()
				.setMaxFailures(2) // number of failure before opening the circuit  (Open State)
				.setTimeout(1000) // consider a failure if the operation does not succeed in time
				.setFallbackOnFailure(true) // do we call the fallback on failure
				.setResetTimeout(5000) // time spent in open state before attempting to re-try
				.setFailuresRollingWindow(10000) // count failures trong 10ms gần nhất (cho phép tỷ lệ drop trong hạn mức).
				);

		/**
		 * cần hiểu tính chất của Future và AsyncResult trc đã thì đọc phần này sẽ hiểu.
		 */
		// circuitBreaker run handlerFutureRequest
		// khi future.fail() hoặc future.complete() => nó sẽ gọi Handler của handlerFutureRequest
		// tai handler của future nó sẽ xử lý các logic của circuitBreaker dựa vào AsyncResult của fail(), complete(): vd count failure
		// vì run trên nhiều thread nên bắt buộc phải synchronize(object) khi read/write count faile or state => performance sẽ ko tốt
		Handler<Promise<String>> handlerFutureRequest = new Handler<Promise<String>>() {

			@Override
			public void handle(Promise<String> future) {
				System.out.println("run circuitBreaker.execute()"+ formatter.format(new Date()));
				System.out.println("handlerFutureRequest: thread=" + Thread.currentThread().getId());

				vertx.createHttpClient().getNow(8080, "localhost", "/", response -> {
					if (response.statusCode() != 200) {
						future.fail("HTTP error");
					} else { //statusCode = 200
						response.exceptionHandler(future::fail)
						.bodyHandler(buffer -> {
							future.complete(buffer.toString());
						});
					}
				});

			}

		};
		
		//handlerAsyncResultResponse run tren thread của hàm future.complete() or future.fail() của handlerFutureRequest
		// circuitBreaker sẽ gọi tơi future.handler() trc de change State sau do moi gọi tiep tơi handlerAsyncResultResponse
		// Handler này ko cần cũng ko sao => nó ko liên quan tới biến đổi State của Circuit Breaker
		Handler<AsyncResult<String>> handlerAsyncResultResponse = new Handler<AsyncResult<String>>() {

			@Override
			public void handle(AsyncResult<String> event) {
				if( event.succeeded()){
					System.out.println("succeeded: "+ formatter.format(new Date()));  
				}else if(event.failed()){
					System.out.println("failed:" + event.result() + " : " + formatter.format(new Date()));  
				}

				System.out.println("handlerAsyncResultResponse: thread=" + Thread.currentThread().getId());

			}
		};



		//=============================  Event State change Handler ============================
		// API gateway có thể dựa vào các event này để biết đc state hiện tại của API gateway là gì dể loadbalancing or routing
		// cần xem State diagram mới hiểu các concept và cách vận hành.
		// Close State: là trạng thái cho phép gửi request
		circuitBreaker.closeHandler(new Handler<Void>() {
			@Override
			public void handle(Void event) {
				//Loadbalancer cho phép circuitBreaker.execute()
				System.out.println("circuitBreaker.closeHandler()"+ formatter.format(new Date()));  
				System.out.println("state = Closed" + " :thread=" + Thread.currentThread().getId());
				
			}
		});
		
		// Open State: là trạng thái ko cho phép gửi request (vì các request trc đó gửi Server bị lỗi vượt Threadhold)
		// các request đi qua CircuitBreaker
		// sau khi TimeOut ở OpenState, nó sẽ chuyển sang HalfOpen state để cho phép các request tiếp tục.
		circuitBreaker.openHandler(new Handler<Void>() {
			@Override
			public void handle(Void event) {
				//Loadbalancer pending circuitBreaker.execute()
				System.out.println("circuitBreaker.openHandler()"+ formatter.format(new Date()));  
				System.out.println("state = Open"+ " :thread=" + Thread.currentThread().getId());

			}
		});

		// halfOpen state: nếu gửi lệnh thành công thì sẽ chuyển sang Closed State
		// nếu gửi lệnh thất bại sẽ quay lại Open State và đợi Timeout để chuyển trạng thái HalfOpen state.
		circuitBreaker.halfOpenHandler(new Handler<Void>() {
			@Override
			public void handle(Void event) {
				System.out.println("circuitBreaker.halfOpen()"+ formatter.format(new Date()));  
				System.out.println("state = halfOpen"+ " :thread=" + Thread.currentThread().getId());

			}
		});

		//==================================== run request ========================================

		//state= Open:  circuitBreaker sẽ pending thread ko execute  => 
		//state = Closed: circuitBreaker sẽ thực hiện lệnh luôn
		// circuitBreaker.execute() sẽ chạy trên context của current Thread gọi nó
		// lệnh circuitBreaker.execute() đầu tiên chạy rất chậm vì nó phải chờ Java compile runtime (mất 500ms) các lệnh tiếp theo rất nhanh (1ms)
		if(circuitBreaker.state() == CircuitBreakerState.CLOSED 
				|| circuitBreaker.state() == CircuitBreakerState.HALF_OPEN){
			circuitBreaker.execute(handlerFutureRequest).setHandler(handlerAsyncResultResponse);
		}
		
		circuitBreaker.execute(handlerFutureRequest).setHandler(handlerAsyncResultResponse);
		circuitBreaker.execute(handlerFutureRequest).setHandler(handlerAsyncResultResponse);
//		circuitBreaker.execute(handlerFutureRequest).setHandler(handlerAsyncResultResponse);
		
		
		Thread.currentThread().sleep(3000); //wait for vertx.close() finished
		//handlerFutureRequest sẽ ko dc thuc hien (ko tạo thread pending) vì State=Open
		//handlerAsyncResultResponse sẽ đc thuc hien luon tren current thread circuitBreaker.execute
		circuitBreaker.execute(handlerFutureRequest).setHandler(handlerAsyncResultResponse);
		
		//handlerFutureRequest đc gọi trở lại vì State=HalfOpen
		Thread.currentThread().sleep(6000); //wait for vertx.close() finished
		circuitBreaker.execute(handlerFutureRequest).setHandler(handlerAsyncResultResponse);
		
		//fallback đc gọi khi state = Open vì bất kỳ lý do gì (exception or failed) => dùng Open state event thay thế ok.
		//circuitBreaker.executeWithFallback(Handler<Future<T>> command, Function<Throwable, T> fallback)

	}

}
