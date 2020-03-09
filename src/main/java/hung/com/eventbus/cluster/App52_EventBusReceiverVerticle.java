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
import io.vertx.core.spi.cluster.ClusterManager;
import io.vertx.spi.cluster.hazelcast.HazelcastClusterManager;

/**
 * 
 * nhiều  Verticle đăng ký với  1 Vertx (Vertx là 1 instance sẽ khởi tạo queue và threads để xử lý event)
 * 1 Verticle instance chỉ attach duy nhất 1 Vertx instance.
 * các Verticle sẽ chạy trên Thread do Vertx chỉ định. Là 1 thread.
 * Nhiều Verticle sẽ chỉ chạy trên 1 thread.
 * 
 * Verticle thực chất giữ function callback để khi có event thì Vertx gọi. 
 * Verticle chứa Event Queue của nó. Nhưng Vertx sẽ truy cập queue này và call Verticle run
 * 
 * Tuy nhiên Vertx sinh ra nhiều thread để quản lý event:
 *  + 1 thread để run callback function khi có event
 *  + 1 thread để run handler của Vertx
 */

public class App52_EventBusReceiverVerticle extends AbstractVerticle {
	public static void main(String[] args) throws Exception {
		System.out.println("main(): thread="+Thread.currentThread().getId());

		// sẽ lấy file config ở resources/cluster.xml
		Config hazelcastConfig = new Config();

		ClusterManager mgr = new HazelcastClusterManager(hazelcastConfig);

		VertxOptions options = new VertxOptions().setClusterManager(mgr);

		Vertx.clusteredVertx(options, res -> {
			if (res.succeeded()) {
				Vertx vertx = res.result();
				vertx.deployVerticle(new App52_EventBusReceiverVerticle());  //receive on a thread of Thread pool
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
		String address = "anAddress";
		MessageConsumer<String> consumer = eb.consumer(address);  //register Address với EventBus to reciever message

		// register nhận Message có address tại Eventbus
		consumer.handler(new Handler<Message<String>>() { //có thể thay String bằng kiểu khác: object, int,float...
			@Override
			public void handle(Message<String> message) {
				// chạy trên Thread của Verticle
				System.out.println( "consumer.handler: thread="+Thread.currentThread().getId() + ", ThreadName="+Thread.currentThread().getName());
				System.out.println("receive Message: " +  
						", address="+ message.address()+ 
						", body=" +message.body());  //body kiểu <string>

				// message tổ chức giống như Http protocol vậy
				// bên publish có thể gửi headers, bên nhận có thê nhận header
				//message.headers();

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
