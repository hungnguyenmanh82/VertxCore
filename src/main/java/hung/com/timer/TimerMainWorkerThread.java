package hung.com.timer;

import hung.com.tcp.server.TcpServerVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;

public class TimerMainWorkerThread {
	public static void main(String[] args) {
		System.out.println("start main(): thread="+Thread.currentThread().getId());
		//
		DeploymentOptions options = new DeploymentOptions()
				//.setInstances(6)          //create 6 instances here
				.setWorkerPoolName("abc")
				.setWorkerPoolSize(10)
				.setWorker(true);  //true = worker thread (default = false)
		

		Vertx vertx = Vertx.vertx();
		vertx.deployVerticle(new TimerVerticle(),options);
		vertx.deployVerticle(new TimerVerticle2(),options);	
		
		//===================
		long timerID = vertx.setTimer(4000,new  Handler<Long>() {
			//run on thread of Verticle
		    @Override
		    public void handle(Long aLong) {
		    	System.out.println("timer3: thread="+Thread.currentThread().getId());
		    }
		});
	}
}
