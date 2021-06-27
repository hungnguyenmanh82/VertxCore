package hung.com.app1_basicSample;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Vertx;

/**
 * 
Verticle:  được hiểu như là 1 đối tượng (đơn vị quản lý tài nguyên: context ) 
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
public class MyVerticle extends AbstractVerticle {

	@Override
	public void start() throws Exception {
		//hàm này phải đc gọi để xác định quá trình Deploy thành công (thì hàm vertx.deployIDs() mới trả về đúng giá trị).
		//hoặc phải gọi hàm startFuture.complete()
		super.start();
		System.out.println(this.getClass().getName()+ ".start()"+ ": thread="+Thread.currentThread().getId() + ", ThreadName="+Thread.currentThread().getName());
	}

	@Override
	public void stop() throws Exception {
		//function này cần đc gọi để xác nhận undeploy() thành công (sẽ xóa DeploymentId)
		// hoặc phải gọi hàm stopFuture.complete()
		super.stop();
		System.out.println(this.getClass().getName()+ ".stop()"+ ": thread="+Thread.currentThread().getId() + ", ThreadName="+Thread.currentThread().getName());
	}



}
