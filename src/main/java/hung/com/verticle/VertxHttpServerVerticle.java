package hung.com.verticle;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerRequest;

public class VertxHttpServerVerticle extends AbstractVerticle{
	private HttpServer httpServer = null;

	@Override
	public void start() throws Exception {
		System.out.println("MyVerticle started! port=81: thread="+Thread.currentThread().getId());
		httpServer = vertx.createHttpServer();

		httpServer.requestHandler(new Handler<HttpServerRequest>() {
		    @Override
		    public void handle(HttpServerRequest request) {
		        System.out.println("incoming request!: thread="+Thread.currentThread().getId());
		        
		        System.out.println("uri = "+ request.uri());
		        System.out.println("uri = "+ request.path());
		        request.getParam("p1");
		    }
		});

		httpServer.listen(81);
	}
	

	@Override
	public void stop(Future<Void> stopFuture) throws Exception {
		System.out.println("MyVerticle stopped!");
	}
}
