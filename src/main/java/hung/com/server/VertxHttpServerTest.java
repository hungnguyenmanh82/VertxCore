package hung.com.server;

import hung.com.verticle.VertxHttpServerVerticle;
import io.vertx.core.Vertx;

public class VertxHttpServerTest {
	public static void main(String[] args) {
		System.out.println("start main(): thread="+Thread.currentThread().getId());
		//create singleton
		Vertx vertx = Vertx.vertx();
		vertx.deployVerticle(new VertxHttpServerVerticle());
	}
}
