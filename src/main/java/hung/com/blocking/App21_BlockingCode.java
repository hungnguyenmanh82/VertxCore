package hung.com.blocking;

import io.vertx.core.Vertx;

/**
 * http://vertx.io/docs/vertx-core/java/#blocking_code
 * 
 *  Phần blocking code nằm ở BlockingVerticle class.
 *  
 *
 */
public class App21_BlockingCode {

	public static void main(String[] args) throws InterruptedException{
		System.out.println("start main(): thread="+Thread.currentThread().getId());
		//create a new instance Vertx => a worker thread
		Vertx vertx = Vertx.vertx();
		
		//register Verticale with Vertex instance to capture event.
		vertx.deployVerticle(new BlockingVerticle()); //asynchronous call MyVerticle1.start() in worker thread
		
		Thread.currentThread().sleep(3000);
		
		//vertx.undeploy(DeploymentId) => DeploymentId đc cấp khi hàm Verticle.start() đc gọi
		//asynchronous function
		vertx.close();  //error: Vertical.stop() won't be called
		
		Thread.currentThread().sleep(3000); //wait for vertx.close() finished
	}

}
