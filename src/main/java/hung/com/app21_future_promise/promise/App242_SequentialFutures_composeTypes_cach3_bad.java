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

public class App242_SequentialFutures_composeTypes_cach3_bad {

	public static void main(String[] args) throws InterruptedException{

		/**
		 * cách này ko tường minh, ko nên dùng => khó nhìn Kiểu trả về hơn. Nên dùng cách 1
		 */
		asyncFuntion1("success")
		.<Integer>compose(str ->{  // str là return của  asyncFuntion1() => lưu ý: Kiểu String của Future
			int count;
			if(str.equals("success")) {
				count = 1;
			}else {
				count = 0;
			}

			// Kiểu của compose và return future giống nhau = <integer>
			return asyncFuntion2(count);
		})
		.<JsonObject>compose(count ->{   // count là return của  asyncFuntion2() => lưu ý: kiểu <Integer> của Compose
			boolean result;
			if(count == 1) {
				result = true;
			}else {
				result = false;
			}

			// Kiểu của compose và return future giống nhau = <JsonObject>
			return asyncFuntion3(result);
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
	 * cách này ko tường minh, ko nên dùng => khó nhìn Kiểu trả về hơn. Nên dùng cách 1
	 */
	private static Future<String> asyncFuntion1(String str) {
		Promise<String> promise = Promise.<String>promise();
		if(str.equals("success")) {
			//do something asynchronous: Vertx Webclient, SQL async, Redis Async, readFile async
			promise.complete(str);
		}else {
			promise.fail("asyncFuntion1() failed");
		}

		return promise.future();
	}

	private static Future<Integer> asyncFuntion2(int count) {
		Promise<Integer> promise = Promise.<Integer>promise();
		if(count == 1) {
			//do something asynchronous: Vertx Webclient, SQL async, Redis Async, readFile async
			promise.complete(1);
		}else {
			promise.fail("asyncFuntion2() failed");
		}

		return promise.future();
	}

	private static Future<JsonObject> asyncFuntion3(boolean result) {
		Promise<JsonObject> promise = Promise.<JsonObject>promise();
		if(result == true) {
			//do something asynchronous: Vertx Webclient, SQL async, Redis Async, readFile async
			promise.complete(new JsonObject().put("key", "value"));
		}else {
			promise.fail("asyncFuntion3() failed");
		}
		return promise.future();
	}

}
