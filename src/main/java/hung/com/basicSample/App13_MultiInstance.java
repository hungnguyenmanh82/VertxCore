package hung.com.basicSample;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;

/**
 * vd: hỗ trợ chạy Multi Verticle instance dùng thread pool
 * Vertical: đc xem là các task. Nhiều Vertical có thể asign cho 1 thread trong thread pool.
 *
 */
public class App13_MultiInstance {

	public static void main(String[] args) throws InterruptedException{
		System.out.println("start main(): thread="+Thread.currentThread().getId());
		//create a new instance Vertx => a worker thread
		Vertx vertx = Vertx.vertx();

		DeploymentOptions options = new DeploymentOptions()
				.setInstances(6)          //create 6 instances of Verticals
				.setWorkerPoolName("abc")
				.setWorkerPoolSize(3)
				.setWorker(true);
		
		//register Verticale with Vertex instance to capture event.
		vertx.deployVerticle("hung.com.basicSample.MyVerticle1",options);//asynchronous call MyVerticle1.start() in worker thread

		Thread.currentThread().sleep(3000);

		//vertx.undeploy(DeploymentId) => DeploymentId đc cấp khi hàm Verticle.start() đc gọi
		//asynchronous function
		vertx.close();  //error: Vertical.stop() won't be called

		Thread.currentThread().sleep(3000); //wait for vertx.close() finished
	}

}
