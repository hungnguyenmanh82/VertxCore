package hung.com.timer;

import hung.com.tcp.server.TcpServerVerticle;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;

public class App11_Timer {
	public static void main(String[] args) {
		System.out.println("start main(): thread="+Thread.currentThread().getId());
		
		//default vertX là Standard Verticle
		Vertx vertx = Vertx.vertx();
		
		//============================== case1: timer đc set o Verticle  ==============
		vertx.deployVerticle(new TimerVerticle());
		vertx.deployVerticle(new TimerVerticle2());	
		
		//==============================case2: timer ở ngoài ========================
		long timerID = vertx.setTimer(4000,new  Handler<Long>() {
			//run on thread of Verticle
		    @Override
		    public void handle(Long aLong) {
		    	System.out.println("timer3: thread="+Thread.currentThread().getId());
		    }
		});
	}
}
