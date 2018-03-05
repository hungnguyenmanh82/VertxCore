package hung.com.basicSample;

import hung.com.http.server.VertxHttpServerVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;

/**
 * 
 */
public class MultiVertxHttpServerMain {
	public static void main(String[] args) {
		System.out.println("start main(): thread="+Thread.currentThread().getId());
		//get an instance of Vertx => tương ứng 1 thread thì đúng hơn.
		Vertx vertx = Vertx.vertx();
		vertx.deployVerticle(new VertxHttpServerVerticle()); //asynchronous call MyVerticle1.start() in worker thread

		//================== Vertx2==================
		Vertx vertx1 = Vertx.vertx();
		vertx1.deployVerticle(new VertxHttpServerVerticle(), new Handler<AsyncResult<String>>(){
			//hàm này trigger khi VertxHttpServerVerticle.start(future) hoàn thành asynchronous
			@Override
			public void handle(AsyncResult<String> stringAsyncResult) {
				System.out.println(" handler => vertx.deployVerticle(): thread="+Thread.currentThread().getId());
			}
		});
	}
}
