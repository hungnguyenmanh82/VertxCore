package hung.com.http.client;

import java.util.Iterator;
import java.util.Map.Entry;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.MultiMap;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpClientResponse;

public class HttpClientVerticle extends AbstractVerticle {

    @SuppressWarnings("deprecation")
	@Override
    public void start(Future<Void> startFuture) throws Exception {
        io.vertx.core.http.HttpClient httpClient = vertx.createHttpClient(); 
        
//        httpClient.options(options);
        
        httpClient.getNow(81, "localhost", "/vertx/client.html", new Handler<HttpClientResponse>() {
        	//asynchronous callback function only 1 time when receive header of response
        	@Override
            public void handle(HttpClientResponse httpClientResponse) {
        		
        		//================== header response asynchronous ===========================
        		System.out.println("====================response===================");
        		System.out.println("status code = " + httpClientResponse.statusCode());
        		MultiMap header = httpClientResponse.headers();
        		
        		Iterator<Entry<String,String>> iterator = header.iterator();
        		
        		while(iterator.hasNext()) {
        			Entry<String,String> item = (Entry<String,String>) iterator.next();
        		}
        		
        		System.out.println("Header =" + httpClientResponse.toString());
        		
        		//================== body response asynchronous ===========================
                httpClientResponse.bodyHandler(new Handler<Buffer>() {
                    //asynchronous called many times
                	@Override
                    public void handle(Buffer buffer) {
                        System.out.println("Response (size=" + buffer.length() + "): ");
                        System.out.println(buffer.getString(0, buffer.length()));
                    }
                });
            }
        });
        
    }
}