package hung.com.timer;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;

/**
 * 
vertx  sẽ có 1 thread chuyên tạo timer. khi có timer nó sẽ trigger event tới Vertx.
VertX có 1 thread riêng để xử lý phân loại event.
Vertx dựa và thông tin đính kèm để gửi nó tới Queue của Verticle.
sau đó VertX sẽ assign thread để chạy event ở của Verticle
 */
public class TimerVerticle extends AbstractVerticle {

	@Override
	public void start(Future<Void> startFuture) throws Exception {	
		System.out.println("MyVerticle.start(): thread="+Thread.currentThread().getId());
		
//		vertx.setTimer(delay, handler)
//		vertx.setPeriodic(delay, handler)
		//timer này chạy trên cùng context với Vertical => nghĩa là chạy tuần tự
	    //trường hợp Stardard verticle vẫn chạy tuần tự (đã test)
	    //trường hợp working thread thì vẫn phải chạy tuần tự (đã test)

		long timerID = vertx.setTimer(3000,new  Handler<Long>() {
			//run on thread of Verticle
		    @Override
		    public void handle(Long aLong) {
		    	System.out.println("timer1.1: thread="+Thread.currentThread().getId());
		    }
		});
		
		timerID = vertx.setTimer(5000,new  Handler<Long>() {
			//run on thread of Verticle
		    @Override
		    public void handle(Long aLong) {
		    	System.out.println("timer1.2: thread="+Thread.currentThread().getId());
		    }
		});
		
//		vertx.cancelTimer(timerID);
	}

	@Override
	public void stop(Future<Void> stopFuture) throws Exception {
		System.out.println("MyVerticle.stop(): thread=" + Thread.currentThread().getId());
	}
	

}
