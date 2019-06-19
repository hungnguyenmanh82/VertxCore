package hung.com.future;


import hung.com.basicSample.MyVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Handler;
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
public class App22_WaitFutureFromVerticle {

	public static void main(String[] args) throws InterruptedException{
		
		System.out.println("main(): thread=" + Thread.currentThread().getId());
		
		Vertx vertx = Vertx.vertx();
		Future<Void> futureCreateFile = Future.future(); 
		
		vertx.deployVerticle(new FutureCreateFileVerticle(futureCreateFile));
		
		
		// Future<Void> và AsyncResult<Void> cùng kiểu <Void>	
		futureCreateFile.setHandler(new Handler<AsyncResult<Void>>() {
			// code này run trên cùng thread với CreateFile đc cấp phát bởi threadpool của vertx context (đã test)
			// Createfile() call futureCreateFile.complete(result) or futureCreateFile.fail(result)
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
		
		System.out.println("main(): end of main()");
		// app ko stop với Main() stop vì có 1 worker thread quản lý Vertx có loop bắt Event
		//vertx.close();

	}

}
