package hung.com.blocking;

import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
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
public class App22_CompositeFuture {

	public static void main(String[] args) throws InterruptedException{
		Vertx vertx = Vertx.vertx();
		NetServer netServer = vertx.createNetServer(); //tcp server để thiết lập mặc định
		HttpServer httpServer = vertx.createHttpServer();  //http server để thiết lập mặc định port 80
		
		// future để quản lý event => thông báo khi http Server đc khởi tạo thành công
		Future<HttpServer> httpServerFuture = Future.future();
		httpServer.listen(httpServerFuture.completer());

		// future để quản lý event => thông báo TCP server đc khởi tạo thành công
		Future<NetServer> netServerFuture = Future.future();
		netServer.listen(netServerFuture.completer());
		
		//chờ cho 2 Server đc khởi tạo thành công (listening) or fail
		//CompositeFuture: nếu 1 trong 2 fail thì tất cả fail
		CompositeFuture.all(httpServerFuture, netServerFuture).setHandler(ar -> {
		  if (ar.succeeded()) {
		    // All servers started
			  System.out.println("all server started successfully");  
		  } else {
		    // At least one server failed
			  System.out.println("At least one server failed"); 
		  }
		});
	}

}
