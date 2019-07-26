package hung.com.eventbus;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.eventbus.EventBus;

public class EventBusSenderVerticle extends AbstractVerticle {
	String name;
	

    public EventBusSenderVerticle(String name) {
		super();
		this.name = name;
	}


	public void start(Future<Void> startFuture) {
    	System.out.println("<= EventBusSenderVerticle.start():"+ "name="+ name +
    								",thread="+Thread.currentThread().getId());
    	
    	//publish() method sends the message to all verticles listening on a given address.
    	// address: của bên nhận và gửi phải giống nhau để giao tiếp với nhau
    	// Message<String> = address + header + body
    	// body kiểu String
    	EventBus evenbus = vertx.eventBus();
    	String address = "anAddress";
    	
    	//bên Pulish ko cần phải register gì cả, vì nó ko phải trigger handler để bắt sự kiện
    	evenbus.publish(address, "=>Message publicly");  //có thể Message Content String bằng kiểu khác: object, int,float...
        
        //The send() method sends the message to just one of the listening verticles.
        //Which verticle receives the message is decided by Vert.x 
        vertx.eventBus().send("anAddress", "=>Message only one Receiver");
    }
}
