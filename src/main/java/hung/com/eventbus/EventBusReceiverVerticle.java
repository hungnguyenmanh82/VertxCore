package hung.com.eventbus;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.Message;

/**
 * 
 * nhiều  Verticle đăng ký với  1 Vertx (Vertx là 1 instance sẽ khởi tạo queue và threads để xử lý event)
 * 1 Verticle instance chỉ attach duy nhất 1 Vertx instance.
 * các Verticle sẽ chạy trên Thread do Vertx chỉ định. Là 1 thread.
 * Nhiều Verticle sẽ chỉ chạy trên 1 thread.
 * 
 * Verticle thực chất giữ function callback để khi có event thì Vertx gọi. 
 * Verticle chứa Queue của nó. Nhưng Vertx sẽ truy cập queue này và call Verticle run
 * 
 * Tuy nhiên Vertx sinh ra nhiều thread để quản lý event:
 *  + 1 thread để run callback function khi có event
 *  + 1 thread để run handler của Vertx
 */

public class EventBusReceiverVerticle extends AbstractVerticle {
	private String name;	
	
    public EventBusReceiverVerticle(String name) {
		super();
		this.name = name;
	}


	public void start(Future<Void> startFuture) {
    	System.out.println("=> EventBusReceiverVerticle.start():"+ "name="+ name +
    									", thread="+Thread.currentThread().getId());
    	
    	//vertx member of AbstractVerticle  => where this Verticle was deployed
    	//consumer=(register to receive event): this Verticle context đăng ký nhận Event từ Vertx
    	// event đc xử lý trên threadpool của Verticle context này
    	String EventId = "anAddress";
    	vertx.eventBus().consumer(EventId,new Handler<Message<String>>() { //có thể thay String bằng kiểu khác: object, int,float...
			@Override
			public void handle(Message<String> message) {
				System.out.println("***Handle(): EventBusReceiverVerticle:Consumer():"+
											"name="+ name +
											", thread="+Thread.currentThread().getId());
				System.out.println("receive Message: name="+ name +  
											", address="+ message.address()+ 
												", body=" +message.body());
			}
    		
		} );
    	
    	//Java 8 Lambda syntax => the same above
/*    	vertx.eventBus().consumer("anAddress", message -> {
            System.out.println("1 received message.body() = " + message.body());
        });*/
    }
}
