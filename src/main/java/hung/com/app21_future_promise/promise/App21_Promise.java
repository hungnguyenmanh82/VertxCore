package hung.com.app21_future_promise.promise;


import io.vertx.core.AsyncResult;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.file.FileSystem;
import io.vertx.core.http.HttpServer;
import io.vertx.core.net.NetServer;

/**
 * 
Future<Type>:  extends Handler<type> và AsyncResult<type> => là kết hợp 2 class này để tạo funtion point (làm call back function khi có event).
 Khi 2 hàm future.complete(result) or future.fail(result) đc gọi thì lập tức nó sẽ gọi hàm callback của nó là Handler.handle(AsyncResult<result>). 
Future là function point => thread nào gọi nó thì nó chạy trên thread đó (đã test).

Từ vertx 3.8 trở đi Promise thay thế cho Future ở tất cả vị trí. Cách sử dụng như nhau => dễ nhìn hơn.

 */
public class App21_Promise {

	public static void main(String[] args) throws InterruptedException{

		System.out.println("main(): thread=" + Thread.currentThread().getId());

		Vertx vertx = Vertx.vertx();


		//tạo promise đồng thời sẽ tạo 1 Future luôn và ngược lại
		Promise<String> promise = Promise.<String>promise();

		// setHandler() =>  onComplete(): chuẩn hóa lại tên với prefix = "on" giống android cho callback function
		/**
		 * compose() = setHandler() = onComplete() = addHandler() xem code FutureImpl class
		 * addHandler() ở FutureImpl làm giảm performance của code 
		 */
		promise.future().onComplete(new Handler<AsyncResult<String>>() {
			@Override
			public void handle(AsyncResult<String> event) {
				if( event.succeeded()){
					System.out.println("succeeded: result=" + event.result());	
				}else if(event.failed()){
					System.out.println("failed: cause=" + event.cause());
				}

				System.out.println("returnHandler: thread=" + Thread.currentThread().getId());

			}
		});

		/**
		 * Promise: đc tách riêng để phụ trách phần trigger callback => Promise đc truyền cho các Asynchronouse Object để trigger future
		 * Future: phụ trách phần xử lý còn lại.
		 */
		// trigger callback: asyncResult.succeeded() = true
		promise.complete("ko co viec gi kho");
		
		// trigger callback: asynResult.failed() = true
		//promise.fail("test fail");

		System.out.println("main(): end of main()");
		// app ko stop với Main() stop vì có 1 worker thread quản lý Vertx có loop bắt Event
		//vertx.close();

	}

}
