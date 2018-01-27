package hung.com.timer;

import hung.com.tcp.server.TcpServerVerticle;
import io.vertx.core.Vertx;

public class TimerMain {
	public static void main(String[] args) {
		System.out.println("start main(): thread="+Thread.currentThread().getId());
		//get a new instance of Vertx => tương ứng 1 thread thì đúng hơn.
		Vertx vertx = Vertx.vertx();
		vertx.deployVerticle(new TimerVerticle());	
	}
}
