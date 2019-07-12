package hung.com.basicSample;

import java.util.Set;

import io.vertx.core.Vertx;

/**
 * vd: Khởi tạo 1 vertical  < xem khái niệm vertical>
 *
 */
public class App11_DeploySingleVerticle {

	public static void main(String[] args) throws InterruptedException{
		System.out.println("start main(): thread="+Thread.currentThread().getId());
		
		//create a new instance Vertx => a worker thread sinh ra để quản lý loop Event, vì thế hàm main() kết thúc nhưng App ko stop
		// gọi vertx.close() để stop thread này
		//Ctrl+ T: để tìm implement Class sẽ thấy Vertx.vertx() sẽ new instance (ko phải Singleton)
		Vertx vertx = Vertx.vertx();
		
		//tạo 1 Verticle context và chạy trên Thread đc chỉ định bởi Vertx
		vertx.deployVerticle(new MyVerticle()); //asynchronous call MyVerticle1.start() in worker thread
		
		// waiting for Verticle context is allocate by Vertx
		Thread.currentThread().sleep(500);
		
		Set<String> deploymentIDs = vertx.deploymentIDs();
		System.out.println("==============  (sleeped 500ms wait for Context allocated), list of deploymentIDs: number Deployments =" + deploymentIDs.size());
		for(String depId: deploymentIDs){
			//
			System.out.println(depId);
		}
		
		//vertx.undeploy(DeploymentId) => DeploymentId đc cấp khi hàm Verticle.start() đc gọi
		//asynchronous function
		vertx.close();  //error: Vertical.stop() won't be called
		
		Thread.currentThread().sleep(3000); //wait for vertx.close() finished
	}

}
