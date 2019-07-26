package hung.com.app1_basicSample;

import java.util.Set;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;

/**
 * vertx instance sẽ asign Thread trong thread pool để chạy Vertical (khi ta Deploy Vertical).
 *  by default: threadpool = 1.
 */
public class App12_DeployVerticle_startFuture {

	public static void main(String[] args) throws InterruptedException {
		System.out.println("start main(): thread="+Thread.currentThread().getId());

		//create a new instance Vertx => a worker thread sinh ra để quản lý loop Event, vì thế hàm main() kết thúc nhưng App ko stop
		// gọi vertx.close() để stop thread này
		//Ctrl+ T: để tìm implement Class sẽ thấy Vertx.vertx() sẽ new instance (ko phải Singleton)
		Vertx vertx = Vertx.vertx();

		//tạo 1 Verticle context và chạy trên Thread đc chỉ định bởi Vertx
		vertx.deployVerticle(new Verticle_startFuture(), new Handler<AsyncResult<String>>(){
			//hàm này đc gọi sau khi startFuture.complete() or startFuture.fail() đc gọi
			// nếu 2 hàm trên ko đc gọi thì hàm này sẽ ko đc gọi
			@Override
			public void handle(AsyncResult<String> asyncResult) {
				// Verticle bản chất là Context quản lý tài nguyên. 
				// Khi nào context đc giải phóng thì hàm Verticle.stop() đc gọi
				System.out.println("Future callback handler: thread="+Thread.currentThread().getId());

				if (asyncResult.succeeded()) { // khi startFuture.complete() đc gọi
					System.out.println("asyncResult = DeployId =" + asyncResult.result());

				} else { //khi startFuture.fail() đc gọi
					System.out.println("Deployment failed!");  //vì chưa đc cấp id
				}
			}
		});
		
		// waiting for Verticle context is allocate by Vertx
		Thread.currentThread().sleep(500);
		
		Set<String> deploymentIDs = vertx.deploymentIDs();
		System.out.println("==============  (sleeped 500ms wait for Context allocated), list of deploymentIDs: number Deployments =" + deploymentIDs.size());
		for(String depId: deploymentIDs){
			//
			System.out.println(depId);
		}

		// app ko stop với Main() stop vì có 1 worker thread quản lý Vertx có loop bắt Event
		//vertx.close();
		
		
	}

}
