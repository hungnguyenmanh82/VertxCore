package hung.com.eventbus.cluster;

import com.hazelcast.config.Config;
import com.hazelcast.config.InterfacesConfig;
import com.hazelcast.config.NetworkConfig;

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

 */

public class App51_vertxCluster_hazelcast extends AbstractVerticle {
	public static void main(String[] args) throws Exception {
		
		// sẽ lấy file config ở resources/cluster.xml
		Config hazelcastConfig = new Config();
		

		
		ClusterManager mgr = new HazelcastClusterManager(hazelcastConfig);

		VertxOptions options = new VertxOptions().setClusterManager(mgr);

		Vertx.clusteredVertx(options, res -> {
		  if (res.succeeded()) {
		    Vertx vertx = res.result();
		  } else {
		    // failed!
		  }
		});
	}


}
