package hung.com.json;

import java.util.HashMap;
import java.util.Map;

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
public class App81_Json {

	public static void main(String[] args) throws Exception {

		exampleAll();
		
//		JsonToJava();

	}
	
	public static void exampleAll(){
		//===========================================convert String => JsonObject ===================
		// cách 1:
		String jsonString = "{\"foo\":\"bar\"}";
		JsonObject jsonObject = new JsonObject(jsonString);
		System.out.println(jsonObject.toString());
		System.out.println(jsonObject.getString("foo"));
		//cách 2:
		JsonObject jsonObject0 = new JsonObject();
		
		jsonObject0.put("name", "hungbeo")
		           .put("phone", 1234)
		           .put("age", 12);
		
		System.out.println(jsonObject0.toString());
		
		//====================== Json Array ==============================
		//cách 1:
		String jsonString1 = "[\"foo\",\"bar\"]";
		JsonArray jsonArray = new JsonArray(jsonString1);
		System.out.println(jsonArray.toString());
		//cách 2:
		JsonArray jsonArray2 = new JsonArray();
		
		jsonArray2.add("abc").add(123).add(false);
		System.out.println(jsonArray2.toString());

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



	}

	public static void JsonToJava(){

		Vertx vertx = Vertx.vertx();
		Buffer buffer = vertx.fileSystem().readFileBlocking(App81_Json.class.getResource("googleAuth2.json").getPath());

		//===========================================convert String => JsonObject ===================
		JsonObject jsonObject = new JsonObject(buffer);
		System.out.println(jsonObject.toString());
		
		//=================================== convert JsonObject => Java Object ================
		GoogleOauth2 auth = jsonObject.mapTo(GoogleOauth2.class);
		
		System.out.println(auth.getWeb().getRedirectUris());
	}


}
