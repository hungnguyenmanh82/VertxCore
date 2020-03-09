package hung.com.config;


import io.vertx.config.ConfigRetriever;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
/**
 * 
https://vertx.io/docs/vertx-config/java/

By default, the Config Retriever is configured with the following stores (in this order):
The Vert.x verticle config()
The system properties
The environment variables
A conf/config.json file. This path can be overridden using the vertx-config-path system property or VERTX_CONFIG_PATH environment variable.
 *
 */
public class App95_config_system {
	public static void main(String[] args) throws InterruptedException{

		Vertx vertx = Vertx.vertx();
		
		ConfigStoreOptions  optionStore = new ConfigStoreOptions()
													  .setType("sys")  //lấy từ system
													  .setConfig(new JsonObject().put("cache", true)); // ko lấy system properties từ cache (ko lấy từ file)
		
		ConfigRetrieverOptions options = new ConfigRetrieverOptions().addStore(optionStore);
		
		ConfigRetriever retriever = ConfigRetriever.create(vertx,options);
		
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
					
					System.out.println(config.toString());
					
					System.out.println("java.runtime.name:"+ config.getString("java.runtime.name"));
					System.out.println("os.name:"+ config.getString("os.name"));
				}

			}
		});
		
		//vertx.close();   //get config asynchronous => ko đc gọi hàm này
	}
}
