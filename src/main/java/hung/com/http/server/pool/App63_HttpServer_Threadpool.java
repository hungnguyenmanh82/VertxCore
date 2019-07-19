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
				.setWorkerPoolName("*HttpServerThreadPoolForConnect")  //tên là ID có thể dùng lại đc
				.setWorkerPoolSize(2)  //thread for server, not client
//				.setMultiThreaded(true)  //ko nên dùng cái này, vì multi thread đọc từ Handler Queue sẽ bị synchronize() ko hiệu quả
//				.setHa(true)         //option for cluster
				.setWorker(false);   //true: worker-Verticle các event vẫn tuần tự nhưng có thể đc assign thread khac trong Worker-pool ở trên
                					//false: Standard-verticle sẽ ko dùng threadpool mà dùng eventloop tức dùng EventLoopPool của Vertx

		
		vertx.deployVerticle(new HttpServerVerticle_Threadpool(),options); 		
	}
}
