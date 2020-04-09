package hung.com.files;


import io.vertx.core.AsyncResult;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.file.FileSystem;
import io.vertx.core.http.HttpServer;
import io.vertx.core.net.NetServer;

/**
 * lúc compile sẽ gộp "main/resources/" và "main/java/" vào 1 folder chung
 App81_https_Server.class.getResource("/") = root = main/resources/ = main/java/
 App81_https_Server.class.getResource("/abc") = main/resource/abc  = main/java/abc  
 //
 App81_https_Server.class.getResource("..") = root/pakage_name       => package_name của class này
 App81_https_Server.class.getResource(".") = root/pakage_name/ 
 App81_https_Server.class.getResource("abc") = root/pakage_name/abc
 App81_https_Server.class.getResource("abc").getPath()
  //===========================
  + Run or Debug mode trên Eclipse lấy ./ = project folder 
  
  + run thực tế:  ./ = folder run "java -jar *.jar"
 //========= 
 File("loginTest.json"):   file ở ./ folder    (tùy run thực tế hay trên eclipse)
 File("./abc/test.json"):   
 File("/abc"): root folder on linux (not window)
 */
public class App22_read_file_sync {

	public static void main(String[] args) throws InterruptedException{

		Vertx vertx = Vertx.vertx();
		Buffer buffer = vertx.fileSystem().readFileBlocking(App22_read_file_sync.class.getResource("local.json").getPath());

		System.out.println(buffer.toString());

	}

}
