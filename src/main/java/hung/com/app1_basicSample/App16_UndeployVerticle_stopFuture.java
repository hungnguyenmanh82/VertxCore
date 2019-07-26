package hung.com.app1_basicSample;

import java.util.Set;

import hung.com.app3_context.ContextOfVerticle;
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
		System.out.println("============== before undeploy (sleeped 500ms wait for Context allocated), list of deploymentIDs: number Deployments =" + deploymentIDs.size());
		for(String depId: deploymentIDs){
			//
			System.out.println(depId);
		}
		
		//=================== undeploy() to stop Verticle ========================
		// undeploy() gọi từ thread khác
		// nếu gọi bên trong Verticl thì dùng hàm verticle.stop()
		Context verticleContext = verticle.getRealContext();
		vertx.undeploy(verticleContext.deploymentID());
		
		Thread.currentThread().sleep(100);
		//
		deploymentIDs = vertx.deploymentIDs();
		System.out.println("============== after undeploy then sleep 100ms, list of deploymentIDs: number Deployments =" + deploymentIDs.size());
		for(String depId: deploymentIDs){
			//
			System.out.println(depId);
		}
		
		// app ko stop với Main() stop vì có 1 worker thread quản lý Vertx có loop bắt Event
		//vertx.close();
		
		Thread.currentThread().sleep(1000);
		
		//============================ test verticle stop or not after undeploy() called =========================
		verticleContext.runOnContext(new Handler<Void>() {			
			@Override
			public void handle(Void event) {
				System.out.println("****Handler run on Context of verticleContext : thread="+Thread.currentThread().getId());
				System.out.println("verticle Context was not released");
				
			}
		});
		

	}

}
