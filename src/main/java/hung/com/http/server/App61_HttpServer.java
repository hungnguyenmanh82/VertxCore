package hung.com.http.server;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;

/**
 * step1: create a new Vertx => a thread
 * step2: create and add a Vertical to Vertx =>  Vertical.start() will be call and run on worker thread
 */
public class App61_HttpServer {
	public static void main(String[] args) {
		System.out.println("start main(): thread="+Thread.currentThread().getId());
		//get a new instance of Vertx => tương ứng 1 thread thì đúng hơn.
		Vertx vertx = Vertx.vertx();
		
		DeploymentOptions options = new DeploymentOptions()
				.setWorkerPoolName("*TcpServerThreadPool")
				.setWorkerPoolSize(2)  //thread for server, not client
				.setWorker(false);   //true: worker-Verticle mỗi event đc assign 1 thread trong worker-pool (các event độc lập, ko phụ thuộc nhau).
								//false: Standard-verticle sẽ ko dùng threadpool mà dùng eventloop tức dùng EventLoopPool của Vertx

		
		vertx.deployVerticle(new HttpServerVerticle(),options); 		

	}
}
