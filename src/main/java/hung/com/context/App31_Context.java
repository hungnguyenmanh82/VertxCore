package hung.com.context;

import io.vertx.core.Context;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;

public class App31_Context {

	public static void main(String[] args) throws InterruptedException{
		System.out.println("main(): thread="+Thread.currentThread().getId());
		//create a new instance Vertx => a worker thread
		Vertx vertx = Vertx.vertx();
		
		Context context = vertx.getOrCreateContext();
		if (context.isEventLoopContext()) {
			System.out.println("main: Context attached to Event Loop: "+ context.deploymentID());
		} else if (context.isWorkerContext()) {
			System.out.println("main: Context attached to Worker Thread: "+ context.deploymentID());
		} else if (context.isMultiThreadedWorkerContext()) {
			System.out.println("main: Context attached to Worker Thread - multi threaded worker: "+ context.deploymentID());
		} else if (! Context.isOnVertxThread()) {
			System.out.println("main: Context not attached to a thread managed by vert.x: "+ context.deploymentID());
		}

		//register Verticale with Vertex instance to capture event.
		vertx.deployVerticle(new ContextVerticle(),new DeploymentOptions().setWorker(false)); //asynchronous call MyVerticle1.start() in worker thread
		vertx.deployVerticle(new ContextVerticle2()); 
		Thread.currentThread().sleep(5000);
		
		//vertx.undeploy(DeploymentId) => DeploymentId đc cấp khi hàm Verticle.start() đc gọi
		//asynchronous function
		vertx.close();  //error: Vertical.stop() won't be called
		Thread.currentThread().sleep(3000); //wait for vertx.close() finished
	}

}
