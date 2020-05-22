package hung.com.http.server.pool;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;

/**
 * step1: create a new Vertx => a thread
 * step2: create and add a Vertical to Vertx =>  Vertical.start() will be call and run on worker thread
 */
public class App63_HttpServer_Threadpool {
	public static void main(String[] args) {
		System.out.println("start main(): thread="+Thread.currentThread().getId());
		//get a new instance of Vertx => tương ứng 1 thread thì đúng hơn.
		Vertx vertx = Vertx.vertx();
		
		DeploymentOptions options = new DeploymentOptions()
				.setWorkerPoolName("WorkerPoolName1")  //tên là ID có thể dùng lại đc
				.setWorkerPoolSize(2)  // chỉ dùng khi là worker-verticle
//				.setMultiThreaded(true)  //ko nên dùng cái này, vì multi thread đọc từ Handler Queue sẽ bị synchronize() ko hiệu quả
//				.setHa(true)         //option for cluster
				.setWorker(false);   //true: worker-verticle dùng WorkerPoolName1  (các event vẫn tuần tự, nhưng trên thread khác nhau)
									//false: Standard-verticle dùng vert.x-eventloop-thread (fix thread to verticle)
									//blockingCode luôn dùng WorkerPoolName1

		
		vertx.deployVerticle(new HttpServerVerticle_Threadpool(),options); 		
	}
}
