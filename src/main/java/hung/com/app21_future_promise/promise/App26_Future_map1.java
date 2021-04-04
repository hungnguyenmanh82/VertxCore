package hung.com.app21_future_promise.promise;


import io.vertx.core.AsyncResult;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.file.FileSystem;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.JsonObject;
import io.vertx.core.net.NetServer;

/**
 * http://vertx.io/docs/vertx-core/java/#_concurrent_composition
 * 
Future<Type>:  extends Handler<type> và AsyncResult<type> => là kết hợp 2 class này để tạo funtion point (làm call back function khi có event).
 Khi 2 hàm future.complete(result) or future.fail(result) đc gọi thì lập tức nó sẽ gọi hàm callback của nó là Handler.handle(AsyncResult<result>). 
Future là function point => thread nào gọi nó thì nó chạy trên thread đó (đã test).

 */


/**
 future
      .<V>map(V value)   // return Future<V> to replace old future when Old Future success (not failed)
      .onSuccess( value->  System.out.println(value) )
     .onFailure( thr -> thr.printStacktrace());

future<T>.<V>map( (T t)->{ return V;} ) 

 future.<T>flatMap()  =  future.<T>compose()    // xem code sẽ rõ
 
 Khái niệm "map" ko đổi so với ngôn ngữ javaScript
 */

/**
 * map() vs flatmap()
 *  https://stackoverflow.com/questions/26684562/whats-the-difference-between-map-and-flatmap-methods-in-java-8
 */
public class App26_Future_map1 {

	public static void main(String[] args) throws InterruptedException{
		
		
		/**
		 * cách này phải khởi tạo khá nhiều Future => JVM có cơ chế tạo Pool để cấp phát và thu hồi Object => performance và RAM ko bị ảnh hưởng.
		 * Hi sinh một chút performance và RAM để source code sáng sủa => ok
		 * Cách này làm giảm số số code tree lồng nhau
		 */
		Future.<String>future(promise-> asyncFuntion1("success",promise))
		      .<Boolean>map((String s)->{
		    	  
		    	// map có trách nhiệm convert kiểu dữ liệu: 1-1    
		  		if(s.equals("success")) {
					//do something asynchronous: Vertx Webclient, SQL async, Redis Async, readFile async
					return true; // <Boolean>
				}else {
					return false; // <Boolean>
				}
		      })
//		      .eventually()  // dùng cho Vertx 4x 
		      .onSuccess(json ->{  // json là return của  asyncFuntion3() => lưu ý kiểu <JsonObject> của compose
		    	  System.out.println(json.toString());
		       })
		      .onFailure(throwable-> {
					System.out.println("Error:"+ throwable.getMessage());
					throwable.printStackTrace();
				});
		
		
	}
	
	/**
	 * Thiết kế asyncFuntion mới dùng Promise<T>. Thiết kế cũ dùng Handler<AsyncResult<T>>
	 */
	 private static void asyncFuntion1(String str, Promise<String> promise) {
		if(str.equals("success")) {
			//do something asynchronous: Vertx Webclient, SQL async, Redis Async, readFile async
			promise.complete(str);
		}else {
			promise.fail("asyncFuntion1() failed");
		}
	}
	

}
