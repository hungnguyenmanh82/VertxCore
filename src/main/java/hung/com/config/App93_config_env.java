package hung.com.config;


import io.vertx.config.ConfigRetriever;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
/**
https://vertx.io/docs/vertx-config/java/

By default, the Config Retriever is configured with the following stores (in this order):
The Vert.x verticle config()
The system properties
The environment variables
A conf/config.json file. This path can be overridden using the vertx-config-path system property or VERTX_CONFIG_PATH environment variable.
 */

/**
 * system properties có thể add vào ở commandline hoặc từ source code java System.setProperty() hoặc từ Pom.xml file cho Debug F11 trên Eclipse :
 *   > java -Dvertx.hazelcast.config=./src/config/cluster.xml -jar ./target/vertx-docker-config-launcher.jar -cluster -conf ./src/config/local.json
 * 
 * System properties chỉ JavaApp add nó vào mới đọc đc 
 * Environment Variable add vào từ OS thì các app (process)  khác đều đọc đc.   
 * search: "java system variable", "java system properties"
 */
public class App93_config_env {
	public static void main(String[] args) throws InterruptedException{
		/**
		 * config đc lưu trong Vertx context.
		 * Verticle khác nhau có context khác nhau => config khác nhau.
		 */
		Vertx vertx = Vertx.vertx();
		
		ConfigStoreOptions  optionStore = new ConfigStoreOptions()
										  .setType("env")          //Environment variable of window or linux
										  .setConfig(new JsonObject().put("raw-data", true)); //true nghĩa là để tất cả Eviroment ở dạng String ko convert sang kiểu khác
		
		ConfigRetrieverOptions options = new ConfigRetrieverOptions().addStore(optionStore);
		
		ConfigRetriever retriever = ConfigRetriever.create(vertx,options);
		
		retriever.getConfig()		// = Future<JsonObject>
		.onSuccess((JsonObject config)->{
			//===========================================================================
			// hau het cac lib của Vertx đêu hỗ trợ options là JsonObject
			// vd: vertx options, http server option, verticle deploy option, threadpool option, circuit Breaker options...
			
			System.out.println(config.toString()); //
			System.out.println("Path:"+ config.getString("Path"));
			System.out.println("JAVA_HOME:"+ config.getString("JAVA_HOME"));
		})
		.onFailure(thr->{
			thr.printStackTrace();
		});
		


		//vertx.close();   //get config asynchronous => ko đc gọi hàm này
	}
}
