package hung.com.basicSample;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;

/**
 * vd: Khởi tạo 1 vertical  < xem khái niệm vertical>
 *
 */
public class App15_VertxThreadPool {

	public static void main(String[] args) throws InterruptedException{
		System.out.println("start main(): thread="+Thread.currentThread().getId());
		
		//lưu ý threadpool của vertx có thể dùng chung khi deploy nhiều Verticle
		// hoặc dùng rieng cho từng Verticle đc (add vào lúc deploy Verticle).
		// add threadpool lúc Deploy Verticle sẽ có nhiều tùy nhọn hơn
		// 1 vertx quản lý nhiều Verticle
		// Nếu ko tạo threadpool thì mỗi lần DeployVerticle, hoặc execute Block code nó lại khởi tạo 1 thread mới.
		VertxOptions option = new VertxOptions().setWorkerPoolSize(4);
		Vertx vertx = Vertx.vertx(option);
		
		//register Verticale with Vertex instance to capture event.
		vertx.deployVerticle(new MyVerticle1()); //asynchronous call MyVerticle1.start() in worker thread
		
		
		//==================== verticle dùng Threadpool riêng ko dùng của Vertx =================
		// tức là mỗi lần Verticle nhận đc event nó sẽ lấy thread trong threadpool của nó, ko dùng chung threadpool với Vertx
		DeploymentOptions options = new DeploymentOptions()
				.setWorkerPoolName("*TcpServerThreadPool")
				.setWorkerPoolSize(10)  //thread for server, not client
				.setWorker(true);   //true: mỗi event đc assign 1 thread trong pool (các event độc lập, ko phụ thuộc nhau).
		
		vertx.deployVerticle(new MyVerticle1(), options); //asynchronous call MyVerticle1.start() in worker thread
		
		
		Thread.currentThread().sleep(3000);
		
		//vertx.undeploy(DeploymentId) => DeploymentId đc cấp khi hàm Verticle.start() đc gọi
		//asynchronous function
		vertx.close();  //error: Vertical.stop() won't be called
		
		Thread.currentThread().sleep(3000); //wait for vertx.close() finished
	}

}
