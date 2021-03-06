package hung.com.app1_basicSample;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;

/**
 * vd: Asign Threadpool cho verticle
 *
 */
public class App152_VertxWorkerThreadPool {

	public static void main(String[] args) throws InterruptedException{
		System.out.println("main(): thread="+Thread.currentThread().getId() + ", ThreadName="+Thread.currentThread().getName());
		
		/**
		 * Vertx có 2 threadpool: 
		 *  + EventLoopPool Thread là cho standard Verticle
		 *  + workerPool Thread là cho blocking code
		 */
		VertxOptions option = new VertxOptions()
								.setEventLoopPoolSize(4)        //dùng cho Standard Verticle							
								.setWorkerPoolSize(4);         //dùng cho blocking code (ko phải Verticle)
		Vertx vertx = Vertx.vertx(option);
		
		//register Verticale with Vertex instance to capture event.
		vertx.deployVerticle(new MyVerticle()); //asynchronous call MyVerticle1.start() in worker thread
		
		
		//==================== verticle dùng Threadpool riêng ko dùng của Vertx =================
		// tức là mỗi lần Verticle nhận đc event nó sẽ lấy thread trong threadpool của nó, ko dùng chung threadpool với Vertx
		DeploymentOptions options = new DeploymentOptions()
				.setWorkerPoolName("*TcpServerThreadPool")   //WorkerPoolName là duy nhất, có thể dùng lại để share với các Verticle khác
				.setWorkerPoolSize(10)  //thread for server, not client
				.setWorker(true);   //true: worker-vertical dùng TcpServerThreadPool  (các event vẫn tuần tự, nhưng trên thread khác nhau)
									//false: Standard-verticle dùng vert.x-eventloop-thread (fix thread to verticle)
									//blockingCode luôn dùng TcpServerThreadPool
		
		vertx.deployVerticle(new MyVerticle(), options); //asynchronous call MyVerticle1.start() in worker thread
		
		
		Thread.currentThread().sleep(3000);
		
		//vertx.undeploy(DeploymentId) => DeploymentId đc cấp khi hàm Verticle.start() đc gọi
		//asynchronous function
		vertx.close();  //error: Vertical.stop() won't be called
		
		Thread.currentThread().sleep(3000); //wait for vertx.close() finished
	}

}
