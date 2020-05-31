package hung.com.tcp.server2;

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
				.setInstances(1)					// 3 TCP server on 3 independent eventLoop threads sharing the same (address+port)
				.setWorkerPoolName("*TcpServerThreadPool")
				.setWorkerPoolSize(4)  //thread for server, not client
				.setWorker(false);   //true: mỗi event đc assign 1 thread trong pool (các event độc lập, ko phụ thuộc nhau).
									//false: Standard-verticle sẽ ko dùng threadpool mà dùng eventloop tức dùng EventLoopPool của Vertx
		
		/**
		 * mỗi TCP server run tren 1 fix thread của EventLoop pool
		 * Nhiều TCP server có thể chung 1 fix thread  => rất khó để phân phối nó ra 2 core khác nhau
		 */
		vertx.deployVerticle("hung.com.tcp.server2.TcpServerVerticle2_newSockets", options);	
		
		// nếu undeploy(verticle) thì server hoặc socket trên đó sẽ tự động close (tài liệu Vertx chỉ ra vậy)
		// xem vd Vertx về undeploy()
	}
}
