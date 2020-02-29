package hung.com.app2_blocking;

import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;

/**
 * http://vertx.io/docs/vertx-core/java/#blocking_code
 * 
 *  Phần blocking code nằm ở BlockingVerticle class.
 *  
 *
 */
public class App21_BlockingCode {

	public static void main(String[] args) throws InterruptedException{
		System.out.println("start main(): thread="+Thread.currentThread().getId());
		
		//create a new instance Vertx => a worker thread
		VertxOptions vertxOptions = new VertxOptions().setWorkerPoolSize(4)  // threadPool của blocking code 
														.setEventLoopPoolSize(4);  //threadpool của EventLoop cho standard Verticle
		Vertx vertx = Vertx.vertx(vertxOptions);

		//register Verticale with Vertex instance to capture event.
		vertx.deployVerticle(new BlockingVerticle()); //asynchronous call MyVerticle1.start() in worker thread

		// app ko stop với Main() stop vì có 1 worker thread quản lý Vertx có loop bắt Event
		//vertx.close();
	}

}
