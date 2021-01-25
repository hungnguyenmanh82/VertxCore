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
public class App242_SequentialFutures_composeTypes_cach2 {

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
	 * Thiết kế asyncFuntion mới dùng Promise<T>. Thiết kế cũ dùng Handler<AsyncResult<T>>
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
