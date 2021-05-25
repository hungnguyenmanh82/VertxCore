package hung.com.config;

import io.vertx.config.ConfigRetriever;
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

/**
 * system properties: là các properties của hệ điều hành
 * system properties có thể add thêm properties vào từ commandline hoặc từ source code java System.setProperty() :
 *   > java -D<propertyName>=<value> -jar <app_params>
 * 
 * add vào ở commandline thì chỉ có phạm vi trong Process đó thôi (là local system properties, ko phải global system properties)
 * Environment Variable add vào từ OS thì các app viết bằng ngôn ngữ khác đều đọc đc.   
 */
public class App91_config_default {
	public static void main(String[] args) throws InterruptedException{

		/**
		 * config đc lưu trong Vertx context.
		 * Verticle khác nhau có context khác nhau => config khác nhau.
		 */
		Vertx vertx = Vertx.vertx();
		
		// dùng defaut config để lấy System properties và Enviroment variables of Window or linux
		ConfigRetriever retriever = ConfigRetriever.create(vertx);
		
		retriever.getConfig()	// = Future<JsonObject>
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
