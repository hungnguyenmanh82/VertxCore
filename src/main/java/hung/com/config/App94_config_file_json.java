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
public class App94_config_file_json {
	public static void main(String[] args) throws InterruptedException{
		
		/**
		 * config đc lưu trong Vertx context.
		 * Verticle khác nhau có context khác nhau => config khác nhau.
		 */
		Vertx vertx = Vertx.vertx();
		
		/**
		 * lúc compile sẽ gộp "main/resources/" và "main/java/" vào 1 folder chung
		 App81_https_Server.class.getResource("/") = root = main/resources/ = main/java/
		 App81_https_Server.class.getResource("/abc") = main/resource/abc  = main/java/abc  
		 //
		 App81_https_Server.class.getResource("..") = root/pakage_name       => package_name của class này
		 App81_https_Server.class.getResource(".") = root/pakage_name/ 
		 App81_https_Server.class.getResource("abc") = root/pakage_name/abc
		 
		 App81_https_Server.class.getResource("abc").getPath()
		  //===========================
		  + Run or Debug mode trên Eclipse lấy Root = project folder 
		  
		  + run thực tế:  root = folder run "java -jar *.jar"
		 //========= 
		 File("loginTest.json"):   file ở root folder    (tùy run thực tế hay trên eclipse)
		 File("/abc/test.json"):   path theo root folder
		 */
		ConfigStoreOptions fileOptions = new ConfigStoreOptions()
											  .setType("file")          // lay option tu Json file
//											  .setFormat("properties")  //format của file default = json
											  .setConfig(new JsonObject().put("path", "./src/config/local.json")); //run or debug mode lấy Root = project folder
		
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
