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
public class App96_config_json {
	public static void main(String[] args) throws InterruptedException{
		/**
		 * config đc lưu trong Vertx context.
		 * Verticle khác nhau có context khác nhau => config khác nhau.
		 */
		Vertx vertx = Vertx.vertx();

		ConfigStoreOptions configStoreOptions = new ConfigStoreOptions()
											  .setType("json")          // lay option tu Json Object
											  .setConfig(new JsonObject().put("api.gateway.http.port", 8081)         //lấy từ JsonObject
													  					 .put("api.gateway.http.address","127.0.0.1"));
		
		ConfigRetrieverOptions options = new ConfigRetrieverOptions().addStore(configStoreOptions);
		
		ConfigRetriever retriever = ConfigRetriever.create(vertx,options);
		
		retriever.getConfig()		// = Future<JsonObject>
		.onSuccess((JsonObject config)->{
			//===========================================================================
			// hau het cac lib của Vertx đêu hỗ trợ options là JsonObject
			// vd: vertx options, http server option, verticle deploy option, threadpool option, circuit Breaker options...
			
			System.out.println(config.toString()); //
			System.out.println("api.gateway.http.port:"+ config.getInteger("api.gateway.http.port"));
			System.out.println("api.gateway.http.address:"+ config.getString("api.gateway.http.address"));
		})
		.onFailure(thr->{
			thr.printStackTrace();
		});


		//vertx.close();   //get config asynchronous => ko đc gọi hàm này
	}
}
