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
public class App81_https_Server extends AbstractVerticle {

	public static void main(String[] args) {
		Vertx vertx = Vertx.vertx();
		vertx.deployVerticle(new App81_https_Server()); 	
	}

	@Override
	public void start() throws Exception {
		//hàm này phải đc gọi để xác định quá trình Deploy thành công (thì vertx.deploymentIDs() cập nhật giá trị)
		// hoặc phải gọi hàm startFuture.complete()
		super.start();  // super đã gọi ham startFuture.complete()
		
		
		// đọc tài liệu Security để hiểu về SSL
		// Từ file server-keystore.jks Https server sẽ lấy Public key để gửi tới Client ở SSL handshake. còn Private key giữ lại
		// key-pair này dùng để lấy Shared-key là symmetric key để mà hóa dữ liệu truyền nhận giữa Client và server.
		// shared-key: là do SSL client quyết định (là symmetric key)
		// dùng OpenSSL để tạo file server-keystore.jks
		URL keyStoreURL = App81_https_Server.class.getResource("server-keystore.jks");  // file này chưa Key-pair.key thường gọi là private key
		
		/**
		 App81_https_Server.class.getResource("/") = root = main/resources/ = main/java/
		 App81_https_Server.class.getResource("abc") = root/pakage_name/abc
		 */
		String path = keyStoreURL.getPath();
		System.out.println("keyStoreURL = " + path); //  root/pakage_name/server-keystore.jks
		
		HttpServer server =	vertx.createHttpServer(new HttpServerOptions()
									.setSsl(true)					// https protocol
//									.setKeyCertOptions(options)   // keyCertificate để check với Certificate Authen Server (phải trả tiền để mua) => ko cần
									.setKeyStoreOptions(
											new JksOptions().setPath(path).setPassword("wibble")   //password để truy cập file key-pair chứa private/public key
											//luc tạo key-pair tốt nhất là ko dùng password
											//OpenSSL generate key-pair random
										));
		
		// =================================== Guide =================================
		System.out.println("*** step1: please test Browsers with url:" + " https://localhost:4443/");
		System.out.println("*** step2: please test Browsers with url:" + " https://localhost:4443/test");
		System.out.println("*** step3: run App83_Client_https");
		
		// Lưu ý: uri = /favicon.ico  là Browser request để lấy Icon của website
		server.requestHandler(new Handler<HttpServerRequest>() {
			
			@Override
			public void handle(HttpServerRequest req) {
				System.out.println("uri = " + req.uri());
				//showHttpRequestHeader(req);
				req.response().putHeader("content-type", "text/html").end("<html><body><h1>Hello from vert.x!</h1></body></html>");			
			}
		}).listen(4443);

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
