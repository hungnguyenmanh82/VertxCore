package hung.com.http.https;

import java.net.URL;
import java.util.Iterator;
import java.util.Map.Entry;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Handler;
import io.vertx.core.MultiMap;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.net.JksOptions;

/*
 * @author <a href="http://tfox.org">Tim Fox</a>
 */
public class App82_http_Server_redirect_https extends AbstractVerticle {

	public static void main(String[] args) {
		Vertx vertx = Vertx.vertx();
		vertx.deployVerticle(new App82_http_Server_redirect_https()); 	
	}

	@Override
	public void start() throws Exception {
		//hàm này phải đc gọi để xác định quá trình Deploy thành công (thì vertx.deploymentIDs() cập nhật giá trị)
		// hoặc phải gọi hàm startFuture.complete()
		super.start();  

		HttpServer httpServer = vertx.createHttpServer();
		
		
		// =========================== Guide =======================================
		System.out.println("*** step1: run App82_http_Server_redirect_https ");
		System.out.println("*** step2: run App81_https_Server");	
		System.out.println("*** step3: please test Browsers with url:" + " http://localhost:8080/");
		System.out.println("*** step3: please test Browsers with url:" + " http://localhost:8080/test");

		
		// Lưu ý: uri = /favicon.ico  là Browser request để lấy Icon của website
		httpServer.requestHandler(new Handler<HttpServerRequest>() {
			
			@Override
			public void handle(HttpServerRequest req) {
				System.out.println("redirect to https: uri = " + req.uri());
				//showHttpRequestHeader(req);
				// statusCode = 301 is redirect permanent (có cache)
				// statusCode = 302 is redirect temporary  (ko cache)
				String httpsPath = "https://localhost:4443" + req.uri(); 
				
				req.response().putHeader("location", httpsPath).setStatusCode(302).end();
			}
		}).listen(8080);

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
