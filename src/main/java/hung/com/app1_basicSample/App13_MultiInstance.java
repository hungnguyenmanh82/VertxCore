package hung.com.app1_basicSample;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;

/**
 * vd: hỗ trợ chạy Multi Verticle instance dùng thread pool
 * Verticle: đc xem là các task. Nhiều Vertical có thể asign cho 1 thread trong thread pool.
 *
 */
public class App13_MultiInstance {

	public static void main(String[] args) throws InterruptedException{
		System.out.println("main(): thread="+Thread.currentThread().getId() + ", ThreadName="+Thread.currentThread().getName());
		
		//vertx là singleton
		Vertx vertx = Vertx.vertx();
		
		//Deploy Option chi hieu quả với Multi Instance
		DeploymentOptions options = new DeploymentOptions()
				.setInstances(6)          //create 6 instances of Verticles
				.setWorkerPoolName("WorkerPoolName")
				.setWorkerPoolSize(3)
				.setWorker(true); 	//true: worker-vertical dùng WorkerPoolName1  (các event vẫn tuần tự, nhưng trên thread khác nhau)
									//false: Standard-verticle dùng vert.x-eventloop-thread (fix thread to verticle)
									//blockingCode luôn dùng WorkerPoolName
		
		//register Verticale with Vertx instance to capture event.
		// 
		vertx.deployVerticle("hung.com.basicSample.MyVerticle",options);//asynchronous call MyVerticle.start() in worker thread

		Thread.currentThread().sleep(3000);

		//vertx.undeploy(DeploymentId) => DeploymentId đc cấp khi hàm Verticle.start() đc gọi
		//asynchronous function
		vertx.close();  //error: Vertical.stop() won't be called

		Thread.currentThread().sleep(3000); //wait for vertx.close() finished
	}

}
