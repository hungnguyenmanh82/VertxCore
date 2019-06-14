package hung.com.http.server;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;

/**
 * 
 */
public class App62_HttpServerHandler {
	public static void main(String[] args) {
		System.out.println("start main(): thread="+Thread.currentThread().getId());
		//get an instance of Vertx => tương ứng 1 thread thì đúng hơn.

		Vertx vertx1 = Vertx.vertx();
		//asynchronous call MyVerticle1.start() in worker thread
		vertx1.deployVerticle(new HttpServerVerticle(), new Handler<AsyncResult<String>>(){
			//hàm này đc gọi sau khi hàm HttpServerVerticle.start() trả về giá trị (lưu ý Future.complete() nếu dùng asynchronous start() )
			@Override
			public void handle(AsyncResult<String> stringAsyncResult) {
				System.out.println(" handler => vertx.deployVerticle(): thread="+Thread.currentThread().getId());
			}
		});
	}
}
