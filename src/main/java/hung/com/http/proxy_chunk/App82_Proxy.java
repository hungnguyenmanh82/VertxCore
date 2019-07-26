package hung.com.http.proxy_chunk;

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
 */
public class App82_Proxy extends AbstractVerticle {

	public static void main(String[] args) {
		Vertx vertx = Vertx.vertx();
		vertx.deployVerticle(new App82_Proxy()); 	
	}

	@Override
	public void start() throws Exception {
		
		/**
		 *  Lay Proxy lam he qui chieu:
		 *   (1) serverRequest => (2) clientRequest => (3) clientReponse => (4) serverResponse
		 *   
		 *   (1)(4): là tương tác giữa client và Proxy  =>  Proxy = Server    (đóng vai http Server)
		 *   (2)(3): là tuong tac giữa Proxy và Server =>   Proxy = Client    (đóng vai http client)
		 */

		// HttpClient tu Proxy connect toi Server de forward thong tin tu Client toi
		HttpClient client = vertx.createHttpClient(new HttpClientOptions());

		// tại đây đã nhận đc client request-header  (chưa nhận đc request-body)
		vertx.createHttpServer().requestHandler(new Handler<HttpServerRequest>() {
			@Override
			public void handle(HttpServerRequest serverRequest) {
				System.out.println("Proxying request: " + serverRequest.uri());

				//================================== proxy tạo 1 connect tới server ============================
				//forward toan bo client-request toi server
				@SuppressWarnings("deprecation")
				HttpClientRequest clientRequest = client.request(serverRequest.method(), 8282, "localhost", serverRequest.uri(), new Handler<HttpClientResponse>() {
					@Override
					public void handle(HttpClientResponse clientResponse) {
						// Proxy nhan server-response va chuyen lai cho client (proxy-response)
						System.out.println("Proxying response: " + clientResponse.statusCode());
						
						// ===================================== response forward ===================================================
						serverRequest.response().setChunked(true);
						serverRequest.response().setStatusCode(clientResponse.statusCode());
						
						serverRequest.response().headers().setAll(clientResponse.headers());
						
						clientResponse.handler(data -> {
							System.out.println("Proxying response body: " + data.toString("ISO-8859-1"));
							serverRequest.response().write(data);
						});
						
						clientResponse.endHandler((v) -> serverRequest.response().end());

					}
				});

				// ===================================== request forward ===================================================
				clientRequest.setChunked(true);
				// chuyen toan bo header cua Client-request sang sang cho Server
				clientRequest.headers().setAll(serverRequest.headers());
				
				
				// chuyen toan bo body cua Client-request sang server
				serverRequest.handler(new Handler<Buffer>() {
					@Override
					public void handle(Buffer buffer) {
						System.out.println("Proxying request body " + buffer.toString("ISO-8859-1"));
						clientRequest.write(buffer);
						
					}
				});
				
				// end body cua client-request
				serverRequest.endHandler(new Handler<Void>() {
					
					@Override
					public void handle(Void event) {
						System.out.println("end request ");
						clientRequest.end();
						
					}
				});

			}
		}).listen(8080);  //proxy server listen port 8080


	}
}
