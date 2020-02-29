package hung.com.http.proxy;

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
public class App81_Server extends AbstractVerticle {

	public static void main(String[] args) {
		Vertx vertx = Vertx.vertx();
		vertx.deployVerticle(new App81_Server()); 	
	}

	@Override
	public void start() throws Exception {
		
		System.out.println("Server: http Server start at port = 8282");
		System.out.println("*** step1: please test Browsers with url:" + " http://localhost:8282/");
		System.out.println("*** step2: please test Browsers with url:" + " http://localhost:8282/test");

		//Luu ý 2 chieu Request và Response là độc lập, ko liên quan tơi nhau
		// Trigger khi nhận đầy đủ header
		vertx.createHttpServer().requestHandler(new Handler<HttpServerRequest>() {
			@Override
			public void handle(HttpServerRequest httpServerRequest) {
				System.out.println("*****Server: requestHandler đã nhận request-header");
				// tại đây đã nhận đầy đủ header
				//showHttpRequestHeader(httpServerRequest);

				//============================================================================
				//Ko dùng hàm này vì với chunk thì nó đã tach phần header đi rồi
				// phan body ban chat là forward socket stream thoi. Phan chunk  nen tach ra 1 service rieng
				httpServerRequest.bodyHandler(new Handler<Buffer>() {
					
					@Override
					public void handle(Buffer event) {
						System.out.println("Server: httpServerRequest.bodyHandler()");
						
					}
				});
				
				// neu goi endHandler thi sẽ ko goi BodyHandler  (đã test)
				// chi dung ham nay voi Chunk request
/*				httpServerRequest.endHandler(new Handler<Void>() {
					
					@Override
					public void handle(Void event) {
						System.out.println("Server: httpServerRequest.endHandler()");
						
					}
				});*/
				//===============================================================================
				//Luu ý 2 chieu Request và Response là độc lập nên tien hanh song song tren thread khác dc (buffer khac nhau)
				// gửi reponse trở lại
				httpServerRequest.response().putHeader("testField1", "thich nghi, relax 80%");
				httpServerRequest.response().putHeader("yChi", "where do I find it?");
				
				// tai day moi gui response toi client
				httpServerRequest.response().end("muc dich song, thich nghi, relax, happy, y chi, uu diem");
				
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