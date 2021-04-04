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
Future<T1> future1;
Future<T2> future2;
Future<T3> future3;
Future<T4> future4;

future1.<T2>compose( (T1 t1)->{  // <T2> là kiểu return 
			return future2<T2>;
		})
		.<T3>compose( (T2 t2)->{ // <T3> là kiểu return
			return future3<T3>;
		})
		.<T4> compose( (T3 t3)->{ // 
			return future4<T4>;
		})
		.<T5> map(t5) // Future<T5>
		.onsucess( (T5 t5)->{ //
			//future5 ok
		})
		onfailure( (throwable thr)->{
			// nếu 1 trong các quá trình trên fail
		})
		.eventually((Void v)->{
			//giống finally của try-catch

		}); 
		
.<T5>map(t5) là cách mà ta có thể truyền tham số từ compose ra ngoài rất hay	
 */

public class App25_SequentialFutures_compose_handler_cach2 {

	public static void main(String[] args) throws InterruptedException{
		/**
		 * Thư viện Vertx hiện tại đang dùng cách 3 là chủ yếu
		 */
		Future.<String>future(promise-> asyncFuntion1("success",promise))
		      .<Integer>compose(str ->{  // str là return của  asyncFuntion1() => lưu ý: Kiểu String của Future
		    	  int count;
		    	  if(str.equals("success")) {
		    		  count = 1;
		    	  }else {
		    		  count = 0;
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
	 * Promise<T> extend Handler<AsyncResult<T>>
	 */
	private static void asyncFuntion1(String str, Handler<AsyncResult<String>> handler) {
		if(str.equals("success")) {
			//do something asynchronous: Vertx Webclient, SQL async, Redis Async, readFile async
			/**
			 * Future extends AsyncResult<T>
			 */
			handler.handle(Future.<String>succeededFuture(str));
		}else {
			handler.handle(Future.failedFuture("asyncFuntion1() failed"));
		}
	}
	
	/**
	 * Thiết kế asyncFuntion mới dùng Promise<T>. Thiết kế cũ dùng Handler<AsyncResult<T>>
	 * Promise<T> extend Handler<AsyncResult<T>>
	 */
	private static void asyncFuntion2(int count, Handler<AsyncResult<Integer>> handler) {
		if(count == 1) {
			//do something asynchronous: Vertx Webclient, SQL async, Redis Async, readFile async
			/**
			 * Future extends AsyncResult<T>
			 */
			handler.handle(Future.<Integer>succeededFuture(count));
		}else {
			handler.handle(Future.failedFuture("asyncFuntion2() failed")); //AsyncResult<T>
		}
	}
	
	/**
	 * Thiết kế asyncFuntion mới dùng Promise<T>. Thiết kế cũ dùng Handler<AsyncResult<T>>
	 * Promise<T> extend Handler<AsyncResult<T>>
	 */
	private static void asyncFuntion3(boolean result, Handler<AsyncResult<JsonObject>> handler) {
		if(result == true) {
			//do something asynchronous: Vertx Webclient, SQL async, Redis Async, readFile async
			/**
			 * Future extends AsyncResult<T>
			 */
			handler.handle(Future.<JsonObject>succeededFuture(new JsonObject().put("key", "value")));
		}else {
			handler.handle(Future.failedFuture("asyncFuntion3() failed"));
		}
	}

}
