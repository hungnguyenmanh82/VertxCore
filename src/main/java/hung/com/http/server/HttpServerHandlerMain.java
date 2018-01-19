package hung.com.http.server;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;

/**
 * 
 */
public class HttpServerHandlerMain {
	public static void main(String[] args) {
		System.out.println("start main(): thread="+Thread.currentThread().getId());
		//get an instance of Vertx => tương ứng 1 thread thì đúng hơn.

		Vertx vertx1 = Vertx.vertx();
		//asynchronous call MyVerticle1.start() in worker thread
		vertx1.deployVerticle(new VertxHttpServerVerticle(), new Handler<AsyncResult<String>>(){
			@Override
			public void handle(AsyncResult<String> stringAsyncResult) {
				System.out.println(" handler => vertx.deployVerticle(): thread="+Thread.currentThread().getId());
			}
		});
	}
}
