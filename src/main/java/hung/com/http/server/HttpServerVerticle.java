package hung.com.http.server;

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
public class HttpServerVerticle extends AbstractVerticle{
	private HttpServer httpServer = null;

	private Buffer totalBuffer = Buffer.buffer(4000); //4k byte
	//run on a worker thread
	@Override
	public void start(Future<Void> startFuture) throws Exception {
		System.out.println("MyVerticle started! port=81: thread="+Thread.currentThread().getId());

		HttpServerOptions httpServerOptions = new HttpServerOptions()
				.setMaxHeaderSize(4000)
				.setReceiveBufferSize(8000)
				.setSendBufferSize(8000);

		httpServer = vertx.createHttpServer(httpServerOptions);


		httpServer.connectionHandler(new Handler<HttpConnection>() {		
			@Override
			public void handle(HttpConnection connect) {
				System.out.println("http connectionHandler: thread="+Thread.currentThread().getId());
				//reject connect neu Server overload or number of connect > Max connect

				// Deploy Verticle de xu ly http request/response tren Threadpool khac nhu the se toi uu hon
				// 1 http request/reponse context nen xu ly tren 1 thread (threadpool worker= false) de dam bao Order
				//truong hop context lay du lieu tu 2 service khac nhau thi phai dam bao tinh thu tu

			}
		});

		// quá trình parser thực hiện trên EventLoopPool của Vertx, chứ ko phải trên thread của context này.
		// thread của context này chỉ để trả về event thôi.
		httpServer.requestHandler(new Handler<HttpServerRequest>() {
			@Override
			public void handle(HttpServerRequest request) {
				//Request chỉ 1 lần duy nhất, vì thế các Request ko có tính phụ thuộc tuần tự => nên dùng Worker Verticle với Thread Pool
				//tại đây phần header đã ok => the same as Tomcat NIO
				System.out.println("http requestHandler: thread="+Thread.currentThread().getId());

				/**
				 * url = http://localhost:81/atm?id=1&command=ejm
				 * uri = /atm?id=1&command=ejm
				 * path = /atm
				 * id = 1
				 * command = ejm
				 */

				System.out.println("path = "+ request.path());

				String id = request.getParam("id");
				System.out.println("id = "+ id);

				String command = request.getParam("command");
				System.out.println("command = "+ command);
				//=========================== body of request ==============
				if(request.method() == HttpMethod.POST){
					System.out.println("POST request");
					
					//===================================================== read body Event ===============
					//Mỗi lần read body Event thì nó trigger handler này
					// chỉ dùng cách này với body loại là chunk thôi (vd: truyền Video, audio....)
					request.handler(new Handler<Buffer>() {
						@Override
						public void handle(Buffer buffer) {
							// đoạn code này chạy trên Thread cấp cho Verticle (trong context của Verticle) => khi gọi lặp lại sẽ vẫn đảm bảo tính tuần tự.
							// (dùng Standard Vertical để đảm bảo tính tuần tự)
							// nếu muốn chạy trên thread khác thì dùng Blocking Code
							//Vertx dùng event (ko phải stream) nên nó biết khi nào kết thúc
							// event will be triggered until body end

						}
					});
					
					//============================================ finish read body ==================
					// dùng cái này với trường hợp ko phải là chunk request (vd: json, file)
					// tại đây để lấy toàn bộ nội dung của body
					request.bodyHandler(new Handler<Buffer>() {
						
						@Override
						public void handle(Buffer buffer) {
							//buffer chứa toàn bộ nội dung của body request
							
						}
					});
					
					//============================================== end of body request ==============
					// dùng với trường hợp chunk request thôi
					request.endHandler(new Handler<Void>() {
						
						@Override
						public void handle(Void event) {
							// tại đây kết thúc body request.
							
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
		});

		httpServer.listen(81, new Handler<AsyncResult<HttpServer> >() {			
			@Override
			public void handle(AsyncResult<HttpServer> result) {
				if (result.succeeded()) {
					// thông báo khởi tạo thành công, cho phia vertx.deployVerticle(verticle, handler<AsyncResult<void>>)
					// <xem Future concept>
					startFuture.complete();  
				} else {
					startFuture.fail(result.cause());
				}

			}
		});//port = 81
	}

	// run on a worker thread
	@Override
	public void stop(Future<Void> stopFuture) throws Exception {
		System.out.println("MyVerticle stopped!");
	}
}
