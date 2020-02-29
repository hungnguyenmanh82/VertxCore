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
public class App94_config_file {
	public static void main(String[] args) throws InterruptedException{

		Vertx vertx = Vertx.vertx();
		
		ConfigStoreOptions fileOptions = new ConfigStoreOptions()
											  .setType("file")          // lay option tu Json file
											  .setConfig(new JsonObject().put("path", "./src/config/local.json"));
		
		ConfigRetrieverOptions options = new ConfigRetrieverOptions().addStore(fileOptions);
		
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
					
					System.out.println("api.gateway.http.port:"+ config.getInteger("api.gateway.http.port"));
					System.out.println("api.gateway.http.address:"+ config.getString("api.gateway.http.address"));
					
					// child JsonObject
					JsonObject circuitBreakerConfig = config.getJsonObject("circuit-breaker");
					System.out.println("name:"+ circuitBreakerConfig.getString("name"));
					System.out.println("timeout:"+ circuitBreakerConfig.getInteger("timeout"));
				}

			}
		});

		//vertx.close();   //get config asynchronous => ko đc gọi hàm này
	}
}
