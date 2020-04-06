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
public class App22_read_file {

	public static void main(String[] args) throws InterruptedException{

		Vertx vertx = Vertx.vertx();
		FileSystem fs = vertx.fileSystem();

		/**
		 * Run or Debug mode trên Eclipse lấy ./ = project folder 
		 */
		//khi create 1 future đồng thời sẽ tạo 1 promise tương ứng và ngược lại cùng kiểu AsyncResult<T>
		// promise extends handler
		Future<Buffer> future1 = Future.<Buffer>future(promise -> fs.readFile("./foo1.txt", promise));

		/**
		 * compose(): cũng lấy tên từ reactive programing của javascript => Builder Pattern
		 * future.compose(function) đc gọi khi promise.complete()/fail() đc gọi => giống future.setHandler(handler)
		 * compose() đc gọi trc Handler() => giống như interceptor 
		 * khác biệt duy nhất là future.compose(Function<T,Future<U>>) return 1 Future
		 * Function: lấy theo tên javascript là 1 method có giá trị trả về
		 *  Handler: là 1 method trả về void
		 */
		Future<Void> startFuture = future1
				.compose(v -> {
					System.out.println("compose 1st: future1 finish");	  
					// When the file is created (fut1), execute this:
					return Future.<Void>future(promise -> fs.writeFile("./foo1.txt", Buffer.buffer("ko co viec gi kho"), promise));
				})
				.compose(v -> {
					System.out.println("compose 2rd: future 2 finish");
					// When the file is written (fut2), execute this:
					return Future.future(promise -> fs.move("./foo1.txt", "./bar.txt", promise));
				});

		future1.setHandler(ar->{
			if(ar.succeeded()){
				System.out.println("end future1: succeeded");	
			}else{ //ar.failed()
				System.out.println("end future1: failed");	
			}
			
		});
		
		startFuture.setHandler(ar->{
			if(ar.succeeded()){
				System.out.println("end of all futures: succeeded");	
			}else{ //ar.failed()
				System.out.println("end of all futures: failed");	
			}
		});
	}

}
