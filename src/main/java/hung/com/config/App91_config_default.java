package hung.com.config;


import io.vertx.config.ConfigRetriever;
import io.vertx.core.Vertx;
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
public class App91_config_default {
	public static void main(String[] args) throws InterruptedException{

		Vertx vertx = Vertx.vertx();
		
		// dùng defaut config để lấy System properties và Enviroment variables
		ConfigRetriever retriever = ConfigRetriever.create(vertx);
		
		

	}
}
