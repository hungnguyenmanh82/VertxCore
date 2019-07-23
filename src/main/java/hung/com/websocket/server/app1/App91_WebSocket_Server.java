package hung.com.websocket.server.app1;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;

/**
 * step1: create a new Vertx => a thread
 * step2: create and add a Vertical to Vertx =>  Vertical.start() will be call and run on worker thread
 */
public class App91_WebSocket_Server {
	public static void main(String[] args) {
		
		System.out.println("start main(): thread="+Thread.currentThread().getId());
		//Ví dụ demo websocket nên ko quan tâm tơi LoopEvent or Threadpool.
		Vertx vertx = Vertx.vertx();
			
		vertx.deployVerticle(new WebsocketServerVerticle_1stWay()); 	
	}
}
