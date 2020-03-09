package hung.com.eventbus.cluster;

import com.hazelcast.config.Config;

import hung.com.eventbus.EventBusReceiverVerticle;
import hung.com.eventbus.EventBusSenderVerticle;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.spi.cluster.ClusterManager;
import io.vertx.spi.cluster.hazelcast.HazelcastClusterManager;

public class App53_EventBusSenderVerticle extends AbstractVerticle {
	
	public static void main(String[] args) throws Exception {
		System.out.println("main(): thread="+Thread.currentThread().getId());
		// sẽ lấy file config ở resources/cluster.xml
		Config hazelcastConfig = new Config();

		ClusterManager mgr = new HazelcastClusterManager(hazelcastConfig);

		VertxOptions options = new VertxOptions().setClusterManager(mgr);

		Vertx.clusteredVertx(options, res -> {
			if (res.succeeded()) {
				Vertx vertx = res.result();
				vertx.deployVerticle(new App53_EventBusSenderVerticle());  //receive on a thread of Thread pool
			} else {
				// failed!
			}
		});

		// app ko stop với Main() stop vì có 1 worker thread quản lý Vertx có loop bắt Event
		//vertx.close();
	}

	public void start(Future<Void> startFuture) throws Exception {
		super.start(startFuture);
		System.out.println(this.getClass().getName()+ ".start(): thread="+Thread.currentThread().getId() + ", ThreadName="+Thread.currentThread().getName());
    	
    	//publish() method sends the message to all verticles listening on a given address.
    	// address: của bên nhận và gửi phải giống nhau để giao tiếp với nhau
    	// Message<String> = address + header + body
    	// body kiểu String
    	EventBus evenbus = vertx.eventBus();
    	String address = "anAddress";
    	
    	//Pulish(): 
    	// tất cả Verticle đăng ký nhận dùng 1 address, đều nhận đc nó.
    	evenbus.publish(address, "=>Message publicly");  //có thể Message Content String bằng kiểu khác: object, int,float...
        
        //send(): 
    	// Nhiều Verticle đăng ký nhận dùng 1 address, nhưng chỉ có 1 verticle nhận đc
        vertx.eventBus().send("anAddress", "=>Message only one Receiver");
    }
}
