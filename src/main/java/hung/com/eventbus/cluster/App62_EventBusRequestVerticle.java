package hung.com.eventbus.cluster;

import com.hazelcast.config.Config;

import hung.com.eventbus.EventBusReceiverVerticle;
import hung.com.eventbus.EventBusSenderVerticle;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.eventbus.DeliveryOptions;
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

public class App62_EventBusRequestVerticle extends AbstractVerticle {
	
	public static void main(String[] args) throws Exception {
		System.out.println("main(): thread="+Thread.currentThread().getId());
		System.out.println("step1: run App52_EventBusReceiverVerticle to create Node1 of Hazelcast cluster");
		System.out.println("step2: run App53_EventBusReceiverVerticle to create Node1 of Hazelcast cluster");

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
				vertx.deployVerticle(new App62_EventBusRequestVerticle());  //receive on a thread of Thread pool
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
		
		JsonObject message = new JsonObject()
									.put("collection", "mycollection")
									.put("document", new JsonObject().put("name", "tim"));
		
		DeliveryOptions options = new DeliveryOptions().addHeader("action", "save");
		
		vertx.eventBus().request(address, message, options, new Handler<AsyncResult<Message<JsonObject>>>() {
			public void handle(AsyncResult<Message<JsonObject>> event) {
				if(event.succeeded()){
					Message message = event.result();
					
					JsonObject body = (JsonObject) message.body();
					
					System.out.println("response = " + body.toString() );
				}else{ // event.failed() = true
					System.out.println("failed");
				}
			};
		});
	}
}
