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
public class ContextHandlerQueueVerticle extends AbstractVerticle {

	@Override
	public void start(Future<Void> startFuture) throws Exception {
		//hàm này phải đc gọi để xác định quá trình Deploy thành công
		// nếu ko phải gọi hàm startFuture.complete()
		super.start(startFuture); 
		
		System.out.println("ContextHandlerQueueVerticle.start(): thread="+Thread.currentThread().getId() + ", ThreadName="+Thread.currentThread().getName());
		
		//vertx.getOrCreateContext() sẽ trả về context gắn với Thread hiện tại:
		// convert Current Thread => Context và trả về
		// start() luôn chạy trên thread của Verticle context hiện tại nên sẽ trả về Verticle context		
		vertx.getOrCreateContext().runOnContext(new Handler<Void>() {
			
			@Override
			public void handle(Void event) {
				System.out.println("case3:  run Handler on Context of Verticle: thread="+Thread.currentThread().getId() + ", ThreadName="+Thread.currentThread().getName());
				
			}
		});
		
		System.out.println("end ContextHandlerQueueVerticle.start()");
		
	}

	@Override
	public void stop(Future<Void> stopFuture) throws Exception {
		//function này cần đc gọi để xác nhận undeploy() thành công (sẽ xóa DeploymentId)
		// hoặc phải gọi hàm stopFuture.complete()
		super.stop(stopFuture);
//		stopFuture.complete();
		System.out.println("ContextHandlerQueueVerticle.stop(): thread=" + Thread.currentThread().getId());
	}

}
