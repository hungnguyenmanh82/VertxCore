package hung.com.context;

import java.util.Set;

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
public class App34_ContextOfVerticle_fromOtherThread {

	public static void main(String[] args) throws InterruptedException{
		System.out.println("main(): thread="+Thread.currentThread().getId());
		//create a new instance Vertx => a worker thread
		Vertx vertx = Vertx.vertx();



		//register Verticale with Vertex instance to capture event.
		ContextOfVerticle verticle = new ContextOfVerticle();
		// nếu chưa DeployVerticle() thì Context = null 
		//		String deployId = verticle.getRealContext().deploymentID();
		//		System.out.println("main: deployId =" + deployId);
		vertx.deployVerticle(verticle);


		// waiting for Verticle context is allocate by Vertx
		Thread.currentThread().sleep(100);
		
		Set<String> deploymentIDs = vertx.deploymentIDs();
		System.out.println("main: list of deploymentIDs: number Deployments =" + deploymentIDs.size());
		for(String depId: deploymentIDs){
			//
			System.out.println(depId);
		}
		
		//nếu verticle.start() chưa đc gọi thì Context = NULL
		Context verticleContext = verticle.getRealContext();
		
		//================================== risk ==================================================
		//lệnh này sẽ add Handler truc tiep vào Vertical context queue mà ko add context của CurrentThread
		// vertx context queue dùng synchronize(object) để bảo về queue => nên performance ko tốt
		verticleContext.runOnContext(new Handler<Void>() {
			
			@Override
			public void handle(Void event) {
				System.out.println("****Handler run on Context of verticleContext : thread="+Thread.currentThread().getId());
				System.out.println(" deploymentId="+vertx.getOrCreateContext().deploymentID());
				
			}
		});
		// app ko stop với Main() stop vì có 1 worker thread quản lý Vertx có loop bắt Event
		//vertx.close();

	}

}
