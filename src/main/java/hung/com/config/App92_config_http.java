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

		/**
		 * config đc lưu trong Vertx context.
		 * Verticle khác nhau có context khác nhau => config khác nhau.
		 */
		Vertx vertx = Vertx.vertx();

		//==================== xac dinh nơi lay file Json la http server ===========
		ConfigStoreOptions httpStore = new ConfigStoreOptions()
				.setType("http")
//				.setFormat("properties") //default = "json"
				.setConfig(new JsonObject().put("host", "localhost")
						                   .put("port", 8080)
						                   .put("path", "/conf") );  // url = http://localhost/conf để lấy config file trên server


		ConfigRetrieverOptions options = new ConfigRetrieverOptions().addStore(httpStore);

		ConfigRetriever retriever = ConfigRetriever.create(vertx, options);

		retriever.getConfig()		// = Future<JsonObject>
		.onSuccess((JsonObject config)->{
			//===========================================================================
			// hau het cac lib của Vertx đêu hỗ trợ options là JsonObject
			// vd: vertx options, http server option, verticle deploy option, threadpool option, circuit Breaker options...
			
			System.out.println(config.toString()); //

		})
		.onFailure(thr->{
			thr.printStackTrace();
		});
		



		//vertx.close();   //get config asynchronous => ko đc gọi hàm này
	}
}
