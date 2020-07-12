package hung.com.app6_exception;

import java.util.Set;

import io.netty.util.internal.logging.InternalLoggerFactory;
import io.netty.util.internal.logging.Log4J2LoggerFactory;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;

/**
 * vd: Khởi tạo 1 vertical  < xem khái niệm vertical>
 *
 */
public class App1_exception extends AbstractVerticle {

	public static void main(String[] args) throws InterruptedException{
		// Netty Logging
		InternalLoggerFactory.setDefaultFactory(Log4J2LoggerFactory.INSTANCE);
		
		//
		Vertx vertx = Vertx.vertx();
		vertx.deployVerticle(new App1_exception()); //asynchronous call MyVerticle1.start() in worker thread
		
	}
	
	@Override
	public void start() throws Exception {
		super.start();
		
		// Vertx 4.0 Milestone5 ko hiển thị Vertx-Log => lỗi
		throw new Exception("test Exception Handler");
	}

}
