package hung.com.basicSample;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
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
public class MyVerticle1 extends AbstractVerticle {

	@Override
	public void start(Future<Void> startFuture) throws Exception {	
		System.out.println("MyVerticle.start(): thread="+Thread.currentThread().getId());
	}

	@Override
	public void stop(Future<Void> stopFuture) throws Exception {
		System.out.println("MyVerticle.stop(): thread=" + Thread.currentThread().getId());
	}

}
