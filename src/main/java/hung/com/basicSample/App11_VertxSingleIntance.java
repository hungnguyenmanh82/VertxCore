package hung.com.basicSample;

import io.vertx.core.Vertx;

/**
 * vd: Khởi tạo 1 vertical  < xem khái niệm vertical>
 *
 */
public class App11_VertxSingleIntance {

	public static void main(String[] args) throws InterruptedException{
		System.out.println("start main(): thread="+Thread.currentThread().getId());
		//create a new instance Vertx => a worker thread
		Vertx vertx = Vertx.vertx();
		//register Verticale with Vertex instance to capture event.
		vertx.deployVerticle(new MyVerticle1()); //asynchronous call MyVerticle1.start() in worker thread
		
		Thread.currentThread().sleep(3000);
		
		//vertx.undeploy(DeploymentId) => DeploymentId đc cấp khi hàm Verticle.start() đc gọi
		//asynchronous function
		vertx.close();  //error: Vertical.stop() won't be called
		
		Thread.currentThread().sleep(3000); //wait for vertx.close() finished
	}

}
