package hung.com.basicSample;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;

public class VertxTestMain2 {

	public static void main(String[] args) {
		System.out.println("start main(): thread="+Thread.currentThread().getId());
		//create singleton
		Vertx vertx = Vertx.vertx();

		//register Verticale to capture event.
		//asynchronous call MyVerticle1.start() in worker thread
		vertx.deployVerticle(new MyVerticle1(), new Handler<AsyncResult<String>>(){
			@Override
			public void handle(AsyncResult<String> stringAsyncResult) {
				System.out.println("vertx.deployVerticle(): thread="+Thread.currentThread().getId());

				if (stringAsyncResult.succeeded()) {
					//Deployment id do Vertx cấp => cái này hơi stupid
					// vertx.undeploy(DeploymentId)
					//(sao ko dùng pointer luôn giống với android LocalBroadCastManager)
					System.out.println("Deployment id is: " + stringAsyncResult.result());
				} else {
					System.out.println("Deployment failed!");  //vì chưa đc cấp id
				}
			}
		});
	}

}