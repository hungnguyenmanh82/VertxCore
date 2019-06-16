package hung.com.future;

import io.vertx.core.AsyncResult;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.net.NetServer;

/**
 * http://vertx.io/docs/vertx-core/java/#_concurrent_composition
 * 
 * VD: tạo 2 server. Và bắt sự kiện cả 2 server khởi tạo xong hoàn toàn.
 * 
 * 	Future: đc hiểu là 1 event
 *  CompositeFuture: sẽ đc trigger event khi cả 2 server đc khởi tạo xong.
 */
public class App24_CompositeFuture_any {

	public static void main(String[] args) throws InterruptedException{
		Vertx vertx = Vertx.vertx();
		NetServer netServer = vertx.createNetServer(); //tcp server để thiết lập mặc định
		HttpServer httpServer = vertx.createHttpServer();  //http server để thiết lập mặc định port 80
		
		// future để quản lý event => thông báo khi http Server đc khởi tạo thành công
		// future đc tạo gắn với context nào sẽ tạo event để gửi về context đó
		Future<HttpServer> httpServerFuture = Future.future();
		int httpPort = 8011;
		httpServer.listen(httpPort,httpServerFuture.completer());

		// future để quản lý event => thông báo TCP server đc khởi tạo thành công
		Future<NetServer> netServerFuture = Future.future();
		int tcpPort = 8012;
		netServer.listen(tcpPort,netServerFuture.completer());
		
		//chờ cho 2 Server đc khởi tạo thành công (listening) or fail
		//CompositeFuture: nếu 1 trong 2 fail thì tất cả fail
		// đăng ký nhận future ở context hiện tại
		//Trường hợp đặc biệt là 1 Future event, vd dưới là 2 event (có thể có N event)
		CompositeFuture.any(httpServerFuture, netServerFuture).setHandler(new Handler<AsyncResult<CompositeFuture>>() {
			
			@Override
			public void handle(AsyncResult<CompositeFuture> event) {
				if (event.succeeded()) {		    
					// At least one is succeeded
					  System.out.println("At least one is succeeded");  
				  } else {
					// All failed
					  System.out.println("All failed"); 
				  }
				
			}
		});

		// app ko stop với Main() stop vì có 1 worker thread quản lý Vertx có loop bắt Event
		//vertx.close();
	}

}
