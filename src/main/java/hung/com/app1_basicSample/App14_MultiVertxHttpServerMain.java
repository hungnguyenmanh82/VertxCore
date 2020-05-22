package hung.com.app1_basicSample;

import hung.com.http.server.HttpServerVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;

/**
 *  mỗi 1 vertx = Vertx.vertx()  sẽ có 1 thread đứng ra điều phối các Verticle mà nó deploy.
 *  Verticle là task đc asign thread trong thread pool của vertx để chạy ( xem app13_ ).
 *  Verticale có thể đóng vai trò Client hay Server thì tùy => bản chất nó là 1 task có các đơn vị quản lý tài nguyên memory.
 *  //=====
 *  Xem thêm mục http protocol
 */
public class App14_MultiVertxHttpServerMain {
	public static void main(String[] args) throws InterruptedException{
		System.out.println("main(): thread="+Thread.currentThread().getId() + ", ThreadName="+Thread.currentThread().getName());
		
		//========================== vertx 1 ===========================
		//Ctrl+ T: để tìm implement Class sẽ thấy Vertx.vertx() sẽ new instance (ko phải Singleton)
		Vertx vertx1 = Vertx.vertx();
		vertx1.deployVerticle(new MyVerticle(), new Handler<AsyncResult<String>>(){
			//hàm này trigger khi VertxHttpServerVerticle.start(future) hoàn thành asynchronous
			@Override
			public void handle(AsyncResult<String> stringAsyncResult) {
				System.out.println(" vertx1=> vertx.deployVerticle(): thread="+Thread.currentThread().getId()+ ", ThreadName="+Thread.currentThread().getName());
			}
		});


		//================== Vertx2==================
		Vertx vertx2 = Vertx.vertx();
		vertx2.deployVerticle(new MyVerticle(), new Handler<AsyncResult<String>>(){
			//hàm này trigger khi VertxHttpServerVerticle.start(future) hoàn thành asynchronous
			@Override
			public void handle(AsyncResult<String> stringAsyncResult) {
				System.out.println(" vertx2 => vertx.deployVerticle(): thread="+Thread.currentThread().getId()+ ", ThreadName="+Thread.currentThread().getName());
			}
		});
	}
}
