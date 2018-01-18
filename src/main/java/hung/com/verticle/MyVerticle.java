package hung.com.verticle;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Vertx;

public class MyVerticle extends AbstractVerticle {

	@Override
	public void start(Future<Void> startFuture) throws Exception {	
		System.out.println("MyVerticle started!: thread="+Thread.currentThread().getId());
	}

	@Override
	public void stop(Future<Void> stopFuture) throws Exception {
		System.out.println("MyVerticle stopped!");
	}


}
