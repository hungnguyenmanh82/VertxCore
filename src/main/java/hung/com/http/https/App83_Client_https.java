package hung.com.http.https;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.http.HttpMethod;

/*
 * @author <a href="http://tfox.org">Tim Fox</a>
 */
public class App83_Client_https extends AbstractVerticle {

	public static void main(String[] args) {
		Vertx vertx = Vertx.vertx();
		vertx.deployVerticle(new App83_Client_https()); 	
	}
	@Override
	public void start() throws Exception {

		// Note! in real-life you wouldn't often set trust all to true as it could leave you open to man in the middle attacks.

		HttpClientOptions options = new HttpClientOptions()
				//			    .setProtocolVersion(HttpVersion.HTTP_2)
				.setSsl(true)
				.setUseAlpn(true)
				.setTrustAll(true);

		HttpClient client = vertx.createHttpClient(options);

		client.getNow(4443, "localhost", "/",  resp -> {
			System.out.println("Got response " + resp.statusCode());
			resp.bodyHandler(body -> System.out.println("Got data " + body.toString("ISO-8859-1")));
		});
	}



}
