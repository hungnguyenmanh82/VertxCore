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

+ CircuitBreaker: tạo một Future để wrap lại Handler 
 */
public class App91_CircuitBreaker {

	public static void main(String[] args) throws InterruptedException{
		System.out.println("start main(): thread="+Thread.currentThread().getId());

		//create a new instance Vertx => a worker thread sinh ra để quản lý loop Event, vì thế hàm main() kết thúc nhưng App ko stop
		// gọi vertx.close() để stop thread này
		Vertx vertx = Vertx.vertx();

		// dùng Log4j2 thì ko cần cái này
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
				.setFailuresRollingWindow(10000) // count failures trong 10000ms gần nhất (cho phép tỷ lệ drop trong hạn mức).
				);

		/**
		 * cần hiểu tính chất của Future và AsyncResult trc đã thì đọc phần này sẽ hiểu.
		 */
		// circuitBreaker run handlerFutureRequest
		// promise sẽ thông báo cho CircuitBreaker về tình trạng request => promise.fail()/promise.complete()
		Handler<Promise<String>> handlerFutureRequest = (Promise<String> promise)-> {
			System.out.println("run circuitBreaker.execute()"+ formatter.format(new Date()));
			System.out.println("handlerFutureRequest: thread=" + Thread.currentThread().getId());

			vertx.createHttpClient().getNow(8080, "localhost", "/", response -> {
				if (response.statusCode() != 200) {
					promise.fail("HTTP error");
				} else { //statusCode = 200
					response.exceptionHandler(promise::fail)
					.bodyHandler(buffer -> {
						promise.complete(buffer.toString());
					});
				}
			});

		};



		//=============================  Event State change Handler: Close, Open, HalfOpen ============================
		// API gateway có thể dựa vào các event này để biết đc state hiện tại của API gateway là gì dể loadbalancing or routing
		// cần xem State diagram mới hiểu các concept và cách vận hành.
		// Close State: là trạng thái cho phép gửi request
		circuitBreaker.closeHandler((Void event)-> {
			//Loadbalancer cho phép circuitBreaker.execute()
			System.out.println("circuitBreaker.closeHandler()"+ formatter.format(new Date()));  
			System.out.println("state = Closed" + " :thread=" + Thread.currentThread().getId());

		});

		// Open State: là trạng thái ko cho phép gửi request (vì các request trc đó gửi Server bị lỗi vượt Threadhold)
		// các request đi qua CircuitBreaker
		// sau khi TimeOut ở OpenState, nó sẽ chuyển sang HalfOpen state để cho phép các request tiếp tục.
		circuitBreaker.openHandler((Void event)-> {
			//Loadbalancer pending circuitBreaker.execute()
			System.out.println("circuitBreaker.openHandler()"+ formatter.format(new Date()));  
			System.out.println("state = Open"+ " :thread=" + Thread.currentThread().getId());

		});

		// halfOpen state: nếu gửi lệnh thành công thì sẽ chuyển sang Closed State
		// nếu gửi lệnh thất bại sẽ quay lại Open State và đợi ResetTimeout để chuyển trạng thái HalfOpen state.
		circuitBreaker.halfOpenHandler((Void event) ->{
			System.out.println("circuitBreaker.halfOpen()"+ formatter.format(new Date()));  
			System.out.println("state = halfOpen"+ " :thread=" + Thread.currentThread().getId());

		});


		//==================================== run request ========================================
		// có thể kiểm tra State trc khi thực hiện. Nếu 
		// circuitBreaker.execute() sẽ chạy trên context của current Thread gọi nó
		// lệnh circuitBreaker.execute() đầu tiên chạy rất chậm vì nó phải chờ Java compile runtime (mất 500ms) các lệnh tiếp theo rất nhanh (1ms)
		if(circuitBreaker.state() == CircuitBreakerState.CLOSED 
				|| circuitBreaker.state() == CircuitBreakerState.HALF_OPEN){

	
			circuitBreaker.execute(handlerFutureRequest)    // return Future<String>
			.onSuccess((String res)->{
				System.out.println(res);
			})
			.onFailure((Throwable thr)->{
				thr.printStackTrace();
			});
		}else {// CircuitBreakerState.OPEN
			// chuyển Route request tới CircuitBreaker khác quản lý Microservice mà state = ClOSED.
		}


		/**
		 *  Nếu state = OPEN thì Future này sẽ trả về luôn mà ko thực hiện handlerFutureRequest
		 *  Vì thế ko cần kiểm tra State trc khi call .execute()
		 */
		circuitBreaker.execute(handlerFutureRequest)    // return Future<String>
		.onSuccess((String res)->{
			System.out.println(res);
		})
		.onFailure((Throwable thr)->{
			thr.printStackTrace();
		});

		circuitBreaker.execute(handlerFutureRequest)
		.onSuccess((String res)->{
			System.out.println(res);
		})
		.onFailure((Throwable thr)->{
			thr.printStackTrace();
		});

		//		circuitBreaker.execute(handlerFutureRequest).onComplete(handlerAsyncResultResponse);


		Thread.currentThread().sleep(3000); //wait for vertx.close() finished
		//handlerFutureRequest sẽ ko dc thuc hien (ko tạo thread pending) vì State=Open
		//handlerAsyncResultResponse sẽ đc thuc hien luon tren current thread circuitBreaker.execute
		circuitBreaker.execute(handlerFutureRequest).onComplete(handlerAsyncResultResponse);

		//handlerFutureRequest đc gọi trở lại vì State=HalfOpen
		Thread.currentThread().sleep(6000); //wait for vertx.close() finished
		circuitBreaker.execute(handlerFutureRequest).onComplete(handlerAsyncResultResponse);

		//fallback đc gọi khi state = Open vì bất kỳ lý do gì (exception or failed) => dùng Open state event thay thế ok.
		//circuitBreaker.executeWithFallback(Handler<Future<T>> command, Function<Throwable, T> fallback)

	}

}
