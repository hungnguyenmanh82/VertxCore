package hung.com.http.client;

import hung.com.http.server.HttpServerVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;

/**
 * vd: tạo ra nhiều instance để tiến hành stress test khả năng chịu đựng của Server khi có multi connect tới
 *
 */
public class App53_VertxHttpClient_MultiInstance {
	public static void main(String[] args) {
		System.out.println("start main(): thread="+Thread.currentThread().getId());

		Vertx vertx = Vertx.vertx();
		
		DeploymentOptions options = new DeploymentOptions()
				.setInstances(6)          //create 6 instances of Verticals
				.setWorkerPoolName("abc")
				.setWorkerPoolSize(3)
				.setWorker(true);
		
		//register Verticale with Vertex instance to capture event.
		vertx.deployVerticle("hung.com.http.client.HttpClientVerticle",options);//asynchronous call MyVerticle1.start() in worker thread
		
	}
}
