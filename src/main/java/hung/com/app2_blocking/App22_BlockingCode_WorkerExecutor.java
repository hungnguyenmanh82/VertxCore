package hung.com.app2_blocking;

import io.vertx.core.Vertx;

/**
 * http://vertx.io/docs/vertx-core/java/#blocking_code
 * 
 *  Phần blocking code nằm ở BlockingVerticleWorkerExecutor class.
 *  
 *
 */
public class App22_BlockingCode_WorkerExecutor {

	public static void main(String[] args) throws InterruptedException{
		System.out.println("start main(): thread="+Thread.currentThread().getId());
		//create a new instance Vertx => a worker thread
		Vertx vertx = Vertx.vertx();

		//register Verticale with Vertex instance to capture event.
		vertx.deployVerticle(new BlockingVerticle_WorkerExecutor()); //asynchronous call MyVerticle1.start() in worker thread

		// app ko stop với Main() stop vì có 1 worker thread quản lý Vertx có loop bắt Event
		//vertx.close();
	}

}
