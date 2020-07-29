package hung.com.json;

import java.util.Base64;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import hung.com.files.App22_read_file_sync;
import hung.com.json.model.GoogleOauth2;
import hung.com.json.model.User;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.http.HttpClientRequest;
import io.vertx.core.http.HttpClientResponse;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

/*
 vertx JsonObject lib rất cơ động, hay hơn Gson và Jackson nhiều
 Có thể dùng JsonObject để convert JDBC Resultset về java Object dễ dàng

 https://vertx.io/docs/vertx-core/java/#_json
 */
public class App82_Json_types {

	public static void main(String[] args) throws Exception {

		JsonArray();

	}
	
	public static void JsonArray(){
	
		//====================== Json Array ==============================
		JsonArray jsonArray2 = new JsonArray();
		
		// JsonArray cho phép các phần tử ở các kiểu khác nhau
		jsonArray2.add("abc")
		         .add(123)
		         .add(false);
		System.out.println(jsonArray2.toString());


		jsonArray2.forEach(o->{
			
			if(o instanceof JsonObject) {
				//đoạn này ko nhảy vào
				System.out.println("JsonObject");
			}
			
			/**
			 * Jump to functions:
			 * JsonObject.getString() => String
			 *           .getInt() => Integer
			 *           .getLong() => Long
			 *           .getBoolean() => Boolean
			 */
			if(o instanceof String) {
				System.out.println(o);
			}else if(o instanceof Integer) {
				System.out.println(o);
			}else if( o instanceof Boolean) {
				System.out.println(o);
			}
			
		});
		

	}
	



	



}
