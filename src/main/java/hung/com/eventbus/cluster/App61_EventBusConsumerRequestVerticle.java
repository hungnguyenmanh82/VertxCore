package hung.com.eventbus.cluster;

import com.hazelcast.config.Config;

import hung.com.eventbus.EventBusReceiverVerticle;
import hung.com.eventbus.EventBusSenderVerticle;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.json.JsonObject;
import io.vertx.core.spi.cluster.ClusterManager;
import io.vertx.spi.cluster.hazelcast.HazelcastClusterManager;

/**
 * <đọc tài liệu Hazelcast>
 * 
 * Hazelcast: là distributed Storage.
 * Hazelcast Nodes chính là các Vertx instance sẽ tạo ra 1 cluster.
 * Cluster chứa Queue Message truyền nhận giữa Publisher và subscriber
 * 
 * 
 * 
 */

public class App61_EventBusConsumerRequestVerticle extends AbstractVerticle {
	
	public static void main(String[] args) throws Exception {
		System.out.println("main(): thread="+Thread.currentThread().getId());
		System.out.println("step1: run App61_EventBusConsumerRequestVerticle to create Node1 of Hazelcast cluster");
		System.out.println("step2: run App62_EventBusRequestVerticle to create Node1 of Hazelcast cluster");

		/**
		 * sẽ lấy file config ở resources/cluster.xml
		 * nếu ko có sẽ lấy config default của Hazelcast
		 */
		ClusterManager mgr = new HazelcastClusterManager();

		VertxOptions options = new VertxOptions().setClusterManager(mgr);

		//Tạo Hazelcast cluster
		Vertx.clusteredVertx(options, res -> {
			if (res.succeeded()) {
				Vertx vertx = res.result();
				vertx.deployVerticle(new App61_EventBusConsumerRequestVerticle());  //receive on a thread of Thread pool
			} else {
				// failed!
			}
		});

		// app ko stop với Main() stop vì có 1 worker thread quản lý Vertx có loop bắt Event
		//vertx.close();
	}

	@Override
	public void start(Future<Void> startFuture) throws Exception {
		super.start(startFuture);
		System.out.println(this.getClass().getName()+ ".start(): thread="+Thread.currentThread().getId() + ", ThreadName="+Thread.currentThread().getName());

		//vertx member of AbstractVerticle  => where this Verticle was deployed
		//consumer=(register to receive event): this Verticle context đăng ký nhận Event từ Vertx
		// event đc xử lý trên threadpool của Verticle context này
		// address: của bên nhận và gửi phải giống nhau để giao tiếp với nhau
		// Message<String> = address + header + body
		// body kiểu String
		EventBus eb = vertx.eventBus();
		String address = "requestAddress";
		MessageConsumer<JsonObject> consumer = eb.consumer(address);  //register Address với EventBus to reciever message

		// register nhận Message có address tại Eventbus
		consumer.handler(new Handler<Message<JsonObject>>() { //có thể thay String bằng kiểu khác: object, int,float...
			@Override
			public void handle(Message<JsonObject> message) {
				// chạy trên Thread của Verticle
				System.out.println( "consumer.handler: thread="+Thread.currentThread().getId() + ", ThreadName="+Thread.currentThread().getName());
				System.out.println("receive Message: " +  
						", address="+ message.address()+ 
						", body=" +message.body().toString());  //body kiểu <string>

				// message tổ chức giống như Http protocol vậy
				// bên publish có thể gửi headers, bên nhận có thê nhận header
				message.headers();
				
				JsonObject response = new JsonObject()
											.put("response-key1", "value1")
											.put("response-key2", "value2")
											.put("response-key3", "value3")
											.put("response-key4", "value4");
				message.reply(response);

			}

		} );

		//register to receive Message ok
		consumer.completionHandler(res -> {
			// chạy trên Thread của Verticle
			if (res.succeeded()) {
				System.out.println("The handler registration has reached all nodes");
			} else {
				System.out.println("Registration failed!");
			}
		});

		// để unregister Message đã đăng ký với lệnh consumer()
		// phải gọi hàm này trên cùng Thread => vì ThreadId đc qui đổi ra Context
		/*consumer.unregister(res -> {
			if (res.succeeded()) {
				System.out.println("The handler un-registration has reached all nodes");
			} else {
				System.out.println("Un-registration failed!");
			}
		});*/

	}
}
