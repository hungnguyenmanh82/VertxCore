package hung.com.http.server;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;

/**
 * step1: create a new Vertx => a thread
 * step2: create and add a Vertical to Vertx =>  Vertical.start() will be call and run on worker thread
 */
public class App61_HttpServer {
	public static void main(String[] args) {
		System.out.println("start main(): thread="+Thread.currentThread().getId());
	
		final VertxOptions vertxOptions = new VertxOptions().setEventLoopPoolSize(4);
		Vertx vertx = Vertx.vertx(vertxOptions);
		
		DeploymentOptions options = new DeploymentOptions()
				.setWorkerPoolName("WorkerPoolName1")
				.setWorkerPoolSize(2)  //thread for server, not client
				.setWorker(false);   //true: worker-vertical dùng WorkerPoolName1  (các event vẫn tuần tự, nhưng trên thread khác nhau)
									//false: Standard-verticle dùng vert.x-eventloop-thread (fix thread to verticle)
									//blockingCode luôn dùng WorkerPoolName1

		
		vertx.deployVerticle(new HttpServerVerticle(),options); 		

	}
}
