package hung.com.websocket.client;

import java.util.Iterator;
import java.util.Map.Entry;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.MultiMap;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpClientResponse;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.WebSocket;
import io.vertx.core.http.WebSocketConnectOptions;

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
public class WebSocketClientVerticle extends AbstractVerticle {

	@Override
	public void start(Future<Void> startFuture) throws Exception {
		io.vertx.core.http.HttpClient httpClient = vertx.createHttpClient(); 

		// các header yêu cau của websocket da dc add vao roi
		MultiMap headers = MultiMap.caseInsensitiveMultiMap(); 
		headers.add("testHeader", "ko co viec gi kho");

		WebSocketConnectOptions connectOptions = new WebSocketConnectOptions()
				.setPort(8080)
				.setHost("localhost")
				.setURI("/websocket_path")
				//				.setSubProtocols(subProtocols)
				.setHeaders(headers);

		//================================ WebSocket Handshake via http protocol =======================
		//Step1:  client gửi http-request theo định dạng Http- request 

		httpClient.webSocket(connectOptions, new Handler<AsyncResult<WebSocket>>() {

			@Override
			public void handle(AsyncResult<WebSocket> asyncResult) {
				//Step2: client nhận http-response theo định dạng http-request => tại đây đã handshake thành công (đã nhận http-response)
				if (asyncResult.succeeded()) {
					WebSocket webSocket = asyncResult.result();
					System.out.println("****websocket Connected! ");

					/**
					 *  ko lay dc Http-response từ webSocket
					 */

					// Handler phai khai bao trc khi Write to websocket
					//========================= Reciever Message ============
					// Asynchronous thi Reciever luon ở handler
					/**
					      writeTextMessage(data) Server  => textMessageHandler() Client
					      writeBinaryMessage(data) Server => binaryMessageHandler(handler) Client
					 Client và server vai tro nhu nhau:
					      writeTextMessage(data) Client  => textMessageHandler() Server
					      writeBinaryMessage(data) Client => binaryMessageHandler(handler) Server
					 */
					webSocket.textMessageHandler(new Handler<String>() {

						@Override
						public void handle(String textFrame) {
							System.out.println(" textFrame from server = " + textFrame);

						}
					});

					//========================== Close handler =================
					webSocket.closeHandler(new Handler<Void>() {

						@Override
						public void handle(Void event) {
							System.out.println("-----websocket closed! ");

						}
					});

					//========================== Send Message ==================
					/**
				      writeTextMessage(data) Server  => textMessageHandler() Client
				      writeBinaryMessage(data) Server => binaryMessageHandler(handler) Client
				 Client và server vai tro nhu nhau:
				      writeTextMessage(data) Client  => textMessageHandler() Server
				      writeBinaryMessage(data) Client => binaryMessageHandler(handler) Server
				 */
					String message = "#######  hello from client";
					webSocket.writeTextMessage(message);

					//========================== Close===========================
					/*					webSocket.close(new Handler<AsyncResult<Void>>() {
						@Override
						public void handle(AsyncResult<Void> event) {
							System.out.println("****close! ");

						}
					});*/
				}

			}
		});

	}


}