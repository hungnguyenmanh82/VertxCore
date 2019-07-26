package hung.com.http.proxy;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.http.HttpClientRequest;
import io.vertx.core.http.HttpClientResponse;

/*
 * @author <a href="http://tfox.org">Tim Fox</a>
 */
public class App83_Client extends AbstractVerticle {

	public static void main(String[] args) {
		Vertx vertx = Vertx.vertx();
		vertx.deployVerticle(new App83_Client()); 	
	}

	@Override
	public void start() throws Exception {
		System.out.println("Client: connect Proxy port = 8080");
		// gui request H
		HttpClient httpClient= vertx.createHttpClient(new HttpClientOptions());
		

		@SuppressWarnings("deprecation")
		HttpClientRequest request = httpClient.get(8080, "localhost", "/", new Handler<HttpClientResponse>() {
					@Override
					public void handle(HttpClientResponse clientResponse) {
						// tại đây mới chỉ connect tơi server (chưa gửi gì hết)
						// đã nhận đầy đủ Response-header
						System.out.println("Client: clientResponse header statusCode = " + clientResponse.statusCode());
						
						clientResponse.bodyHandler(new Handler<Buffer>() {
							
							@Override
							public void handle(Buffer body) {
								System.out.println("Client:  clientResponse.bodyHandler() = " + body.toString());
								
							}
						});

					}
				}  );  

		//tại day chua gui request-header di
		// add to MultiMap (kind of hashMap)
		request.putHeader("headerField1", "1234");
		request.putHeader("headerField2", "core basic, nen tang");
		//request.headers();  //lệnh này để send header


		
		// send toan bo thong tin body đi
		// neu request.headers() chua dc goi no se gọi hàm này trc
		request.end();
	}
}
