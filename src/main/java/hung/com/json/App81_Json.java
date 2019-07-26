package hung.com.json;

import java.util.HashMap;
import java.util.Map;

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
public class App81_Json {

	public static void main(String[] args) throws Exception {
		
		//===========================================convert String => JsonObject ===================
		String jsonString = "{\"foo\":\"bar\"}";
		JsonObject jsonObject = new JsonObject(jsonString);
		System.out.println(jsonObject.toString());
		
		//===================================convert Map => JsonObject ===================
		Map<String, Object> map = new HashMap<>();
		map.put("foo", "bar");
		map.put("xyz", 3);
		JsonObject jsonObject1 = new JsonObject(map);
		System.out.println(jsonObject1.toString());
		
		//==================================== create JsonObject ===============================
		JsonObject jsonObject2 = new JsonObject();
		jsonObject2.put("foo", "bar")
		           .put("num", 123)
		           .put("mybool", true);
		System.out.println(jsonObject2.toString());
		
		//=================================== convert JsonObject => Java Object ================
		JsonObject jsonUser = new JsonObject();
		jsonUser.put("name", "Happy")
				.put("yearOld", 18);
		
		User user = jsonUser.mapTo(User.class);
		System.out.println("************* {" + user.getName() + "," + user.getYearOld() + "}" );
		
		//================================ convert java Object => JsonObject ================
		JsonObject  jsonObjectFromUser = JsonObject.mapFrom(user);
		System.out.println(jsonObjectFromUser.toString());
		
		//======================
		String jsonString1 = "[\"foo\",\"bar\"]";
		JsonArray jsonArray = new JsonArray(jsonString1);
		System.out.println(jsonArray.toString());
	}


}
