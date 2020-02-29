package hung.com.app2_blocking;

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
    Handler<type>: là khái niệm giống Runable chạy trên java thread. Điểm khác biệt là Handler có tham số đầu vào, còn runable thì ko.
    Tham số đầu vào của Handler sẽ đc implementor nó thực hiện lốt.
    Handler, runable đều coi là function point (trên C++) đối với implementor.  
    //=============
    Future<Type>:  extends Handler<type> và AsyncResult<type> => là kết hợp 2 class này để tạo funtion point (làm call back function khi có event).
     Khi 2 hàm future.complete(result) or future.fail(result) đc gọi thì lập tức nó sẽ gọi hàm callback của nó là Handler.handle(AsyncResult<result>). 
    Future là function point => thread nào gọi nó thì nó chạy trên thread đó (đã test).
   //===============
   AsyncResult< type>:   Là kiểu dữ liệu trả về từ Asynchronous function thường là đầu vào của Handler. Vd: Future hoặc Blocking-code…
   Đây là kiểu dữ liễu đc chuẩn hóa gồm 2 yếu tố:  (1) kết quả true/fail, Return Value là <type> có thể là kiểu tùy ý: String, int, Class Object,…


 */
public class BlockingVerticle extends AbstractVerticle {

	@Override
	public void start(Future<Void> startFuture) throws Exception {	
		//		this.context;  //quản lý tất cả tài nguyên của Verticle
		//		this.context.isWorkerContext()
		//		this.context.isMultiThreadedWorkerContext()
		System.out.println("BlockingVerticle.start(): thread="+Thread.currentThread().getId() + ", ThreadName="+Thread.currentThread().getName());


		//mấu chốt là khái niệm Future
		// BlockingHandler vẫn thuộc context của Verticle, nhưng chạy trên thread khác (xem DeploymentId ở log sẽ thấy).
		// => BlockingHandler vẫn lưu trong 1 task queue order của Verticle context
		// Verticle context có 2 task
		Handler blockingHandler = new Handler<Future<String>>() {
			public String test = "abc";
			@Override
			public void handle(Future<String> future) {
				System.out.println("******blockingHandler: thread="+Thread.currentThread().getId() + ", ThreadName="+Thread.currentThread().getName());
				
				String result = "blockingHandler: thread="+Thread.currentThread().getId();
				future.complete(result);   //sẽ gọi future.handle(AsyncResult<resultType>) ngay trên Thread này

				//future.fail(result);   //sẽ gọi future.handle(AsyncResult<resultType>) ngay trên Thread này
				System.out.println(test);
			}

		};

		//returnHandler run trên Verticle Thread
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

		//order = false: blockingHandler.handle(AsyncResult<resultType>) chạy trên thread khác với Verticle, ko đợi Handler queue của verticle
		//by default (function 2 tham số): order = true => chạy theo order trong queue của Verticle
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
