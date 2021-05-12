package hung.com.http.proxy;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.http.HttpClientRequest;
import io.vertx.core.http.HttpClientResponse;
import io.vertx.core.http.HttpServerRequest;

/*
Proxy don't process Body request. Body should be socket streaming forward

Vidu nay cho truong hop ko phai là chunked Request
 */
public class App82_Proxy2 extends AbstractVerticle {

	public static void main(String[] args) {
		Vertx vertx = Vertx.vertx();
		vertx.deployVerticle(new App82_Proxy2()); 	
		
		
	}

	@Override
	public void start() throws Exception {
		
		System.out.println("Proxy: http Proxy start at port = 8080");
		System.out.println("*** step1: please test Browsers with url:" + " http://localhost:8080/");
		System.out.println("*** step2: please test Browsers with url:" + " http://localhost:8080/test");
		
		/**
		 *  Lay Proxy lam he qui chieu:
		 *   (1) serverRequest => (2) clientRequest => (3) clientReponse => (4) serverResponse
		 *   
		 *   (1)(4): là tương tác giữa client và Proxy  =>  Proxy = Server    (đóng vai http Server)
		 *   (2)(3): là tuong tac giữa Proxy và Server =>   Proxy = Client    (đóng vai http client)
		 *   
		 *   (1) và (3) giống nhau, bản chất nhận bản tin = header + body  => vì thế cần headerHandler và bodyHandler
		 */

		// HttpClient tu Proxy connect toi Server de forward thong tin tu Client toi
		HttpClient client = vertx.createHttpClient(new HttpClientOptions());

		//STEP 1:  ServerRequest: tại đây đã nhận đc full client request-header  (chưa nhận đc request-body)
		vertx.createHttpServer().requestHandler((HttpServerRequest serverRequest) -> {
					System.out.println("******Proxy: serverRequest Handler: uri = " + serverRequest.uri());
					System.out.println("Proxy: connect Server port = 8282 ");
					
					//================================== proxy tạo 1 connect tới server ============================
					//forward toan bo client-request toi server
					@SuppressWarnings("deprecation")
					HttpClientRequest clientRequest = client.request(serverRequest.method(), 8282, "localhost", serverRequest.uri(), (HttpClientResponse clientResponse)-> {
							//STEP 3.1: (3) Client-Response: Tại day da nhan dc full http-response header roi (nhưng chưa nhận đc http-response body)
							// request/response 2 chieu độc lập (nghĩa là tại đây có thể gửi response mà ko quan tâm tới body-request)
							System.out.println("Proxy: clientResponse connect Handler " + clientResponse.statusCode());
							
							// ===================================== response forward ===================================================
							//STEP 3.2: (3)chi write vào buffer của Response (chưa send đi)
							serverRequest.response().setStatusCode(clientResponse.statusCode());
							// STEP 4.1: (4) server response header
							serverRequest.response().headers().setAll(clientResponse.headers());
							
							
							clientResponse.bodyHandler(new Handler<Buffer>() {
								@Override
								public void handle(Buffer body) {
									// STEP 3.3: (3) Client-Response: tại đây nhận đc toàn bộ body của http-response
									System.out.println("Proxy: clientResponse.bodyHandler(): ");
									//STEP 4.2: (4) serverResponse: gửi cả response-header và response-body cho Client
									serverRequest.response().end(body);
									
								}
							});
							
							// neu goi endHandler thi se ko goi BodyHandler (đã test)
							// chi dung ham nay voi Chunk request
	/*						clientResponse.endHandler(new Handler<Void>() {
								
								@Override
								public void handle(Void event) {
									System.out.println("Proxy: clientResponse.endHandler() ");
									// send toan bo thong tin body toi server
									// neu request.headers() chua dc goi no se gọi hàm này trc
									serverRequest.response().end();
									
								}
							});*/


					});

					// ===================================== request forward ===================================================
					// STEP 2.1: (2) ClientRequest: gửi header  
					// chunk cung thuoc header
					// chuyen toan bo header cua Client-request sang sang cho Server
					// cho nay chi ghi vào MultiMap chua gui đi
					clientRequest.headers().setAll(serverRequest.headers());
					
					
					// GET/HEAD ko có body nen hàm này ko dc gọi
					// tại đây đã nhận đủ body của ClientRequest
					serverRequest.bodyHandler(new Handler<Buffer>() {
						
						@Override
						public void handle(Buffer body) {
							System.out.println("Proxy: ServerRequest.bodyHandler() ");
							// STEP 2.2: (2) ClientRequest gửi body
							clientRequest.end(body);
							
						}
					});
					
					// neu goi endHandler thi sẽ ko goi BodyHandler  (đã test)
					// chi dung ham nay voi Chunk request
	/*				serverRequest.endHandler(new Handler<Void>() {
						
						@Override
						public void handle(Void event) {
							System.out.println("Proxy: ServerRequest.endHandler() ");
							// send toan bo thong tin body toi server
							// neu request.headers() chua dc goi no se gọi hàm này trc
							clientRequest.end();
							
						}
					});*/

				}
				).listen(8080);  //proxy server listen port 8080


	}
}
