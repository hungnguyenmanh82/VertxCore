package hung.com.blocking;

import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.net.NetServer;

/**
 * http://vertx.io/docs/vertx-core/java/#_concurrent_composition
 *
 */
public class ConcurrentMain {

	public static void main(String[] args) throws InterruptedException{
		Vertx vertx = Vertx.vertx();
		NetServer netServer = vertx.createNetServer();
		HttpServer httpServer = vertx.createHttpServer();
		
		Future<HttpServer> httpServerFuture = Future.future();
		httpServer.listen(httpServerFuture.completer());

		Future<NetServer> netServerFuture = Future.future();
		netServer.listen(netServerFuture.completer());
		
		//chờ cho 2 Server đc khởi tạo thành công (listening) or fail
		//CompositeFuture: nếu 1 trong 2 fail thì tất cả fail
		CompositeFuture.all(httpServerFuture, netServerFuture).setHandler(ar -> {
		  if (ar.succeeded()) {
		    // All servers started
		  } else {
		    // At least one server failed
		  }
		});
	}

}
