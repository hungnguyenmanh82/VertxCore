package hung.com.future;


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
 * http://vertx.io/docs/vertx-core/java/#_concurrent_composition
 * 
 * VD: tạo 2 server. Và bắt sự kiện cả 2 server khởi tạo xong hoàn toàn.
 * 
 * 	Future: đc hiểu là 1 event
 *  CompositeFuture: sẽ đc trigger event khi cả 2 server đc khởi tạo xong.
 */
public class App25_SequentialFutures {

	public static void main(String[] args) throws InterruptedException{
		
		Vertx vertx = Vertx.vertx();
		FileSystem fs = vertx.fileSystem();
		
		Future<Void> startFuture = Future.future();

		Future<Void> fut1 = Future.future();
		fs.createFile("foo.txt", fut1.completer());
		
		// Future và Handler cùng xử lý kiểu <void>

/*		fut1.compose(new Handler<AsyncResult<Void>>() {
			public void handle(AsyncResult<Void> event) {
				
				if( event.succeeded()){
					System.out.println("succeeded");	
				}else if(event.failed()){
					System.out.println("failed");
				}
				
			};
		});*/

		fut1.compose(v -> {
		  // When the file is created (fut1), execute this:
		  Future<Void> fut2 = Future.future();
		  
		  Buffer buff = Buffer.buffer("happy = real - expected. find it inside you"); //
		  
		  //asynchronou write to file
		  fs.writeFile("foo.txt", buff, fut2);
		  return fut2;
		}).compose(v -> { //= fut2.compose()
		          // When the file is written (fut2), execute this:
		          fs.move("/foo", "/bar", startFuture);
		        },
		        // mark startFuture it as failed if any step fails.
		        startFuture);

	}

}
