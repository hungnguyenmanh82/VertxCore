package hung.com.http.server;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerRequest;

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
		        System.out.println("incoming request!: thread="+Thread.currentThread().getId());
		        
		        System.out.println("uri = "+ request.uri());
		        System.out.println("path = "+ request.path());
		        request.getParam("p1");
		        
		        if(request.method() == HttpMethod.POST){
		        	//asynchronous get body of post request (non-blocking)
		            request.handler(new Handler<Buffer>() {
		                @Override
		                public void handle(Buffer buffer) {
		                    //dựa vào Buffer để biết khi nào kết thúc body
		                	//buffer trả về -1 là kết thúc.
		                	//ở tầng giao thức http sẽ có cách xác định khi nào request kết thúc
		                }
		            });
		        }
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
