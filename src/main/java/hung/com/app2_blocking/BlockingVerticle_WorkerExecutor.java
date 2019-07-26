package hung.com.app2_blocking;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.WorkerExecutor;

/**
 * 
   //=============
   blocking execute code: Là 1 đoạn code đc Verticle trigger để chạy Asynchronous với Verticle (chạy song song).
    Sau khi chạy xong đoạn code này nó sinh event gửi tới Verticle qua Vertx.
    
   có thể tạo threadpool riêng để thực hiện Blocking code. Thay vì dùng chung threadpool với Vertx   

 */
public class BlockingVerticle_WorkerExecutor extends AbstractVerticle {

	@Override
	public void start(Future<Void> startFuture) throws Exception {	
//		this.context;  //quản lý tất cả tài nguyên của Verticle
//		this.context.isWorkerContext()
//		this.context.isMultiThreadedWorkerContext()
		System.out.println("MyVerticle.start(): thread="+Thread.currentThread().getId());
		
		
		//BlockingHanderler: thuộc Vertx context => chạy trên threadpool của Vertx context
		//bất key Event, hay task nào tạo ra trong Blocking code đều thuộc quản lý của Context hiện tại => đều run trên thread của Verticle
		//Trong khi Event, task sinh ra ở Blocking-code lại thuộc context của Verticle tạo ra “blocking-code” => 
		//event hay task này sẽ chạy trên thread (or threadpool) của Verticle (ko chạy trên vertx context).
		Handler blockingHandler = new Handler<Future<String>>() {
			public String test = "abc";
			//Future này quản lý bởi Vertx, ko phải Verticle
			@Override
			public void handle(Future<String> future) {
				String result = "blockingHandler: thread="+Thread.currentThread().getId();
				//future đc dùng cho asynchronous function ở trong hàm handle này.	
				future.complete(result);   //sẽ gửi event tới context của Verticle
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
		
		
		//======================== tạo threadpool riêng để thực hiện Blocking code =================
		// ko dùng chung threadpool với Vertx nữa
		int poolSize = 2;
		long maxExecuteTime = 1000; //mini second
		String threadPoolName = "my-worker-pool"; //tên là id duy nhất. 2 tham số còn lại chỉ dùng lần đầu tạo threadpool
		
		// nếu đã tồn tại threadpoolName này rồi, thì nó lấy luôn threadpool đó (ko tạo mới)
		WorkerExecutor executor = vertx.createSharedWorkerExecutor(threadPoolName,poolSize, maxExecuteTime);
		
		//order = false => sẽ có ưu tiên cao hơn. chạy trên thread độc lập ko quan tâm order trong queue
		//by default (function 2 tham số): order = true => chạy theo order trong queue 
		executor.executeBlocking(blockingHandler, false, returnHandler);
		
		//giải phóng threadpool này
		//executor.close();

	}

	@Override
	public void stop(Future<Void> stopFuture) throws Exception {
		System.out.println("MyVerticle.stop(): thread=" + Thread.currentThread().getId());
	}

}
