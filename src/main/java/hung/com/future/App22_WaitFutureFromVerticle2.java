package hung.com.future;


import hung.com.basicSample.MyVerticle1;
import io.vertx.core.AsyncResult;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.file.FileSystem;
import io.vertx.core.http.HttpServer;
import io.vertx.core.net.NetServer;

/**
 * 
Future<Type>:  extends Handler<type> và AsyncResult<type> => là kết hợp 2 class này để tạo funtion point (làm call back function khi có event).
 Khi 2 hàm future.complete(result) or future.fail(result) đc gọi thì lập tức nó sẽ gọi hàm callback của nó là Handler.handle(AsyncResult<result>). 
Future là function point => thread nào gọi nó thì nó chạy trên thread đó (đã test).

 */
public class App22_WaitFutureFromVerticle2 {

	public static void main(String[] args) throws InterruptedException{

		System.out.println("main(): thread=" + Thread.currentThread().getId());

		VertxOptions option = new VertxOptions().setWorkerPoolSize(4);
		Vertx vertx = Vertx.vertx(option);
		Future<String> futureTest = Future.future(); 

		vertx.deployVerticle(new FutureInplementAtVerticle(futureTest));


		// Future<Void> và AsyncResult<Void> cùng kiểu <Void>	
		futureTest.setHandler(new Handler<AsyncResult<String>>() {
			// code này run trên cùng thread gọi hàm futureTest.complete(result) or futureTest.fail(result)
			// ở vd này là cùng thread với FutureInplementAtVerticle
			@Override
			public void handle(AsyncResult<String> event) {
				if( event.succeeded()){
					System.out.println("succeeded");	
				}else if(event.failed()){
					System.out.println("failed");
				}

				System.out.println("returnHandler: thread=" + Thread.currentThread().getId()+
						", result=" + event.result());
			}
		});

		System.out.println("main(): end of main()");
		// app ko stop với Main() stop vì có 1 worker thread quản lý Vertx có loop bắt Event
		//vertx.close();

	}

}
