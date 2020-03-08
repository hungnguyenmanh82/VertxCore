package hung.com.app1_basicSample;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;

/**
 * vd: Asign Threadpool cho verticle
 *
 */
public class App152_VertxEventLoopPoolSize {

	public static void main(String[] args) throws InterruptedException{
		System.out.println("main(): thread="+Thread.currentThread().getId() + ", ThreadName="+Thread.currentThread().getName());
		
		//eventloop chỉ dùng cho Standard Verticle thôi
		final VertxOptions vertxOptions = new VertxOptions().setEventLoopPoolSize(4);
		
		Vertx vertx = Vertx.vertx(vertxOptions);
		
		//register Verticale with Vertex instance to capture event.
		vertx.deployVerticle(new MyVerticle()); //asynchronous call MyVerticle1.start() in worker thread
		
		vertx.deployVerticle(new MyVerticle()); 
		vertx.deployVerticle(new MyVerticle()); 
		vertx.deployVerticle(new MyVerticle()); 
		vertx.deployVerticle(new MyVerticle()); 
		vertx.deployVerticle(new MyVerticle()); 
		vertx.deployVerticle(new MyVerticle()); 
		vertx.deployVerticle(new MyVerticle()); 
		
		
		Thread.currentThread().sleep(3000);
		
		//vertx.undeploy(DeploymentId) => DeploymentId đc cấp khi hàm Verticle.start() đc gọi
		//asynchronous function
		vertx.close();  //error: Vertical.stop() won't be called
		
		Thread.currentThread().sleep(3000); //wait for vertx.close() finished
	}

}
