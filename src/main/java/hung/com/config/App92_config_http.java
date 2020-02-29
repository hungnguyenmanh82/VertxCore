package hung.com.config;

import java.util.Set;

import hung.com.app1_basicSample.MyVerticle;
import io.vertx.config.ConfigRetriever;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

public class App92_config_http {
	public static void main(String[] args) throws InterruptedException{
		System.out.println("start main(): thread="+Thread.currentThread().getId());

		//create a new instance Vertx => a worker thread sinh ra để quản lý loop Event, vì thế hàm main() kết thúc nhưng App ko stop
		// gọi vertx.close() để stop thread này
		// vertx là singleton
		Vertx vertx = Vertx.vertx();

		//==================== xac dinh nơi lay file Json la http server ===========
		ConfigStoreOptions httpStore = new ConfigStoreOptions()
				.setType("http")
				.setConfig(new JsonObject().put("host", "localhost")
						                   .put("port", 8080)
						                   .put("path", "/conf") );  // url để lấy config file trên server


		ConfigRetrieverOptions options = new ConfigRetrieverOptions().addStore(httpStore);

		ConfigRetriever retriever = ConfigRetriever.create(vertx, options);

		// Asynchronous get Json restful from http server
		retriever.getConfig(new Handler<AsyncResult<JsonObject>>() {
			@Override
			public void handle(AsyncResult<JsonObject> event) {
				if (event.failed()) {
					// Failed to retrieve the configuration
				} else {
					//===========================================================================
					// hau het cac lib của Vertx đêu hỗ trợ options là JsonObject
					// vd: vertx options, http server option, verticle deploy option, threadpool option, circuit Breaker options...
					JsonObject config = event.result();
					
				}

			}
		});
		

		// dùng Lambda syntax
/*		retriever.getConfig(ar -> {
			if (ar.failed()) {
				// Failed to retrieve the configuration
			} else {
				JsonObject config = ar.result();
			}
		});*/

		//vertx.close();
	}
}
