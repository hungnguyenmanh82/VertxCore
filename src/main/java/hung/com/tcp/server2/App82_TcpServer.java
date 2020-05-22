package hung.com.tcp.server2;

import hung.com.tcp.server.TcpServerVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;

/**
 * step1: create a new Vertx => a thread
 * step2: create and add a Vertical to Vertx =>  Vertical.start() will be call and run on worker thread
 */
public class App82_TcpServer {
	public static void main(String[] args) throws Exception{
		System.out.println("start main(): thread="+Thread.currentThread().getId());
		
		//eventloop chỉ dùng cho Standard Verticle thôi
		final VertxOptions vertxOptions = new VertxOptions().setEventLoopPoolSize(4);
		
		Vertx vertx = Vertx.vertx(vertxOptions);
		
		DeploymentOptions options = new DeploymentOptions()
				.setWorkerPoolName("*TcpServerThreadPool")
				.setWorkerPoolSize(4)  //thread for server, not client
				.setWorker(false);   //true: mỗi event đc assign 1 thread trong pool (các event độc lập, ko phụ thuộc nhau).
									//false: Standard-verticle sẽ ko dùng threadpool mà dùng eventloop tức dùng EventLoopPool của Vertx
		
		// event là event Open Socket nên độc lập nhau
		vertx.deployVerticle(new TcpServerVerticle2_newSockets(), options);	
		
		
		// nếu undeploy(verticle) thì server hoặc socket trên đó sẽ tự động close (tài liệu Vertx chỉ ra vậy)
		// xem vd Vertx về undeploy()
	}
}
