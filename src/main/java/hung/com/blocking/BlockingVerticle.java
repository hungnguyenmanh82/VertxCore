package hung.com.blocking;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
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
   //=============
   blocking execute code: Là 1 đoạn code đc Verticle trigger để chạy Asynchronous với Verticle (chạy song song).
    Sau khi chạy xong đoạn code này nó sinh event gửi tới Verticle qua Vertx.  

 */
public class BlockingVerticle extends AbstractVerticle {

	@Override
	public void start(Future<Void> startFuture) throws Exception {	
//		this.context;  //quản lý tất cả tài nguyên của Verticle
//		this.context.isWorkerContext()
//		this.context.isMultiThreadedWorkerContext()
		System.out.println("MyVerticle.start(): thread="+Thread.currentThread().getId());
		
		
		//BlockingHanderler: là 1 đoạn code đc chạy Asynchronous với Vertical
		//run on another thread (it is not Verticle thread)
		Handler blockingHandler = new Handler<Future<String>>() {
			public String test = "abc";
			//Future này quản lý bởi Vertx, ko phải Verticle
			@Override
			public void handle(Future<String> future) {
				String result = "blockingHandler: thread="+Thread.currentThread().getId();
				//future đc dùng cho asynchronous function ở trong hàm handle này.	
				future.complete(result);   //phải gọi hàm này thì function này mới đc coi là kết thúc.
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
		
		//order = false => worker thread context (các blockingHandler tiếp theo sẽ độc lập chạy parallel)
		//order = true => theo thứ tự các blockingHandler sẽ chay nối tiếp trên 1 context khac với với Verticle
		vertx.executeBlocking(blockingHandler, false, returnHandler);
		
		//Cách 2: Java lambda syntax  => ko nên dùng vì cú pháp này ko tường minh
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
