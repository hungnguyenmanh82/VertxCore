package hung.com.basicSample;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Vertx;

/**
 * 
Verticle:  được hiểu như là 1 đối tượng (đơn vị quản lý tài nguyên) 
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
public class MyVerticle1 extends AbstractVerticle {

	@Override
	public void start(Future<Void> startFuture) throws Exception {	
//		this.context;  //quản lý tất cả tài nguyên của Verticle
//		this.context.isWorkerContext()
//		this.context.isMultiThreadedWorkerContext()
		System.out.println("MyVerticle.start(): thread="+Thread.currentThread().getId());
	}

	@Override
	public void stop(Future<Void> stopFuture) throws Exception {
		System.out.println("MyVerticle.stop(): thread=" + Thread.currentThread().getId());
	}

}
