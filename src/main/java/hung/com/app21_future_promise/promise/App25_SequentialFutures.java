package hung.com.app21_future_promise.promise;


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
 * http://vertx.io/docs/vertx-core/java/#_concurrent_composition
 * 
Future<Type>:  extends Handler<type> và AsyncResult<type> => là kết hợp 2 class này để tạo funtion point (làm call back function khi có event).
 Khi 2 hàm future.complete(result) or future.fail(result) đc gọi thì lập tức nó sẽ gọi hàm callback của nó là Handler.handle(AsyncResult<result>). 
Future là function point => thread nào gọi nó thì nó chạy trên thread đó (đã test).

 */
public class App25_SequentialFutures {

	public static void main(String[] args) throws InterruptedException{

		Vertx vertx = Vertx.vertx();
		FileSystem fs = vertx.fileSystem();

		/**
		  //===========================
		  + Run or Debug mode trên Eclipse lấy ./ = project folder 	  
		  + run thực tế:  ./ = folder run "java -jar *.jar"
		 //========= 
		 File("loginTest.json"):   file ở ./ folder    (tùy run thực tế hay trên eclipse)
		 File("./abc/test.json"):   
		 File("/abc"): root folder on linux (not window)
		 */
		//khi create 1 future đồng thời sẽ tạo 1 promise tương ứng, và ngược lại
		Future<Void> future1 = Future.future(promise -> fs.createFile("./foo1.txt", promise));

		/**
		 * compose(): cũng lấy tên từ reactive programing của javascript => Builder Pattern
		 * future.compose(function<T,Future<T>>) thi function<T,Future<T>> chi đc gọi khi promise.complete() 
		 * future.setHandler(handler<AsyncResult<T>)  thì Handler<AsynResult<T>> đc gọi khi promise.complete()/fail() 
		 * compose() = setHandler() = addHandler() xem code FutureImpl class
		 * 
		 * compose(Funtion<T,Future<T>) return new Future.  Ko phải Future<T> do Function trả về. 
		 *                              Vì future cũ có thể chứa List<handler> rất phức tạp.  Future<T>.setHanlder(new Future).
		 * setHandler() return old Future.
		 * 
		 *  Function: lấy theo tên javascript là 1 method có giá trị trả về
		 *  Handler: là 1 method trả về void
		 */
		// compose() tạo new future
		Future<Void> startFuture = future1
				.compose(v -> {  //function này chỉ dc gọi khi Future.completed(). fail() sẽ ko gọi nó
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
