package hung.com.app4_timer;

import hung.com.tcp.server.TcpServerVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;

public class App12_TimerWorkerThread {
	public static void main(String[] args) {
		System.out.println("start main(): thread="+Thread.currentThread().getId() + ", ThreadName="+Thread.currentThread().getName());
		
		// tạo thread pool để chạy các Verticle khi nhan dc Timer Event
		DeploymentOptions options = new DeploymentOptions()
				//.setInstances(6)          //create 6 instances here
				.setWorkerPoolName("*workerThreadPool")
				.setWorkerPoolSize(10)
				.setWorker(true);  //true: worker-vertical dùng WorkerPoolName1  (các event vẫn tuần tự, nhưng trên thread khác nhau)
									//false: Standard-verticle dùng vert.x-eventloop-thread (fix thread to verticle)
									//blockingCode luôn dùng WorkerPoolName1
		

		Vertx vertx = Vertx.vertx();
		//============================== case1: timer đc set o Verticle  ==============
		vertx.deployVerticle(new TimerVerticle(), options);  //default = standard verticle
		vertx.deployVerticle(new TimerVerticle2(), options);	 //default = standard verticle
		
		//==============================case2: timer ở ngoài ========================
		long timerID = vertx.setTimer(700,new  Handler<Long>() {
			//run on thread of Verticle
		    @Override
		    public void handle(Long aLong) {
		    	System.out.println("timer3: thread="+Thread.currentThread().getId()+ ", ThreadName="+Thread.currentThread().getName());
		    }
		});
	}
}
