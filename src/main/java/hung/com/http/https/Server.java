package hung.com.http.https;

import java.net.URL;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.net.JksOptions;

/*
 * @author <a href="http://tfox.org">Tim Fox</a>
 */
public class Server extends AbstractVerticle {

	public static void main(String[] args) {
		Vertx vertx = Vertx.vertx();
		vertx.deployVerticle(new Server()); 	
	}

	@Override
	public void start() throws Exception {

		// đọc tài liệu Security để hiểu về SSL
		// Từ file server-keystore.jks Https server sẽ lấy Public key để gửi tới Client ở SSL handshake. còn Private key giữ lại
		// key-pair này dùng để lấy Shared-key là symmetric key để mà hóa dữ liệu truyền nhận giữa Client và server.
		// shared-key: là do SSL client quyết định.
		URL keyStoreURL = Server.class.getResource("server-keystore.jks");  // file này chưa Key-pair.key thường gọi là private key
		String path = keyStoreURL.getPath();
		
		System.out.println("keyStoreURL = " + path);
		
		HttpServer server =	vertx.createHttpServer(new HttpServerOptions()
									.setSsl(true)
//									.setKeyCertOptions(options)   //
									.setKeyStoreOptions(
											new JksOptions().setPath(path).setPassword("wibble")   //p
										));
		
		// Lưu ý: uri = /favicon.ico  là Browser request để lấy Icon của website
		server.requestHandler(new Handler<HttpServerRequest>() {
			
			@Override
			public void handle(HttpServerRequest req) {
				System.out.println("uri = " + req.uri());				
				req.response().putHeader("content-type", "text/html").end("<html><body><h1>Hello from vert.x!</h1></body></html>");			
			}
		}).listen(4443);

	}
}
