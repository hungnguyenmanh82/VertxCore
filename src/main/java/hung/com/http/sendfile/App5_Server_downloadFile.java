package hung.com.http.sendfile;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;

/*
 * @author <a href="http://tfox.org">Tim Fox</a>
 */
public class App5_Server_downloadFile extends AbstractVerticle {

	public static void main(String[] args) {
		Vertx vertx = Vertx.vertx();
		vertx.deployVerticle(new App5_Server_downloadFile()); 	
	}

	@Override
	public void start() throws Exception {

		System.out.println("try with: http://localhost:8080/");

		// ở chế độ debug file Debug sẽ được run ở cùng cấp với "src" => tức trong Project folder.
		vertx.createHttpServer().requestHandler(req -> {
			String filename = null;
			if (req.path().equals("/")) {
				filename = "./src/main/resources/webroot/index.html";
			} else if (req.path().equals("/page1.html")) {
				filename = "./src/main/resources/webroot/page1.html";
			} else if (req.path().equals("/page2.html")) {
				filename = "./src/main/resources/webroot/page2.html";
			} else {
				req.response().setStatusCode(404).end();
			}
			if (filename != null) {
				req.response().sendFile(filename);
			}
		}).listen(8080);
	}
}
