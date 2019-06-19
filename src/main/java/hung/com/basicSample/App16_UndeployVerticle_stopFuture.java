package hung.com.basicSample;

import java.util.Set;

import hung.com.context.ContextOfVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.Context;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;

/**
Context: để chỉ 1 đơn vị quản lý tài nguyên Memory và threadpool luôn. 
Vertx context khác Verticle Context (đã test).

 Event, task thuộc context nào thì sẽ đc xử lý trên thread (or threadpool) của context đó.
blocking-code đc gọi ở Verticle nhưng thuộc Vertx Context nên run ở context của Vertx => dùng threadpool của Vertx.

Trong khi Event, task sinh ra ở Blocking-code lại thuộc context của Verticle tạo ra “blocking-code” => 
event hay task này sẽ chạy trên thread (or threadpool) của Verticle (ko chạy trên vertx context).

 */
public class App16_UndeployVerticle_stopFuture {

	public static void main(String[] args) throws InterruptedException{
		System.out.println("main(): thread="+Thread.currentThread().getId());
		//create a new instance Vertx => a worker thread
		Vertx vertx = Vertx.vertx();


		//register Verticale with Vertex instance to capture event.
		Verticle_stopFuture verticle = new Verticle_stopFuture();

		vertx.deployVerticle(verticle);

		// waiting for Verticle context is allocate by Vertx
		Thread.currentThread().sleep(500);
		
		Set<String> deploymentIDs = vertx.deploymentIDs();
		System.out.println("============== before undeploy, list of deploymentIDs: number Deployments =" + deploymentIDs.size());
		for(String depId: deploymentIDs){
			//
			System.out.println(depId);
		}
		
		//=================== undeploy() to stop Verticle ========================
		Context verticleContext = verticle.getRealContext();
		vertx.undeploy(verticleContext.deploymentID());
		
		Thread.currentThread().sleep(100);
		//
		deploymentIDs = vertx.deploymentIDs();
		System.out.println("============== after undeploy, list of deploymentIDs: number Deployments =" + deploymentIDs.size());
		for(String depId: deploymentIDs){
			//
			System.out.println(depId);
		}
		
		// app ko stop với Main() stop vì có 1 worker thread quản lý Vertx có loop bắt Event
		//vertx.close();

	}

}
