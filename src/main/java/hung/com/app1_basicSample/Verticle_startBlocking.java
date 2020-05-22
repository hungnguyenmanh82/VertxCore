package hung.com.app1_basicSample;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Context;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.net.NetSocket;

/**
 * 
 start() blocking tức ko dùng Future để callback

 */
public class Verticle_startBlocking extends AbstractVerticle {


	@Override
	public void start() throws Exception {
		super.start();  //phải gọi hàm này thì vertx.deploymentIDs() mới cập nhật giá trị.
		
		System.out.println(this.getClass().getName()+ ".start()"+ ": thread="+Thread.currentThread().getId() + ", ThreadName="+Thread.currentThread().getName());
	}


	@Override
	public void stop() throws Exception {
		super.stop(); //phải gọi hàm này thì vertx.deploymentIDs() mới cập nhật giá trị.
		System.out.println(this.getClass().getName()+ ".stop()"+ ": thread="+Thread.currentThread().getId() + ", ThreadName="+Thread.currentThread().getName());
	}

}
