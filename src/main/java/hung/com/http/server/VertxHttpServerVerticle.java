package hung.com.http.server;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;

/**
 * 
http://tutorials.jenkov.com/vert.x/http-server.html

Vertical đăng ký nhận HttpServer event với Vertx.
Vertx thay mặt Vertical làm mọi thứ, bắt event.
Khi có event, Vertx sẽ trả về và gọi Vertical xử lý
Tất nhiên, Vertx phải cấp phát thread cho Verticle 
 */
public class VertxHttpServerVerticle extends AbstractVerticle{
	private HttpServer httpServer = null;

	//run on a worker thread
	@Override
	public void start() throws Exception {
		System.out.println("MyVerticle started! port=81: thread="+Thread.currentThread().getId());
		httpServer = vertx.createHttpServer();
		
		httpServer.requestHandler(new Handler<HttpServerRequest>() {
		    @Override
		    public void handle(HttpServerRequest request) {
		    	//tại đây phần header đã ok => the same as Tomcat NIO
		        System.out.println("incoming connect request!: thread="+Thread.currentThread().getId());
		        
		        /**
		         * url = http://localhost:81/atm?id=1&command=ejm
		         * uri = /atm?id=1&command=ejm
		         * path = /atm
		         * id = 1
		         * command = ejm
		         */
		        System.out.println("uri = "+ request.uri()); // 
		        System.out.println("path = "+ request.path());
		        
		        String id = request.getParam("id");
		        System.out.println("id = "+ id);
		        
		        String command = request.getParam("command");
		        System.out.println("command = "+ command);
		        //=========================== body of request ==============
		        if(request.method() == HttpMethod.POST){
		        	System.out.println("POST request");
		        	//asynchronous get body of post request (non-blocking).
		        	// event is called many times
		            request.handler(new Handler<Buffer>() {
		                @Override
		                public void handle(Buffer buffer) {
		                    //Vertx dùng event (ko phải stream) nên nó biết khi nào kết thúc
		                	// event will be triggered until body end
		                	//ở tầng giao thức http sẽ có cách xác định khi nào request kết thúc
		                }
		            });
		        }
		        
		        //============================= response ===================
		        HttpServerResponse response = request.response();
		        response.setStatusCode(200);
		        String body = "Verticle HttpServer body";
		        //header phải gửi trc
		        response.headers()
		            .add("Content-Length", String.valueOf(body.length()))
		            .add("Content-Type", "text/html")
		        ;
		        
		        //this function will return immediately
		        response.write(body); //asynchronous write by Vertx
		        //asynchronously check whether socket's write buffer is full or not
//		        response.writeQueueFull(); 
		        response.end(); //close socket
		        //httpServer.close();
		    }
		});

		httpServer.listen(81);
	}
	
	// run on a worker thread
	@Override
	public void stop(Future<Void> stopFuture) throws Exception {
		System.out.println("MyVerticle stopped!");
	}
}
