package hung.com.http.server;

import java.util.Iterator;
import java.util.Map.Entry;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.MultiMap;
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
		//hàm này phải đc gọi để xác định quá trình Deploy thành công (thì vertx.deploymentIDs() cập nhật giá trị)
		// hoặc phải gọi hàm startFuture.complete()
//		super.start(startFuture);  // đã gọi startFuture.complete()
		
		/**
		 * url = http://localhost:81/atm?id=1&command=ejm
		 * uri = /atm?id=1&command=ejm
		 * path = /atm         => lay path tu Uri trong http request header
		 * param id = 1
		 * param command = ejm
		 */
		System.out.println(this.getClass().getName()+ ".start(): thread="+Thread.currentThread().getId() + ", ThreadName="+Thread.currentThread().getName());
		System.out.println("test1: http://localhost:81/atm?id=1&command=ejm");
		System.out.println("test2: http://localhost:81/abcdef?command=abc&id=888");

		HttpServerOptions httpServerOptions = new HttpServerOptions()
				.setMaxHeaderSize(4000)
				.setReceiveBufferSize(8000)   //of socket
				.setSendBufferSize(8000);    // of socket

		httpServer = vertx.createHttpServer(httpServerOptions);


		httpServer.connectionHandler(new Handler<HttpConnection>() {		
			@Override
			public void handle(HttpConnection connect) {
				System.out.println("ConnectHandler:"+ "thread="+Thread.currentThread().getId() + ", ThreadName="+Thread.currentThread().getName());
				//reject connect neu Server overload or number of connect > Max connect

				// Deploy Verticle de xu ly http request/response tren Threadpool khac nhu the se toi uu hon
				// 1 http request/reponse context nen xu ly tren 1 thread (threadpool worker= false) de dam bao Order
				//truong hop context lay du lieu tu 2 service khac nhau thi phai dam bao tinh thu tu
				
				/**
				 * http 1.1 dùng keep alive connect, nên nhiều request chung 1 socket connect (đã test với Chrome và Wireshark)
				 */
			}
		});

		// quá trình parser thực hiện trên EventLoopPool của Vertx, chứ ko phải trên thread của context này.
		// thread của context này chỉ để trả về event thôi.
		httpServer.requestHandler(new Handler<HttpServerRequest>() {
			@Override
			public void handle(HttpServerRequest request) {
				//Request chỉ 1 lần duy nhất, vì thế các Request ko có tính phụ thuộc tuần tự => nên dùng Worker Verticle với Thread Pool
				//tại đây phần header đã nhân dc het=> the same as Tomcat NIO
				System.out.println("http requestHandler: thread="+Thread.currentThread().getId()+ ", ThreadName="+Thread.currentThread().getName());

				// cach lay thong tin tu Header ra
				showHttpRequestHeader(request);
				
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
				//Luu ý 2 chieu Request và Response là độc lập nên tien hanh song song tren thread khác dc (buffer khac nhau)
				//cách 1: phần này nên dùng Blocking code để run nó trên 1 thread khác
				// cách 2: chuyển phần này sang Verticle khác để xử lý => vì còn xử lý call database và other Micro Service
				// xem vd ở mục server.pool.*
				HttpServerResponse response = request.response();
				response.setStatusCode(200);
				String body = "Verticle HttpServer body";
				//header phải gửi trc
				response.headers()
								.add("Content-Length", String.valueOf(body.length()))
								.add("Content-Type", "text/html");

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
	
	private void showHttpRequestHeader(HttpServerRequest request){
		/**
		 * url = http://localhost:81/atm?id=1&command=ejm
		 * uri = /atm?id=1&command=ejm
		 * path = /atm         => lay path tu Uri trong http request header
		 * param id = 1
		 * param command = ejm
		 */
		
		// ================================= show request header =============================
		System.out.println("\n");
		System.out.println("=========================================  a request header");
		System.out.println(" url: " + request.absoluteURI());
		// 3 tham số sau là parsing từ:  là dòng đàu tiên của Request Header
		System.out.println(" http method: " + request.method());  // request.method() = Enum {GET,POST,...}
		System.out.println(" uri: " + request.uri());
		System.out.println(" http version: " + request.version());  //http version
		
	    // ================== path and params lay tu URI ======================
		System.out.println("path = "+ request.path()); 
		//
		String id = request.getParam("id");
		System.out.println("id = "+ id);
		//
		String command = request.getParam("command");
		System.out.println("command = "+ command);
		
		// ============================
		// System.out.println(" host " + request.host()); // thuoc Header map{key:value}
		// các tham số còn lại trong header đều có cấu trúc {key, value} kể cả cookies và các field do user add vào
		System.out.println(" ++++++ httpRequest Headers map{key:value}: "); //cookies also here
		
		MultiMap header = request.headers();
		
		Iterator<Entry<String,String>> iterator = header.iterator();
		
		while(iterator.hasNext()) {
			Entry<String,String> item = (Entry<String,String>) iterator.next();
			System.out.println(item.toString() );
		}
		
		System.out.println(" ++++++ End: httpRequest Headers");
		
	}
}
