package hung.com.app4_timer;

import hung.com.tcp.server.TcpServerVerticle;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;

/**
 *Standard Verticle đc ấn định 1 Thread duy nhất trong Vertx Threadpool.
 *nhiều Standard Verticle có thể chung nhau 1 Thread này.
 */
public class App11_Timer {
	public static void main(String[] args) {
		System.out.println("start main(): thread="+Thread.currentThread().getId());
		
		//default vertX là Standard Verticle
		Vertx vertx = Vertx.vertx();
		
		//============================== case1: timer đc set o Verticle  ==============
		vertx.deployVerticle(new TimerVerticle());  //default = standard verticle
		vertx.deployVerticle(new TimerVerticle2());	 //default = standard verticle
		
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
