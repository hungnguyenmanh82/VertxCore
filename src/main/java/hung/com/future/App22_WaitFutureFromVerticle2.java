package hung.com.future;


import hung.com.basicSample.MyVerticle1;
import io.vertx.core.AsyncResult;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.file.FileSystem;
import io.vertx.core.http.HttpServer;
import io.vertx.core.net.NetServer;

/**
Future:là 1 class chứa function để tạo event Future.complete(), Future.fail() kèm giá trị trả về result là kiểu tùy ý (String, int, object..). 
 Future đc tạo ở context nào thì  thuộc về context đó (ở Verticle context nào thì thuộc về context đó).
  Future sẽ trigger event cho Context mà nó thuộc về.  Có nghĩa là trong Future phải chứa thông tin của Context nó thuộc về.  
  VertX implicit phần thiết lập Future => bản chất là Future đc tạo ở thread của verticle context nào thì nó thuộc về Verticle đó và sẽ trigger cho Verticle đó. 
 */
public class App22_WaitFutureFromVerticle2 {

	public static void main(String[] args) throws InterruptedException{

		System.out.println("main(): thread=" + Thread.currentThread().getId());

		VertxOptions option = new VertxOptions().setWorkerPoolSize(4);
		Vertx vertx = Vertx.vertx(option);
		Future<String> futureTest = Future.future(); //fut1: gắn với context của Vertx

		vertx.deployVerticle(new FutureInplementAtVerticle(futureTest));


		// Future<Void> và AsyncResult<Void> cùng kiểu <Void>	
		futureTest.setHandler(new Handler<AsyncResult<String>>() {
			// code này run trên cùng thread với CreateFile đc cấp phát bởi threadpool của vertx context (đã test)
			@Override
			public void handle(AsyncResult<String> event) {
				if( event.succeeded()){
					System.out.println("succeeded");	
				}else if(event.failed()){
					System.out.println("failed");
				}

				System.out.println("returnHandler: thread=" + Thread.currentThread().getId()+
						", result=" + event.result());
			}
		});

		System.out.println("main(): end of main()");
		// app ko stop với Main() stop vì có 1 worker thread quản lý Vertx có loop bắt Event
		//vertx.close();

	}

}
