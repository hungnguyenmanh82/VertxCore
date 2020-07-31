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
public class App24_SequentialFutures_composeTypes {

	public static void main(String[] args) throws InterruptedException{
		
		/**
		 * cách này phải khởi tạo khá nhiều Future => JVM có cơ chế tạo Pool để cấp phát và thu hồi Object => performance và RAM ko bị ảnh hưởng.
		 * Hi sinh một chút performance và RAM để source code sáng sủa => ok
		 * Cách này làm giảm số số code tree lồng nhau
		 */
		Future.<String>future(promise-> asyncFuntion1("success",promise))
		      .<Integer>compose(str ->{  // str là return của  asyncFuntion1() => lưu ý: Kiểu String của Future
		    	  int count;
		    	  if(str.equals("success")) {
		    		  count = 1;
		    		  
		    	  }else {
		    		  count = 0;
		    		  //
		    		  return Future.failedFuture("fail here");
		    	  }
		    	  
		    	  // Kiểu của compose và return future giống nhau = <integer>
		    	  return Future.<Integer>future(promise-> asyncFuntion2(count, promise)); 
		      })
		      .<JsonObject>compose(count ->{   // count là return của  asyncFuntion2() => lưu ý: kiểu <Integer> của Compose
		    	  boolean result;
		    	  if(count == 1) {
		    		  result = true;
		    	  }else {
		    		  result = false;
		    		  
		    		  return Future.failedFuture("fail here");
		    	  }
		    	  
		    	// Kiểu của compose và return future giống nhau = <JsonObject>
		    	  return Future.<JsonObject>future(promise-> asyncFuntion3(result, promise));
		    	  
		      })
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
	
	private static void asyncFuntion2(int count, Promise<Integer> promise) {
		if(count == 1) {
			//do something asynchronous: Vertx Webclient, SQL async, Redis Async, readFile async
			promise.complete(1);
		}else {
			promise.fail("asyncFuntion2() failed");
		}
	}
	
	private static void asyncFuntion3(boolean result, Promise<JsonObject> promise) {
		if(result == true) {
			//do something asynchronous: Vertx Webclient, SQL async, Redis Async, readFile async
			promise.complete(new JsonObject().put("key", "value"));
		}else {
			promise.fail("asyncFuntion3() failed");
		}
	}

}
