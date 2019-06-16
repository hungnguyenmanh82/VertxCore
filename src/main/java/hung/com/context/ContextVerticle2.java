package hung.com.context;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.Context;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;

/**
 * 
Vertical:  được hiểu như là 1 đối tượng (đơn vị quản lý tài nguyên) 
   by default: vertical sẽ đc asign thread để đảm bảo tài nguyên ko bị tranh chấp giữa 2 thread.
    1 Thời điểm chỉ 1 thread đc assyn truy cập tài nguyên vertical
   //=======
   Các Vertical tương tác với nhau qua cơ chế message (có thể tương tác với ngoài), 
   chứa các function callback để nhận event đăng ký với Vertx. 
   Và gửi event (or message) tới Vertx.  
   //=======
   VertX sẽ quản lý cấp phát thread cho Vertical từ thread pool.  
   1 thread có thể dùng lại cho nhiều vertical

 */
public class ContextVerticle2 extends AbstractVerticle {

	@Override
	public void start(Future<Void> startFuture) throws Exception {	

		System.out.println("MyVerticle.start(): thread="+Thread.currentThread().getId());

		// context của Verticle khác Vertx Context
		// các verticle khác nhau thì context khác nhau
		Context context = vertx.getOrCreateContext();
		if (context.isEventLoopContext()) {
			System.out.println("Verticle2: Context attached to Event Loop: "+ context.deploymentID());
		} else if (context.isWorkerContext()) {
			System.out.println("Verticle2: Context attached to Worker Thread: "+ context.deploymentID());
		} else if (context.isMultiThreadedWorkerContext()) {
			System.out.println("Verticle2: Context attached to Worker Thread - multi threaded worker: "+ context.deploymentID());
		} else if (! Context.isOnVertxThread()) {
			System.out.println("Verticle2: Context not attached to a thread managed by vert.x: "+ context.deploymentID());
		}

		//Future<String>  => String là giá trị trả về của Future 
		//BlockingHanderler: thuộc Vertx context => chạy trên threadpool của Vertx context
		//bất key Event, hay task nào tạo ra trong Blocking code đều thuộc quản lý của Context hiện tại => đều run trên thread của Verticle
		//Trong khi Event, task sinh ra ở Blocking-code lại thuộc context của Verticle tạo ra “blocking-code” => 
		//event hay task này sẽ chạy trên thread (or threadpool) của Verticle (ko chạy trên vertx context).
		Handler blockingHandler = new Handler<Future<String>>() {
			public String test = "abc";
			//Future này quản lý bởi Vertx, ko phải Verticle
			@Override
			public void handle(Future<String> future) {
				System.out.println("***blockingHandler2: thread="+Thread.currentThread().getId());
				//vẫn cùng context với Verticle 
				Context context1 = vertx.getOrCreateContext();
				if (context1.isEventLoopContext()) {
					System.out.println("blockingHandler2: Context attached to Event Loop: "+ context1.deploymentID());
				} else if (context1.isWorkerContext()) {
					System.out.println("blockingHandler2: Context attached to Worker Thread: "+ context1.deploymentID());
				} else if (context1.isMultiThreadedWorkerContext()) {
					System.out.println("blockingHandler2: Context attached to Worker Thread - multi threaded worker: "+ context1.deploymentID());
				} else if (! Context.isOnVertxThread()) {
					System.out.println("blockingHandler2: Context not attached to a thread managed by vert.x: "+ context1.deploymentID());
				}
				//
				String result = "MyVerticle.start(): thread="+Thread.currentThread().getId();
				future.complete(result);  //sẽ gửi event tới context của Verticle
				System.out.println(test);
			}

		};

		//run on thread of this Verticle
		Handler returnHandler =  new Handler<AsyncResult<String>>() {
			public void handle(AsyncResult<String> event) {
				System.out.println("returnHandler: thread=" + Thread.currentThread().getId()+
						", result=" + event.result());
			};
		};

		vertx.executeBlocking(blockingHandler,false ,returnHandler);

		//Java lambda syntax
		/*		vertx.executeBlocking(future -> {
			  // Call some blocking API that takes a significant amount of time to return
			  String result = "hello";
			  future.complete(result);
			}, res -> {
			  System.out.println("The result is: " + res.result());
			});*/
	}

	@Override
	public void stop(Future<Void> stopFuture) throws Exception {
		System.out.println("MyVerticle.stop(): thread=" + Thread.currentThread().getId());
	}

}
