package hung.com.basicSample;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;

/**
 * vertx instance sẽ asign Thread trong thread pool để chạy Vertical (khi ta Deploy Vertical).
 *  by default: threadpool = 1.
 */
public class App12_VertxHandler {

	public static void main(String[] args) {
		System.out.println("start main(): thread="+Thread.currentThread().getId());
		//create singleton
		Vertx vertx = Vertx.vertx();

		//register Verticale to capture event.
		//asynchronous call MyVerticle1.start() in worker thread
		vertx.deployVerticle(new MyVerticle1(), new Handler<AsyncResult<String>>(){
			//hàm này đc gọi sau khi hàm MyVerticle1.start() trả về giá trị (lưu ý Future.complete() nếu dùng asynchronous start() )
			@Override
			public void handle(AsyncResult<String> asyncResult) {
				System.out.println("vertx.deployVerticle(): thread="+Thread.currentThread().getId());

				if (asyncResult.succeeded()) {
					//Deployment id do Vertx cấp => cái này hơi stupid
					// vertx.undeploy(DeploymentId)
					//(sao ko dùng pointer luôn giống với android LocalBroadCastManager)
					System.out.println("Deployment id is: " + asyncResult.result());
				} else {
					System.out.println("Deployment failed!");  //vì chưa đc cấp id
				}
			}
		});
		
		// app ko stop với Main() stop vì có 1 worker thread quản lý Vertx có loop bắt Event
		//vertx.close();
	}

}
