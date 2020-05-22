package hung.com.tcp.server;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;

/**
 * step1: create a new Vertx => a thread
 * step2: create and add a Vertical to Vertx =>  Vertical.start() will be call and run on worker thread
 */
public class App81_TcpServer {
	public static void main(String[] args) throws Exception {
		System.out.println("start main(): thread="+Thread.currentThread().getId());
		
		//eventloop chỉ dùng cho Standard Verticle thôi
		final VertxOptions vertxOptions = new VertxOptions().setEventLoopPoolSize(4);
		
		Vertx vertx = Vertx.vertx(vertxOptions);
		
	
		// event là event Open Socket nên độc lập nhau
		vertx.deployVerticle(new TcpServerVerticle());	
		
		
		// nếu undeploy(verticle) thì server hoặc socket trên đó sẽ tự động close (tài liệu Vertx chỉ ra vậy)
		// xem vd Vertx về undeploy()
	}
}
