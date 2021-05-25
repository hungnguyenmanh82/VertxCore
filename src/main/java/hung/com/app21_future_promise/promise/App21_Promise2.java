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
import io.vertx.core.net.NetServer;

/**
 * 
Future<Type>:  extends Handler<type> và AsyncResult<type> => là kết hợp 2 class này để tạo funtion point (làm call back function khi có event).
 Khi 2 hàm future.complete(result) or future.fail(result) đc gọi thì lập tức nó sẽ gọi hàm callback của nó là Handler.handle(AsyncResult<result>). 
Future là function point => thread nào gọi nó thì nó chạy trên thread đó (đã test).

Từ vertx 3.8 trở đi Promise thay thế cho Future ở tất cả vị trí. Cách sử dụng như nhau => dễ nhìn hơn.

 */
public class App21_Promise2 {

	public static void main(String[] args) throws InterruptedException{

		System.out.println("main(): thread=" + Thread.currentThread().getId());

		Vertx vertx = Vertx.vertx();

		/**
		 * + ngay khi khởi tạo Future thì Handler đã đc gọi luôn rồi. Ko chờ .onSuccess() đc gọi
		 * + khác với Observable của rxjs. Handler chỉ đc gọi khi observable.subscribe() đc gọi
		 * + chính vì thế SQL hay WebClient đều phải có hàm .send() và .execute() đi kèm
		 */
		Future<String> futureTest = Future.<String>future((Promise<String> pr)->{
			System.out.println(" run handler ");
			pr.complete("ko co viec gi kho");
		});
		
		System.out.println("================== test xem Handler đã gọi chưa khi future.onSuccess() chưa đc gọi ");

		
		/**
		 * Handler này dù thiết lập sau khi Handler đc gọi vẫn có tác dụng như thường
		 */
		futureTest
		.onSuccess((String st)->{
			System.out.println("output = " + st);
		})
		.onFailure((Throwable thr)->{
			thr.printStackTrace();
		});


	}

}
