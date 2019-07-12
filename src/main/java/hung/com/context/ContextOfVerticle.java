package hung.com.context;

import java.util.Set;

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
public class ContextOfVerticle extends AbstractVerticle {
	

	/**
	 * Hàm này lấy context ko phụ thuộc vào Thread gọi nó
	 */
	public Context getRealContext(){
		// ko dùng hàm vertx.getOrCreateContext() => vì nó convert CurrentThread về Context
		return context;  // = protect AbstractVerticle.context
	}

	@Override
	public void start(Future<Void> startFuture) throws Exception {
		//hàm này phải đc gọi để xác định quá trình Deploy thành công
		// nếu ko phải gọi hàm startFuture.complete()
		super.start(startFuture);  

		System.out.println("ContextOfVerticle.start(): thread="+Thread.currentThread().getId());

		
		if (context.isEventLoopContext()) {
			System.out.println("ContextOfVerticle: Context attached to Event Loop: "+ context.deploymentID());
		} else if (context.isWorkerContext()) {
			System.out.println("ContextOfVerticle: Context attached to Worker Thread: "+ context.deploymentID());
		} else if (context.isMultiThreadedWorkerContext()) {
			System.out.println("ContextOfVerticle: Context attached to Worker Thread - multi threaded worker: "+ context.deploymentID());
		} else if (! Context.isOnVertxThread()) {
			System.out.println("ContextOfVerticle: Context not attached to a thread managed by vert.x: "+ context.deploymentID());
		}
		
		
		Set<String> deploymentIDs = vertx.deploymentIDs();
		
		// hàm startFuture.complete() trong super.start() là asynchronous chưa đc thực hiện nên chua cập nhật
		System.out.println("ContextOfVerticle: list of deploymentIDs: number Deployments =" + deploymentIDs.size());
		for(String depId: deploymentIDs){
			//
			System.out.println(depId);
		}

	}

	@Override
	public void stop(Future<Void> stopFuture) throws Exception {
		super.stop(stopFuture);
		System.out.println("ContextOfVerticle.stop(): thread=" + Thread.currentThread().getId());
	}

}
