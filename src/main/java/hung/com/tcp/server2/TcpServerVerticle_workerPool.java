package hung.com.tcp.server2;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetServer;
import io.vertx.core.net.NetServerOptions;
import io.vertx.core.net.NetSocket;


public class TcpServerVerticle_workerPool extends AbstractVerticle {

	@Override
	public void start() throws Exception {
		super.start();  //phải gọi hàm này thì vertx.deploymentIDs() mới cập nhật giá trị.

		NetServerOptions netServerOptions = new NetServerOptions().setPort(10000)
				.setHost("localhost");
		//																.setIdleTimeout(3000);
		NetServer server = vertx.createNetServer(netServerOptions);

		server.connectHandler(new Handler<NetSocket>() {
			//Asynchronous Event only 1 time when Socket client connect to server
			@Override
			public void handle(NetSocket netSocket) {
				System.out.println("****incoming connect request!: thread="+Thread.currentThread().getId());

				DeploymentOptions options = new DeploymentOptions()
						.setWorkerPoolName("*TcpServletThreadPool")
						.setWorkerPoolSize(10)  //thread for server, not client
						.setWorker(false);  //standard Verticle  => vì yêu cầu Handler theo thứ tự

				vertx.deployVerticle(new TcpServletVerticle(netSocket),options); //change netSocket to other Context and Stardard Verticle Threadpool

				//==========================close socket ======================
				//              netSocket.close();

				//==================== Handler close Socket by any reason 	
				// Để 1 thread quản lý việc Open/Close của Socket sẽ tiện hơn
				// Nếu để Threadpoold quản lý sẽ dẫn tới pending thread khi add/delete Socket để quản ly
				netSocket.closeHandler(new Handler<Void>() {
					@Override
					public void handle(Void event) {
						System.out.println("socket is closed: ");
					}
				});
			}
		});

		server.listen( res -> {
			if (res.succeeded()) {
				System.out.println("tcp server is now listening on Port=10000");
			} else {
				System.out.println("cant not start tcp server on Port=10000");
			}
		});

		//=================================================================
		/*		server.close(new Handler<AsyncResult<Void>>() {
            @Override
            public void handle(AsyncResult<Void> result) {
                if(result.succeeded()){
                	 System.out.println("tcp server closed successfully");
                }

                System.out.println("tcp server was closed");
            }
        });*/
	}

	@Override
	public void stop() throws Exception {
		// TODO Auto-generated method stub
		super.stop();
	}


}