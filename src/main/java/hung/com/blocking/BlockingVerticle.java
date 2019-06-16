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
    
   //=============
    Handler: là khái niệm giống Runable chạy trên java thread. Điểm khác biệt là Handler có tham số đầu vào, còn runable thì ko.
    Tham số đầu vào của Handler sẽ đc implementor nó thực hiện lốt.
    Handler, runable đều coi là function point (trên C++) đối với implementor.  

 */
public class BlockingVerticle extends AbstractVerticle {

	@Override
	public void start(Future<Void> startFuture) throws Exception {	
//		this.context;  //quản lý tất cả tài nguyên của Verticle
//		this.context.isWorkerContext()
//		this.context.isMultiThreadedWorkerContext()
		System.out.println("MyVerticle.start(): thread="+Thread.currentThread().getId());
		
		
		//mấu chốt là khái niệm Future
		// Future: đc tạo ra ở context nào thì sẽ trigger Event cho Context đó (ở đây là BlokingVerticle context)
		Handler blockingHandler = new Handler<Future<String>>() {
			public String test = "abc";
			@Override
			public void handle(Future<String> future) {
				String result = "blockingHandler: thread="+Thread.currentThread().getId();
					future.complete(result);   //sẽ gửi event tới BlockingVerticle context
				
				//future.fail(result);   //sẽ gửi event tới BlockingVerticle context
				System.out.println(test);
			}
			
		};
		
		//returnHandler run on threadpool của Future Context (tức BlockingVerticle context)
		// bản chất là call future.setHandler(returnHandler) sau khi blockingHandler đc thực hiện 
		// Type trong Future<Type> và AsyncResult<Type> phải giống nhau =>nếu ko để kiểu Object và check type
		Handler returnHandler =  new Handler<AsyncResult<String>>() {
			public void handle(AsyncResult<String> event) {
				
				if( event.succeeded()){
					System.out.println("succeeded");	
				}else if(event.failed()){
					System.out.println("failed");
				}
				
				System.out.println("returnHandler: thread=" + Thread.currentThread().getId()+
							 ", result=" + event.result());
			};
		};
		
		//order = false => sẽ có ưu tiên cao hơn. chạy trên thread độc lập ko quan tâm order trong queue
		//by default (function 2 tham số): order = true => chạy theo order trong queue 
		// blocking-code run trên threadpool của vertx context
		// bản chất là call future.setHandler(returnHandler) sau khi blockingHandler đc thực hiện 
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
