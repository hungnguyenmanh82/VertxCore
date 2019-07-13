package hung.com.http.server;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;

/**
 * step1: create a new Vertx => a thread
 * step2: create and add a Vertical to Vertx =>  Vertical.start() will be call and run on worker thread
 */
public class App64_HttpServer4_eventLoopPoolSize {
	public static void main(String[] args) {
		System.out.println("start main(): thread="+Thread.currentThread().getId());
		//get a new instance of Vertx => tương ứng 1 thread thì đúng hơn.
		
		//eventloop chỉ dùng cho Standard Verticle thôi
		final VertxOptions vertxOptions = new VertxOptions().setEventLoopPoolSize(4);
		
		Vertx vertx = Vertx.vertx(vertxOptions);	
		
		// 1 standard verticle có 1 eventloop và attach với duy nhất 1 thread trong EventLoopPool.
		// Đây là hạn chế khi dùng httpServer (ko config dc multi thread giống tcpServer)
		vertx.deployVerticle(new HttpServerVerticle()); //standard verticle use event loop 		

	}
}
