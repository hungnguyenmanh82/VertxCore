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

 */
public class App22_PromiseFile_cach1 {

	public static void main(String[] args) throws InterruptedException{

		System.out.println("main(): thread=" + Thread.currentThread().getId());

		Vertx vertx = Vertx.vertx();
		// toàn bộ quá trình tạo file đc thực hiện trên threadpool của Vertx context
		FileSystem fs = vertx.fileSystem();

		//Cách 1: để hiểu bản chất. Đa phần là dùng cách 2
		//tạo promise đồng thời sẽ tạo 1 Future luôn và ngược lại
		Promise<Void> promise = Promise.<Void>promise();

		// setHandler() =>  onComplete(): chuẩn hóa lại tên với prefix = "on" giống android cho callback function
		promise.future().onComplete(new Handler<AsyncResult<Void>>() {
			// code này run trên cùng thread với fs.createFile() đc cấp phát bởi threadpool của vertx context (đã test)
			// nghĩa là fs.createFile() sẽ gọi promise.complete() và promise.fail() thì 2 hàm này sẽ gọi tới Hander trong hàm future.setHandler(Handler)
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

		/**
		  //===========================
		  + Run or Debug mode trên Eclipse lấy ./ = project folder 	  
		  + run thực tế:  ./ = folder run "java -jar *.jar"
		 //========= 
		 File("loginTest.json"):   file ở ./ folder    (tùy run thực tế hay trên eclipse)
		 File("./abc/test.json"):   
		 File("/abc"): root folder on linux (not window)
		 */
		// promise.complete()/fail() sẽ đc gọi khi create file
		// lưu ý: Promise extend Handler<AsyncResult<T>>
		fs.createFile("foo.txt", promise);  // promise và future extends  handler<asyncResult<T>> luôn => rất cơ động

		System.out.println("main(): end of main()");
		// app ko stop với Main() stop vì có 1 worker thread quản lý Vertx có loop bắt Event
		//vertx.close();

	}

}
