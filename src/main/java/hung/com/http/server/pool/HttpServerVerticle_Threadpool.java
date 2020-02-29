package hung.com.http.server.pool;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpConnection;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;

/**
 * 
http://tutorials.jenkov.com/vert.x/http-server.html

Vertical đăng ký nhận HttpServer event với Vertx.
Vertx thay mặt Vertical làm mọi thứ, bắt event.
Khi có event, Vertx sẽ trả về và gọi Vertical xử lý
Tất nhiên, Vertx phải cấp phát thread cho Verticle 
 */
public class HttpServerVerticle_Threadpool extends AbstractVerticle{
	private HttpServer httpServer = null;

	private Buffer totalBuffer = Buffer.buffer(4000); //4k byte
	//run on a worker thread
	@Override
	public void start(Future<Void> startFuture) throws Exception {
		System.out.println("MyVerticle started! port=81: thread="+Thread.currentThread().getId());

		HttpServerOptions httpServerOptions = new HttpServerOptions()
				.setMaxHeaderSize(4000)
				.setReceiveBufferSize(8000)
				.setSendBufferSize(8000);

		httpServer = vertx.createHttpServer(httpServerOptions);


		/**
		 * url = http://localhost:81/atm?id=1&command=ejm
		 * uri = /atm?id=1&command=ejm
		 * path = /atm
		 * id = 1
		 * command = ejm
		 */


		httpServer.connectionHandler(new Handler<HttpConnection>() {		
			@Override
			public void handle(HttpConnection connect) {
				System.out.println("************ http connectionHandler: thread="+Thread.currentThread().getId());
				//reject connect neu Server overload or number of connect > Max connect

				// Deploy Verticle de xu ly http request/response tren Threadpool khac nhu the se toi uu hon
				// 1 http request/reponse context nen xu ly tren 1 thread (threadpool worker= false) de dam bao Order
				//truong hop context lay du lieu tu 2 service khac nhau thi phai dam bao tinh thu tu


			}
		});

		// quá trình parser thực hiện trên EventLoopPool của Vertx, chứ ko phải trên thread của context này.
		// thread của context này chỉ để trả về event thôi.
		httpServer.requestHandler(new Handler<HttpServerRequest>() {
			@Override
			public void handle(HttpServerRequest request) {
				System.out.println(" http requestHandler, *** deploy request Vertical: thread="+Thread.currentThread().getId());
				//================================ Move http HandlerRequest to other Thread context =========================== 
				DeploymentOptions options = new DeploymentOptions()
						.setWorkerPoolName("ThreadPoolForRequestHandler")   //name là duy nhất để share giữa các Verticle
						.setWorkerPoolSize(3)  //thread for server, not client
						.setHa(true)         //HA: high Availability
						.setWorker(true);    //true: worker-vertical dùng ThreadPoolForRequestHandler  (các event vẫn tuần tự, nhưng trên thread khác nhau)
											//false: Standard-verticle dùng vert.x-eventloop-thread (fix thread to verticle)
											//blockingCode luôn dùng ThreadPoolForRequestHandler

				vertx.deployVerticle(new HttpRequestHandlerVerticle(request),options);
			}
		});


		httpServer.listen(81, new Handler<AsyncResult<HttpServer> >() {			
			@Override
			public void handle(AsyncResult<HttpServer> result) {
				if (result.succeeded()) {
					// thông báo khởi tạo thành công, cho phia vertx.deployVerticle(verticle, handler<AsyncResult<void>>)
					// <xem Future concept>
					startFuture.complete();  
				} else {
					startFuture.fail(result.cause());
				}

			}
		});//port = 81
	}

	// run on a worker thread
	@Override
	public void stop(Future<Void> stopFuture) throws Exception {
		//function này cần đc gọi để xác nhận undeploy() thành công (sẽ xóa DeploymentId)
		// hoặc phải gọi hàm stopFuture.complete()
		super.stop(stopFuture);
//		stopFuture.complete();
		System.out.println("Verticle_startFuture.stop(): thread=" + Thread.currentThread().getId());
	}
}
