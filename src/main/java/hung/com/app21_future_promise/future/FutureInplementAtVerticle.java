package hung.com.app21_future_promise.future;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.Context;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.file.FileSystem;

/**
 * 
Future<Type>:  extends Handler<type> và AsyncResult<type> => là kết hợp 2 class này để tạo funtion point (làm call back function khi có event).
 Khi 2 hàm future.complete(result) or future.fail(result) đc gọi thì lập tức nó sẽ gọi hàm callback của nó là Handler.handle(AsyncResult<result>). 
Future là function point => thread nào gọi nó thì nó chạy trên thread đó (đã test).

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
		
		String result = "futureTest****result here";
		futureTest.complete(result);
		
	}

	@Override
	public void stop(Future<Void> stopFuture) throws Exception {
		System.out.println("MyVerticle.stop(): thread=" + Thread.currentThread().getId());
	}

}
