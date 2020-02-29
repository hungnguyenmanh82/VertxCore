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


	public void start(Future<Void> startFuture) throws Exception {
		super.start(startFuture);
    	System.out.println("<= EventBusSenderVerticle.start():"+ "name="+ name +
    								",thread="+Thread.currentThread().getId());
    	
    	//publish() method sends the message to all verticles listening on a given address.
    	// address: của bên nhận và gửi phải giống nhau để giao tiếp với nhau
    	// Message<String> = address + header + body
    	// body kiểu String
    	EventBus evenbus = vertx.eventBus();
    	String address = "anAddress";
    	
    	//Pulish(): 
    	// tất cả Verticle đăng ký nhận dùng 1 address, đều nhận đc nó.
    	evenbus.publish(address, "=>Message publicly");  //có thể Message Content String bằng kiểu khác: object, int,float...
        
        //send(): 
    	// Nhiều Verticle đăng ký nhận dùng 1 address, nhưng chỉ có 1 verticle nhận đc
        vertx.eventBus().send("anAddress", "=>Message only one Receiver");
    }
}
