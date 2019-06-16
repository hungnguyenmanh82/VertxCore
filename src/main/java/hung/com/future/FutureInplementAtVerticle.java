package hung.com.future;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.Context;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.file.FileSystem;

/**
 * 


 */
public class FutureInplementAtVerticle extends AbstractVerticle {

	private Future<String> futureTest;
	
	
	public FutureInplementAtVerticle(Future<String> futureTest) {
		super();
		this.futureTest = futureTest;
	}

	
	@Override
	public void start(Future<Void> startFuture) throws Exception {	
		
		System.out.println("FutureCreateFileVerticle: thread=" + Thread.currentThread().getId());
		
		Thread.currentThread().sleep(2000); //wait for vertx.close() finished
		
		String result = "****result here";
		futureTest.complete(result);
		
	}

	@Override
	public void stop(Future<Void> stopFuture) throws Exception {
		System.out.println("MyVerticle.stop(): thread=" + Thread.currentThread().getId());
	}

}
