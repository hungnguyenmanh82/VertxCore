package hung.com.tcp.server2;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Handler;
import io.vertx.core.net.NetServer;
import io.vertx.core.net.NetServerOptions;
import io.vertx.core.net.NetSocket;


public class TcpServerVerticle2_newSockets extends AbstractVerticle {

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
				System.out.println("******** new socket connected : thread="+Thread.currentThread().getId()+ ", ThreadName="+Thread.currentThread().getName());

				DeploymentOptions options = new DeploymentOptions()
						.setWorkerPoolName("*TcpServletThreadPool")
						.setWorkerPoolSize(10)  //thread for server, not client
						.setWorker(false);  //true: worker-vertical dùng WorkerPoolName1  (các event vẫn tuần tự, nhưng trên thread khác nhau)
											//false: Standard-verticle dùng vert.x-eventloop-thread (fix thread to verticle)
											//blockingCode luôn dùng WorkerPoolName

				// Standard-verticle sẽ lấy Thread của Verticle (ko lấy của EventLoop thread)
				vertx.deployVerticle(new TcpServletVerticle2(netSocket),options); //change netSocket to other Context and Stardard Verticle Threadpool

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