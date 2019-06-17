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
public class ContextHandlerQueueVerticle extends AbstractVerticle {

	@Override
	public void start(Future<Void> startFuture) throws Exception {	
		System.out.println("ContextHandlerQueueVerticle.start(): thread="+Thread.currentThread().getId());
		
		//Handler đc add trên Thread của Context nào thì sẽ chạy trên Threadpool của context ấy
		//Handler đc add vào Queue của Context chứ ko chạy luôn
		vertx.getOrCreateContext().runOnContext(new Handler<Void>() {
			
			@Override
			public void handle(Void event) {
				System.out.println("case3:  run Handler on Context of Verticle: thread="+Thread.currentThread().getId());
				
			}
		});
		
		System.out.println("end Verticle.start()");
		
	}

	@Override
	public void stop(Future<Void> stopFuture) throws Exception {
		System.out.println("MyVerticle.stop(): thread=" + Thread.currentThread().getId());
	}

}
