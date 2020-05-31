package hung.com.app2_blocking;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;

/**
 * 
http://vertx.io/docs/vertx-core/java/#blocking_code

Vertical:  được hiểu như là 1 đối tượng (đơn vị quản lý tài nguyên) 
   by default: vertical sẽ đc asign thread để đảm bảo tài nguyên ko bị tranh chấp giữa 2 thread.
    1 Thời điểm chỉ 1 thread đc assyn truy cập tài nguyên vertical
   //=======
   Các Vertical tương tác với nhau qua cơ chế Event queue (có thể tương tác với ngoài), 
   chứa các function callback để nhận event đăng ký với Vertx. 
   Và gửi event (or message) tới Vertx.  
   //=======
   VertX sẽ quản lý cấp phát thread cho Vertical từ thread pool.  
   1 thread có thể dùng lại cho nhiều vertical
   //=============
   blocking execute code: Là 1 đoạn code đc Verticle trigger để chạy Asynchronous với Verticle (chạy song song) trên workerThreadPool (ko chạy trên EventLoop ThreadPool)
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
public class App22_BlockingCodeVerticle_DeployOption extends AbstractVerticle {

	public static void main(String[] args) throws InterruptedException{
		System.out.println("start main(): thread="+Thread.currentThread().getId());
		
		//create a new instance Vertx => a worker thread
		VertxOptions vertxOptions = new VertxOptions().setWorkerPoolSize(4)  // threadPool của blocking code 
														.setEventLoopPoolSize(4);  //threadpool của EventLoop cho standard Verticle
		Vertx vertx = Vertx.vertx(vertxOptions);

		DeploymentOptions deploymentOptions = new DeploymentOptions()
				.setInstances(1)          //create 6 instances of Verticles
				.setWorkerPoolName("WorkerPoolName")
				.setWorkerPoolSize(3)
				.setWorker(false); 	//true: worker-vertical dùng WorkerPoolName1  (các event vẫn tuần tự, nhưng trên thread khác nhau)
									//false (default): Standard-verticle dùng vert.x-eventloop-thread (fix thread to verticle)
									//blockingCode luôn dùng WorkerPoolName
		
		//register Verticale with Vertex instance to capture event.
		vertx.deployVerticle(new App22_BlockingCodeVerticle_DeployOption(), deploymentOptions); 

		// app ko stop với Main() stop vì có 1 worker thread quản lý Vertx có loop bắt Event
		//vertx.close();
	}
	
	@Override
	public void start(Future<Void> startFuture) throws Exception {	
		super.start(startFuture);
		System.out.println(this.getClass().getName()+ ".start(): thread="+Thread.currentThread().getId() + ", ThreadName="+Thread.currentThread().getName());


		//blockingHandler run trên workerThreadPool => độc lập với verticle thread
		// nó trigger Verticle context qua Promise
		Handler<Promise<String>> blockingHandler = new Handler<Promise<String>>() {
			public String test = "abc";
			@Override
			public void handle(Promise<String> promise) {
				System.out.println("******blockingHandler: thread="+Thread.currentThread().getId() + ", ThreadName="+Thread.currentThread().getName());
				
				String result = "blockingHandler: thread="+Thread.currentThread().getId();
				promise.complete(result);   //sẽ gọi future.handle(AsyncResult<resultType>) ngay trên Thread này

				//future.fail(result);   //sẽ gọi future.handle(AsyncResult<resultType>) ngay trên Thread này
				System.out.println(test);
			}

		};

		//returnHandler run trên Verticle Thread
		Handler<AsyncResult<String>> returnHandler =  new Handler<AsyncResult<String>>() {
			public void handle(AsyncResult<String> event) {

				if( event.succeeded()){
					System.out.println("succeeded");	
				}else if(event.failed()){
					System.out.println("failed");
				}

				System.out.println("returnHandler: thread=" + Thread.currentThread().getId()+ ", ThreadName="+Thread.currentThread().getName()+
						", result=" + event.result());
			};
		};

		//order = false: các blockingHandler của Verticle này chạy trên WorkerThreadPool độc lập nhau (ko quan trọng order).
		//by default (function 2 tham số): order = true => chạy theo order trong queue của Verticle
		// blocking-code run trên Workerthreadpool của vertx context
		vertx.executeBlocking(blockingHandler, false, returnHandler);

	}

	@Override
	public void stop(Future<Void> stopFuture) throws Exception {
		System.out.println("MyVerticle.stop(): thread=" + Thread.currentThread().getId());
	}

}
