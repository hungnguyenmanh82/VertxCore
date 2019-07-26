package hung.com.app1_basicSample;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Context;
import io.vertx.core.Future;
//import io.vertx.core.Vertx;

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
public class Verticle_stopFuture extends AbstractVerticle {

	/**
	 * Cần nắm rõ khái niệm Context trc. Vả cơ chế lấy Context qua Current Thread
	 * Hàm này lấy context ko phụ thuộc vào Thread gọi nó
	 * 
	 */
	public Context getRealContext(){
		// ko dùng hàm vertx.getOrCreateContext() => vì nó convert CurrentThread về Context
		return context;  // = protect AbstrctVerticle.context
	}
	
	@Override
	public void start(Future<Void> startFuture) throws Exception {	
		//hàm này phải đc gọi để xác định quá trình Deploy thành công (sẽ add DeploymentId)
		// hoặc phải gọi hàm startFuture.complete()
//		super.start(startFuture);   
		
		System.out.println("Verticle_stopFuture.start(): thread="+Thread.currentThread().getId());
		
		// chìa khóa là khái niệm Future
		// hàm này sẽ call Handler<AsyncResult<String>> của vertx.deploy(Verticle, Handler)
		startFuture.complete();
		
		// hàm này sẽ call Handler<AsyncResult<String>> của vertx.deploy(Verticle, Handler)
//		startFuture.fail("MyVerticle_Finish failed");
		
		System.out.println("Verticle_stopFuture.start(): deployId=" + context.deploymentID());
		
	}

	@Override
	public void stop(Future<Void> stopFuture) throws Exception {
		//function này cần đc gọi để xác nhận undeploy() thành công (sẽ xóa DeploymentId)
		// hoặc phải gọi hàm stopFuture.complete()
		super.stop(stopFuture);
		System.out.println("Verticle_stopFuture.stop(): thread=" + Thread.currentThread().getId());
//		stopFuture.complete();
	}

}
