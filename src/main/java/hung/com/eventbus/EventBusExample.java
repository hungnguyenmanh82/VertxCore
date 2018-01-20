package hung.com.eventbus;

import io.vertx.core.Vertx;

/**
 * Eventbus là tính năng của Vertx quản lý cho phép các Verticle message nội bộ với nhau.
 * Vertx sẽ quản lý message, tạo event asynchnous tới các Verticle.
 * Vertx sẽ cấp phát thread cho các Verticles. 
 */
public class EventBusExample {
	public static void main(String[] args) throws Exception {
		System.out.println("main(): thread="+Thread.currentThread().getId());
		Vertx vertx = Vertx.vertx();
		

		//mỗi Vertical sẽ đc cấp phát 1 thread riêng để xử lý nhận message
		vertx.deployVerticle(new EventBusReceiverVerticle("R1"));  //receive on a thread of Thread pool
		vertx.deployVerticle(new EventBusReceiverVerticle("R2"));
		vertx.deployVerticle(new EventBusReceiverVerticle("R3"));
		vertx.deployVerticle(new EventBusReceiverVerticle("R4"));

		Thread.sleep(3000);
		vertx.deployVerticle(new EventBusSenderVerticle("S1")); //receive on a thread of Thread pool
		vertx.deployVerticle(new EventBusSenderVerticle("S2"));
		Thread.sleep(3000);
		vertx.deployVerticle(new EventBusSenderVerticle("S3"));
	}
}
