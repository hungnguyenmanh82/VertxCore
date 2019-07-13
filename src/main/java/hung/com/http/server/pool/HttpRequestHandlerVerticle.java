package hung.com.http.server.pool;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpConnection;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
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
public class HttpRequestHandlerVerticle extends AbstractVerticle{
	private HttpServerRequest request = null;

	private Buffer totalBuffer = Buffer.buffer(4000); //4k byte
	//run on a worker thread

	public HttpRequestHandlerVerticle(HttpServerRequest request) {
		super();
		
		// chuyển Request sang Vertical context để có thể thao tác với nhiều Microservice event trên 1 thread của context
		this.request = request;
	}


	@Override
	public void start(Future<Void> startFuture) throws Exception {
		System.out.println("HttpRequestHandlerVerticle : thread="+Thread.currentThread().getId());

		/**
		 * url = http://localhost:81/atm?id=1&command=ejm
		 * uri = /atm?id=1&command=ejm
		 * path = /atm
		 * id = 1
		 * command = ejm
		 */

		// path() = favicon.icon  là request hỏi Icon của website do Browser gửi tới vertx
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
			// Nên di chuyển phần code này vào Verticle khác (Vertx Context khác) để xử lý tuần tự event
			// (dùng Standard Vertical để đảm bảo tính tuần tự)
			request.handler(new Handler<Buffer>() {
				@Override
				public void handle(Buffer buffer) {
					// đoạn code này chạy trên Thread cấp cho Verticle (trong context của Verticle) => khi gọi lặp lại sẽ vẫn đảm bảo tính tuần tự.
					// (dùng Standard Vertical để đảm bảo tính tuần tự)
					// nếu muốn chạy trên thread khác thì dùng Blocking Code
					//Vertx dùng event (ko phải stream) nên nó biết khi nào kết thúc
					// event will be triggered until body end
					//ở tầng giao thức http sẽ có cách xác định khi nào request kết thúc
				}
			});
		}

		//============================= response ===================
		//cách 1: phần này nên dùng Blocking code để run nó trên 1 thread khác
		// cách 2: chuyển phần này sang Verticle khác để xử lý
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



	// run on a worker thread
	@Override
	public void stop(Future<Void> stopFuture) throws Exception {
		System.out.println("MyVerticle stopped!");
	}
}
