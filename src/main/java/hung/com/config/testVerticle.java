package hung.com.config;

import io.vertx.config.ConfigRetriever;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;

public class testVerticle extends AbstractVerticle {

	public static void main(String[] args) {
		//get config in Verticle
		Vertx vertx = Vertx.vertx();
		vertx.deployVerticle(new testVerticle());
	}

	public void start(Future<Void> startFuture) throws Exception {
		super.start(startFuture);
		System.out.println("\n\n<= testVerticle.start():"+ ",thread="+Thread.currentThread().getId());

		// dùng defaut config để lấy System properties và Enviroment variables of Window or linux
		ConfigRetriever retriever = ConfigRetriever.create(vertx);

		// Asynchronous get Json restful Environment (
		retriever.getConfig(new Handler<AsyncResult<JsonObject>>() {
			@Override
			public void handle(AsyncResult<JsonObject> ar) {
				if (ar.failed()) {
					// Failed to retrieve the configuration
					System.out.println("fail: get config from Enviroment Variable");
				} else { //event.succeeded()
					//===========================================================================
					// hau het cac lib của Vertx đêu hỗ trợ options là JsonObject
					// vd: vertx options, http server option, verticle deploy option, threadpool option, circuit Breaker options...
					JsonObject config = ar.result();

					System.out.println(config.toString());

					System.out.println("Path:"+ config.getString("Path"));
					System.out.println("JAVA_HOME:"+ config.getString("JAVA_HOME"));
				}

			}
		});
	}
}
