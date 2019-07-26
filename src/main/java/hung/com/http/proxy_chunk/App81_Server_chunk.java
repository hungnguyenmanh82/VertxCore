package hung.com.http.proxy_chunk;

import java.util.Iterator;
import java.util.Map.Entry;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Handler;
import io.vertx.core.MultiMap;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServerRequest;

/*
 * @author <a href="http://tfox.org">Tim Fox</a>
 */
public class App81_Server_chunk extends AbstractVerticle {

	public static void main(String[] args) {
		Vertx vertx = Vertx.vertx();
		vertx.deployVerticle(new App81_Server_chunk()); 	
	}

	@Override
	public void start() throws Exception {
		
		// Trigger khi nhận đầy đủ header
		vertx.createHttpServer().requestHandler(new Handler<HttpServerRequest>() {
			@Override
			public void handle(HttpServerRequest httpServerRequest) {
				// tại đây đã nhận đầy đủ header
				showHttpRequestHeader(httpServerRequest);

				//============================================================================
				//trigger mỗi lần đọc body-request buffer
				// dùng đc cho cả trường hợp chunk và fix content-length
				httpServerRequest.handler(new Handler<Buffer>() {
					@Override
					public void handle(Buffer data) {
						System.out.println("Got data: " + data.toString("ISO-8859-1"));
						
					}
				});
				
				//===============================================================================
				// dùng với trường hợp chunk request thôi
				// tại đây đã nhận đầy đủ Request-header và request-body
				httpServerRequest.endHandler(new Handler<Void>() {
					@Override
					public void handle(Void event) {
						//set content-type is chunk in response-header
						httpServerRequest.response().setChunked(true);

						for (int i = 0; i < 10; i++) {
							// mỗi lần gửi là 1 chunk = chunk-header + chunk-body
							// chunk-header chứa chunk-lenght
							httpServerRequest.response().write("server-data-chunk-" + i);
						}
						
						// chunk-length = 0 sẽ kết thúc response body
						httpServerRequest.response().end();
						
					}
				});
				
			}
		}).listen(8282);

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