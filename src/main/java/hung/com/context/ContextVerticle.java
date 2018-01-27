package hung.com.context;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.Context;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;

/**
 * 
Vertical:  được hiểu như là 1 đối tượng tương tác. 
   Các Vertical tương tác với nhau qua cơ chế message (có thể tương tác với ngoài), 
   chứa các function callback để nhận event đăng ký với Vertx. 
   Và gửi event (or message) tới Vertx.  
   VertX sẽ quản lý cấp phát thread cho Vertical => có vẻ như thread pool. 
   Chứ thread ko găn cố định với Vertical.  1 thread có thể dùng lại cho nhiều vertical.

 */
public class ContextVerticle extends AbstractVerticle {

	@Override
	public void start(Future<Void> startFuture) throws Exception {	

		System.out.println("MyVerticle.start(): thread="+Thread.currentThread().getId());

		Context context = vertx.getOrCreateContext();
		if (context.isEventLoopContext()) {
			System.out.println("Verticle: Context attached to Event Loop: "+ context.deploymentID());
		} else if (context.isWorkerContext()) {
			System.out.println("Verticle: Context attached to Worker Thread: "+ context.deploymentID());
		} else if (context.isMultiThreadedWorkerContext()) {
			System.out.println("Verticle: Context attached to Worker Thread - multi threaded worker: "+ context.deploymentID());
		} else if (! Context.isOnVertxThread()) {
			System.out.println("Verticle: Context not attached to a thread managed by vert.x: "+ context.deploymentID());
		}

		//Future<String>  => String là giá trị trả về của Future 
		//run on another thread
		Handler blockingHandler = new Handler<Future<String>>() {
			public String test = "abc";
			//Future này quản lý bởi Vertx, ko phải Verticle
			@Override
			public void handle(Future<String> future) {
				System.out.println("***blockingHandler: thread="+Thread.currentThread().getId());
				Context context1 = vertx.getOrCreateContext();  //vẫn cùng context với Verticle 
				if (context1.isEventLoopContext()) {
					System.out.println("blockingHandler: Context attached to Event Loop"+ context1.deploymentID());
				} else if (context1.isWorkerContext()) {
					System.out.println("blockingHandler: Context attached to Worker Thread"+ context1.deploymentID());
				} else if (context1.isMultiThreadedWorkerContext()) {
					System.out.println("blockingHandler: Context attached to Worker Thread - multi threaded worker"+ context1.deploymentID());
				} else if (! Context.isOnVertxThread()) {
					System.out.println("blockingHandler: Context not attached to a thread managed by vert.x"+ context1.deploymentID());
				}
				//
				String result = "MyVerticle.start(): thread="+Thread.currentThread().getId();
				future.complete(result);
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