package hung.com.websocket.server.app1;

import java.util.Iterator;
import java.util.Map.Entry;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.DeploymentOptions;
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
import io.vertx.core.http.ServerWebSocket;

/**
//================================ WebSocket Handshake via http protocol =======================
Step1:  client gửi http-request theo định dạng Http- request 
Step2: client nhận http-response theo định dạng http-request.

//=========================== websocket protocol ====================================
Step3: client và server giao tiếp qua Message theo 2 chiều (ko phân biệt client- server). 
Message = header + body  -> theo format của websocket protocol. https://tools.ietf.org/html/rfc6455 (ko cần đọc)
Chú ý: websocket protocol là chuẩn riêng Khác hẳn http protocol (tất nhiên vẫn base trên TCP protocol).
 Sau khi thiết lập handshake qua http protocol, nó hoàn toàn tuân thủ websocket protocol về gửi nhận dữ liệu (khác với tcp protocol). 

 */

public class WebsocketServerVerticle_1stWay extends AbstractVerticle{
	private HttpServer httpServer = null;

	private Buffer totalBuffer = Buffer.buffer(4000); //4k byte
	//run on a worker thread
	@Override
	public void start(Future<Void> startFuture) throws Exception {
		System.out.println("MyVerticle started! port=8080: thread="+Thread.currentThread().getId());

		HttpServerOptions httpServerOptions = new HttpServerOptions()
				.setMaxHeaderSize(4000)
				.setReceiveBufferSize(8000)
				.setSendBufferSize(8000);

		httpServer = vertx.createHttpServer(httpServerOptions);

		// TCP connect
		httpServer.connectionHandler(new Handler<HttpConnection>() {		
			@Override
			public void handle(HttpConnection connect) {
				System.out.println("************ http connectionHandler: thread="+Thread.currentThread().getId());
				System.out.println("Client Address="+connect.remoteAddress().toString());
			}
		});


		//================================ WebSocket Handshake via http protocol =======================
		// Step1:  client gửi http-request theo định dạng Http- request => tại đây đã nhận đc http-request 
		
		httpServer.requestHandler(new Handler<HttpServerRequest>() {
			@Override
			public void handle(HttpServerRequest request) {
				System.out.println(" ============ http requestHandler: "  );
				//cach lay thong tin tu header ra
				showHttpRequestHeader(request);
				
				//request.netSocket() => lay TCP socket neu cần
				
				if (request.path().equals("/websocket_path")) {
					System.out.println("Upgrade to WebSocket " );
					
					// ========================================= upgrade ================================
					// Step2: server gửi http-response accept Websocket
					ServerWebSocket websocket = request.upgrade();
					
					// các handler cần đc khai bao trc khi write
					//=========================================== Reciever Message =======================
					// Opcode = Text Frame
					websocket.textMessageHandler(new Handler<String>() {
						
						@Override
						public void handle(String textFrame) {
							System.out.println(" textFrame from client = " + textFrame);
							
						}
					});
					
					//======================================== Close Handler ===========================
					websocket.closeHandler(new Handler<Void>() {
						
						@Override
						public void handle(Void event) {
							System.out.println("-------- websocket closed ");
							
						}
					});
					
					//========================================= Send Message ============================
					/**
				      writeTextMessage(data) Server  => textMessageHandler() Client
				      writeBinaryMessage(data) Server => binaryMessageHandler(handler) Client
				 Client và server vai tro nhu nhau:
				      writeTextMessage(data) Client  => textMessageHandler() Server
				      writeBinaryMessage(data) Client => binaryMessageHandler(handler) Server
				 */
					websocket.writeTextMessage(" ##### Hello from Server ");

				} else {
					// send Http-response to reject Websocket
					request.response().setStatusCode(400).end();
				}
			}
		});


		httpServer.listen(8080, new Handler<AsyncResult<HttpServer> >() {			
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
		});//port = 8080
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
		 * path = /atm
		 * params  id = 1
		 * parames command = ejm
		 */
		
		// ================================= show request header =============================
		// 3 tham số sau là parsing từ:  là dòng đàu tiên của Request Header
		System.out.println(" http method: " + request.method());  // request.method() = Enum {GET,POST,...}
		System.out.println(" uri: " + request.uri());
		System.out.println(" http version: " + request.version());  //http version
		
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
