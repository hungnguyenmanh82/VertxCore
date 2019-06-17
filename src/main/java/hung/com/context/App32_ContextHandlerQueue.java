package hung.com.context;

import io.vertx.core.AsyncResult;
import io.vertx.core.Context;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.impl.VertxThreadFactory;

/**
Context: để chỉ 1 đơn vị quản lý tài nguyên Memory và threadpool luôn. 
Vertx context khác Verticle Context (đã test).

 Event, task thuộc context nào thì sẽ đc xử lý trên thread (or threadpool) của context đó.
blocking-code đc gọi ở Verticle nhưng thuộc Vertx Context nên run ở context của Vertx => dùng threadpool của Vertx.

Trong khi Event, task sinh ra ở Blocking-code lại thuộc context của Verticle tạo ra “blocking-code” => 
event hay task này sẽ chạy trên thread (or threadpool) của Verticle (ko chạy trên vertx context).

 */
public class App32_ContextHandlerQueue {

	public static void main(String[] args) throws InterruptedException{
		System.out.println("main(): thread="+Thread.currentThread().getId());
		//create a new instance Vertx => a worker thread
		Vertx vertx = Vertx.vertx();
		
		
		ContextHandlerQueueVerticle verticle = new ContextHandlerQueueVerticle();
		
		vertx.deployVerticle(verticle, new Handler<AsyncResult<String>>() {
			
			@Override
			public void handle(AsyncResult<String> asyncResult) {
				if (asyncResult.succeeded()) {
					//Deployment id do Vertx cấp => cái này hơi stupid
					// vertx.undeploy(DeploymentId)
					//(sao ko dùng pointer luôn giống với android LocalBroadCastManager)
					System.out.println("Deployment id is: " + asyncResult.result());
				} else {
					System.out.println("Deployment failed!");  //vì chưa đc cấp id
				}
				
			}
		}); 
		
		Thread.currentThread().sleep(200); //chờ Verticle khởi tạo thành công
		
		//================================= case 1: context từ Vertx
		//Handler đc add trên Thread của Context nào thì sẽ chạy trên Threadpool của context ấy
		// trường hợp Thread ko thuộc Verticle nào thì sẽ do Vertx chỉ định từ Threadpool của nó
		// code này ko run trên thread của verticle, mà của Vertx
		vertx.runOnContext(new Handler<Void>() {
			
			@Override
			public void handle(Void event) {
				System.out.println("case1: run Handler on Context of Vertx: thread="+Thread.currentThread().getId());
				
			}
		});
		
		//================================= case2: context từ verticle
		//Handler đc add trên Thread của Context nào thì sẽ chạy trên Threadpool của context ấy
		// trường hợp Thread ko thuộc Verticle nào thì sẽ do Vertx chỉ định từ Threadpool của nó
		// code này ko run trên thread của verticle, mà của Vertx
		verticle.getVertx().getOrCreateContext().runOnContext(new Handler<Void>() {
			
			@Override
			public void handle(Void event) {
				System.out.println("case21: run Handler on Context of Verticle: thread="+Thread.currentThread().getId());
				
			}
		});
		
		verticle.getVertx().getOrCreateContext().runOnContext(new Handler<Void>() {
			
			@Override
			public void handle(Void event) {
				System.out.println("case22: run Handler on Context of Verticle: thread="+Thread.currentThread().getId());
			}
		});
		
		
		
		// app ko stop với Main() stop vì có 1 worker thread quản lý Vertx có loop bắt Event
	    //vertx.close();
	}

}
