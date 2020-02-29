package hung.com.http.sendfile;

import java.net.URL;

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

		System.out.println("***  try Browser with url: http://localhost:8080/");
		System.out.println("*** try Browser with url: http://localhost:8080/page1.html");
		System.out.println("*** try Browser with url: http://localhost:8080/page2.html");
		System.out.println("*** try Browser with url: http://localhost:8080/abc");
		
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
				
				/**
				 * lúc compile sẽ gộp "main/resources/" và "main/java/" vào 1 folder chung
				 App81_https_Server.class.getResource("/") = root = main/resources/ = main/java/
				 App81_https_Server.class.getResource("/abc") = main/resource/abc  = main/java/abc  
				 //
				 App81_https_Server.class.getResource("..") = root/pakage_name       => package_name của class này
				 App81_https_Server.class.getResource(".") = root/pakage_name/ 
				 App81_https_Server.class.getResource("abc") = root/pakage_name/abc
				 */
				
				URL filenameURL =  App5_Server_downloadFile.class.getResource(filename);   // = "root/pakage_name/filename"
				System.out.println("filenameURL = " + filenameURL.getPath());
				
				// vertx sử dụng file cache khi đọc static file
				// quá trình readfile là asynchronous
				if (filename != null) {
					req.response().sendFile(filenameURL.getPath());
				}
				
			}
		}).listen(8080);

	}
}
