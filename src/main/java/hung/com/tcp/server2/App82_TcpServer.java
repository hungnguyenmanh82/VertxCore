package hung.com.tcp.server2;

import hung.com.tcp.server.TcpServerVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;

/**
 * step1: create a new Vertx => a thread
 * step2: create and add a Vertical to Vertx =>  Vertical.start() will be call and run on worker thread
 */
public class App82_TcpServer {
	public static void main(String[] args) throws Exception{
		System.out.println("start main(): thread="+Thread.currentThread().getId());
		//get a new instance of Vertx => tương ứng 1 thread thì đúng hơn.
		Vertx vertx = Vertx.vertx();
		
		DeploymentOptions options = new DeploymentOptions()
				.setWorkerPoolName("*TcpServerThreadPool")
				.setWorkerPoolSize(4)  //thread for server, not client
				.setWorker(true);   //true: mỗi event đc assign 1 thread trong pool (các event độc lập, ko phụ thuộc nhau).
									//false: Standard-verticle sẽ ko dùng threadpool mà dùng eventloop tức dùng EventLoopPool của Vertx
		
		// event là event Open Socket nên độc lập nhau
		vertx.deployVerticle(new TcpServerVerticle_workerPool(), options);	
		
		
		// nếu undeploy(verticle) thì server hoặc socket trên đó sẽ tự động close (tài liệu Vertx chỉ ra vậy)
		// xem vd Vertx về undeploy()
	}
}
