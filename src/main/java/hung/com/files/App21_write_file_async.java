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
 App81_https_Server.class.getResource(".") = root/pakage_name/     => package_name của class này
 App81_https_Server.class.getResource("abc") = root/pakage_name/abc
 App81_https_Server.class.getResource("abc").getPath()
 //
   App81_https_Server.class.getResource("..") = parent folder of root/pakage_name/
   App81_https_Server.class.getResource("../..") = parent of parent of root/pakage_name/  
  //===========================
  + Run or Debug mode trên Eclipse lấy ./ = project folder 
  
  + run thực tế:  ./ = folder run "java -jar *.jar"
 //========= 
 File("loginTest.json"):   file ở ./ folder    (tùy run thực tế hay trên eclipse)
 File("./abc/test.json"):   
 File("/abc"): root folder on linux (not window)
 */
public class App21_write_file_async {

	public static void main(String[] args) throws InterruptedException{

		Vertx vertx = Vertx.vertx();
		
		// dùng Event-Loop thread để đọc file
		FileSystem fs = vertx.fileSystem();

		/**
		 * Run or Debug mode trên Eclipse lấy ./ = project folder 
		 */
		//khi create 1 future đồng thời sẽ tạo 1 promise tương ứng
		// promise extends handler
		Future<Void> future1 = Future.future(promise ->	fs.createFile("./foo1.txt", promise));

		/**
		 * compose(): cũng lấy tên từ reactive programing của javascript => Builder Pattern
		 * future.compose(function) thi function đc gọi khi promise.complete()/fail() đc gọi => giống future.setHandler(handler)
		 * compose() đc gọi trc Handler() => giống như interceptor 
		 * khác biệt duy nhất là future.compose(Function<T,Future<U>>) return 1 Future
		 * Function<T>: lấy theo tên javascript là 1 method có return. 
		 *  Handler<AsyncResult<T>>: là 1 method return void
		 */
		Future<Void> startFuture = future1
				.compose(v -> {   //chi dc goi khi sucess
					System.out.println("compose 1st: future1 finish");	  
					// When the file is created (fut1), execute this:
					return Future.<Void>future(promise -> fs.writeFile("./foo1.txt", Buffer.buffer("ko co viec gi kho"), promise));
				})
				.compose(v -> {
					System.out.println("compose 2rd: future 2 finish");
					// When the file is written (fut2), execute this:
					return Future.future(promise -> fs.move("./foo1.txt", "./bar.txt", promise));
				});

		future1.onComplete(ar->{
			if(ar.succeeded()){
				System.out.println("end future1: succeeded");	
			}else{ //ar.failed()
				System.out.println("end future1: failed");	
			}
			
		});
		
		startFuture.onComplete(ar->{
			if(ar.succeeded()){
				System.out.println("end of all futures: succeeded");	
			}else{ //ar.failed()
				System.out.println("end of all futures: failed");	
			}
		});
	}

}
