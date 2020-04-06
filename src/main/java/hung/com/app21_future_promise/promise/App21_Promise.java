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
		// toàn bộ quá trình tạo file đc thực hiện trên threadpool của Vertx context
		FileSystem fs = vertx.fileSystem();

		Promise<Void> promise = Promise.<Void>promise();

		promise.future().setHandler(new Handler<AsyncResult<Void>>() {
			// code này run trên cùng thread với fs.createFile() đc cấp phát bởi threadpool của vertx context (đã test)
			// nghĩa là fs.createFile() sẽ gọi future.complete() và future.fail() thì 2 hàm này sẽ gọi tới Hander trong hàm future.setHandler()
			@Override
			public void handle(AsyncResult<Void> event) {
				if( event.succeeded()){
					System.out.println("succeeded");	
				}else if(event.failed()){
					System.out.println("failed");
				}

				System.out.println("returnHandler: thread=" + Thread.currentThread().getId());

			}
		});

		// Handler callback: asyncResult.succeeded() = true
		promise.complete();
		
		// Handler callback: asynResult.failed() = true
		//promise.fail("test fail");

		System.out.println("main(): end of main()");
		// app ko stop với Main() stop vì có 1 worker thread quản lý Vertx có loop bắt Event
		//vertx.close();

	}

}
