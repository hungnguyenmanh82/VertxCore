package hung.com.app3_context;

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
public class ContextVerticle1 extends AbstractVerticle {

	@Override
	public void start(Future<Void> startFuture) throws Exception {	
		//hàm này phải đc gọi để xác định quá trình Deploy thành công
		// nếu ko phải gọi hàm startFuture.complete()
		super.start(startFuture);   
		
		System.out.println("ContextVerticle1.start(): thread="+Thread.currentThread().getId() + ", ThreadName="+Thread.currentThread().getName());

		//vertx.getOrCreateContext() sẽ trả về context gắn với Thread hiện tại Thread.currentThread()
		// convert Current Thread => Context và trả về => hầu hết các thư viện dùng theo cách này
		// Verticle.start() luôn chạy trên thread của Verticle context hiện tại nên sẽ trả về Verticle context	
		Context context = vertx.getOrCreateContext();
		if (context.isEventLoopContext()) {
			System.out.println("ContextVerticle1: Context attached to Event Loop: deploymentId= "+ context.deploymentID());
		} else if (context.isWorkerContext()) {
			System.out.println("ContextVerticle1: Context attached to Worker Thread: deploymentId= "+ context.deploymentID());
		} else if (context.isMultiThreadedWorkerContext()) {
			System.out.println("ContextVerticle1: Context attached to Worker Thread - multi threaded worker: deploymentId= "+ context.deploymentID());
		} else if (! Context.isOnVertxThread()) {
			System.out.println("ContextVerticle1: Context not attached to a thread managed by vert.x: deploymentId= "+ context.deploymentID());
		}

		//Future<String>  => String là giá trị trả về của Future 
		//BlockingHanderler: thuộc Vertx context => chạy trên threadpool của Vertx context
		//bất key Event, hay task nào tạo ra trong Blocking code đều thuộc quản lý của Context hiện tại => đều run trên thread của Verticle
		//Trong khi Event, task sinh ra ở Blocking-code lại thuộc context của Verticle tạo ra “blocking-code” => 
		//event hay task này sẽ chạy trên thread (or threadpool) của Verticle (ko chạy trên vertx context).
		Handler blockingHandler = new Handler<Future<String>>() {

			//Future này quản lý bởi Vertx, ko phải Verticle
			@Override
			public void handle(Future<String> future) {
				System.out.println("*************blockingHandler1: thread="+Thread.currentThread().getId() + ",ThreadName="+Thread.currentThread().getName());
				Context context1 = vertx.getOrCreateContext();  //vẫn cùng context với Verticle 
				if (context1.isEventLoopContext()) {
					System.out.println("blockingHandler1: Context attached to Event Loop: deploymentId= "+ context1.deploymentID());
				} else if (context1.isWorkerContext()) {
					System.out.println("blockingHandler1: Context attached to Worker Thread: deploymentId= "+ context1.deploymentID());
				} else if (context1.isMultiThreadedWorkerContext()) {
					System.out.println("blockingHandler1: Context attached to Worker Thread - multi threaded worker: deploymentId= "+ context1.deploymentID());
				} else if (! Context.isOnVertxThread()) {
					System.out.println("blockingHandler1: Context not attached to a thread managed by vert.x: "+ context1.deploymentID());
				}
				//
				String result = "MyVerticle1.start(): thread="+Thread.currentThread().getId();
				future.complete(result);  //sẽ gửi event tới context của Verticle

			}

		};

		//run on thread of this Verticle
		Handler returnHandler =  new Handler<AsyncResult<String>>() {
			public void handle(AsyncResult<String> event) {
				System.out.println("returnHandler1: thread=" + Thread.currentThread().getId()+ 
						", result=" + event.result());
				
				System.out.println("ThreadName="+Thread.currentThread().getName());
			};
		};

		//order = false => sẽ có ưu tiên cao hơn. chạy trên thread độc lập ko quan tâm order trong queue
		//by default (function 2 tham số): order = true => chạy theo order trong queue 
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
		super.stop(stopFuture);
		System.out.println("ContextVerticle1.stop(): thread=" + Thread.currentThread().getId());
	}

}
