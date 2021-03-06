package hung.com.app3_context;

import io.vertx.core.Context;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;

/**
Context: để chỉ 1 đơn vị quản lý tài nguyên Memory và threadpool luôn. 
Vertx context khác Verticle Context (đã test).

 Event, task thuộc context nào thì sẽ đc xử lý trên thread (or threadpool) của context đó.
blocking-code đc gọi ở Verticle nhưng thuộc Vertx Context nên run ở context của Vertx => dùng threadpool của Vertx.

Trong khi Event, task sinh ra ở Blocking-code lại thuộc context của Verticle tạo ra “blocking-code” => 
event hay task này sẽ chạy trên thread (or threadpool) của Verticle (ko chạy trên vertx context).

 */
public class App31_Context_StandardVerticle {

	public static void main(String[] args) throws InterruptedException{
		System.out.println("main(): thread="+Thread.currentThread().getId() + ", ThreadName="+Thread.currentThread().getName());
		
		//create a new instance Vertx => a worker thread
		//eventloop chỉ dùng cho Standard Verticle thôi	
		VertxOptions vertxOptions = new VertxOptions().setWorkerPoolSize(4)  // threadPool của blocking code 
				 									.setEventLoopPoolSize(4);  //threadpool của EventLoop cho standard Verticle
		Vertx vertx = Vertx.vertx(vertxOptions);

		//vertx.getOrCreateContext() sẽ trả về context gắn với current-thread
		// convert Current Thread => Context và trả về
		// Verticle.start() luôn chạy trên thread của Verticle context hiện tại nên sẽ trả về Verticle context	
		Context context = vertx.getOrCreateContext();
		
		//Context đc gắn với HashMap cho CRUD dữ liệu trên đó.
		/**
		 * context.put(key, value); 
		 * context.get(key);
		 */

		if (context.isEventLoopContext()) {
			System.out.println("main: Context attached to Event Loop: "+ context.deploymentID());
		} else if (context.isWorkerContext()) {
			System.out.println("main: Context attached to Worker Thread: "+ context.deploymentID());
		} else if (context.isMultiThreadedWorkerContext()) {
			System.out.println("main: Context attached to Worker Thread - multi threaded worker: "+ context.deploymentID());
		} else if (! Context.isOnVertxThread()) {
			System.out.println("main: Context not attached to a thread managed by vert.x: "+ context.deploymentID());
		}

		//StandardVerticle dung Evenloop của Vertx. Lấy thread trong EventLoopPool
		vertx.deployVerticle(new ContextVerticle1());  //standard verticle


		// app ko stop với Main() stop vì có 1 worker thread quản lý Vertx có loop bắt Event
		//vertx.close();
	}

}
