package hung.com.http.sendfile;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerRequest;

/*
 * @author <a href="http://tfox.org">Tim Fox</a>
 */
public class App5_Server_downloadFile extends AbstractVerticle {

	public static void main(String[] args) {
		Vertx vertx = Vertx.vertx();
		vertx.deployVerticle(new App5_Server_downloadFile()); 	
	}

	/**
	Debug Mode:   see folder "$Project/target/classes/output.txt"
	root = “/”  = "main/resources" = "main/java" = “$Project/target/classes/”
	
	Nên dùng Vertx Web với static-resource để dùng tính năng cache file và map file sẽ hay hơn
	 */
	@Override
	public void start() throws Exception {

		System.out.println("try with: http://localhost:8080/");

		
		vertx.createHttpServer().requestHandler(new Handler<HttpServerRequest>() {
			@Override
			public void handle(HttpServerRequest req) {
				
				System.out.println("handle() path =" + req.path());
				
				
				String filename = null;
				if (req.path().equals("/")) {
					filename = "/webroot/index.html";
				} else if (req.path().equals("/page1.html")) {
					filename = "/webroot/page1.html";
				} else if (req.path().equals("/page2.html")) {
					filename = "/webroot/page2.html";
				} else {
					req.response().setStatusCode(404).end();
				}
				
				// vertx sử dụng file cache khi đọc static file
				// quá trình readfile là asynchronous
				// bug: request lần đầu tiên ko tìm thấy file  (cần nâng version vertx >3.7.0)
				if (filename != null) {
					req.response().sendFile(filename);
				}
				
			}
		}).listen(8080);

	}
}
