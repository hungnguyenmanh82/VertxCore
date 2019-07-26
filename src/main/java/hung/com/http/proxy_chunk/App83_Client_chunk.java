package hung.com.http.proxy_chunk;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.http.HttpClientRequest;
import io.vertx.core.http.HttpClientResponse;

/*
 * @author <a href="http://tfox.org">Tim Fox</a>
 */
public class App83_Client_chunk extends AbstractVerticle {

	public static void main(String[] args) {
		Vertx vertx = Vertx.vertx();
		vertx.deployVerticle(new App83_Client_chunk()); 	
	}

	@Override
	public void start() throws Exception {

		// gui request H
		HttpClient httpClient= vertx.createHttpClient(new HttpClientOptions());
		
		// tại đây connect và gửi PUT request-header tơi server  (chưa gửi request-body tới server)
		// Handler để bắt event http Response
		HttpClientRequest request = httpClient.put(8080, "localhost", "/", new Handler<HttpClientResponse>() {
					//asynchronous callback function only 1 time when receive response-header
					@Override
					public void handle(HttpClientResponse resp) {
						System.out.println("Got response " + resp.statusCode());
						resp.bodyHandler(body -> System.out.println("Got data " + body.toString("ISO-8859-1")));

					}
				}  );  

		// ghi vào request-header và gửi header đi. cấu trúc body sẽ gửi bởi chunk
		request.setChunked(true);
		
		//request.headers();  //lệnh này để send header

		
		for (int i = 0; i < 10; i++) {
			// mỗi lần gửi là 1 chunk = chunk-header + chunk-body
			// chunk-header chứa chunk-lenght
			// ghi vào WriteStream tới socket
			request.write("client-chunk-" + i);
		}
		
		// chunk-length = 0 sẽ kết thúc response body
		request.end();
	}
}
