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

/**
 vertx JsonObject lib rất cơ động, hay hơn Gson và Jackson nhiều
 Có thể dùng JsonObject để convert JDBC Resultset về java Object dễ dàng => varChar(UTF8) sẽ chuyển thành String
 https://vertx.io/docs/vertx-core/java/#_json
 
 JsonObject chứa dữ liệu String (ko phải UTF8) vì trên java các phép toán UTF8 rất chậm.
 JsonObject save bytes dạng Base64 string=> tránh các ký tự đặc biệt của Json.
 
 JsonObject.toString().getbytes(UTF-8) để trả về cho back-end.

 */
public class App81_Json {

	public static void main(String[] args) throws Exception {

//		String2JsonObject();
//		JsonObject2String();
//		
//		JsonObject2Java();
//		Java2JsonObject();
//		
//		Map2JsonObject();
//		JsonObject2Map();
		
//		JsonFileToJava();
		
//		JsonArray();
//		JsonArray2List();
		
		JsonObject_BytesArray();

	}
	
	public static void JsonObject_BytesArray() {
		 
		byte[] bytes = new byte[] {(byte) 0xAB,(byte) 0xCD,(byte) 0xEF, 0x00};
		
		//================================ bytes Array to JsonObject ========================
		
		JsonObject object = new JsonObject().put("byte", bytes);
		System.out.println(object.toString()); // json String: các thành phần đã đc convert ra Base64 thì mới lưu vào đc Json
		
		/**
		 * Base64 sẽ lưu 6bits = 1 char = 1 byte
		 * vd: 
		 *    32 bytes array = 256 bit/6 = 42.6 = 43 bytes Base64
		 * Lưu trên SQL Base64 dùng char-set = ASCII, 1 char = 1 byte (vì Base64 ko có ký tự đặc biệt)
		 * ko nên lưu SQL Base64 với char-set = UTF8   
		 */
		System.out.println("o.getString() = " + object.getString("byte"));
		System.out.println("base64 = " + Base64.getEncoder().encodeToString(bytes));
		
		//=============================== JsonObject to bytes Array ================
		// trong jsonObject lưu ở định dạng String (ko có ký tự đặc biệt vi phạm chuẩn Json)
		// khi gọi getBinary() thì nó tự hiểu là convert String Base64 về dạng byte[]
		byte[] bytes2 = object.getBinary("byte"); 
		
		
		System.out.println("bytes2 base64 = " + Base64.getEncoder().encodeToString(bytes2));
	}
	
	public static void JsonArray(){

		
		//====================== Json Array ==============================
		//cách 1:
		String jsonString1 = "[\"foo\",\"bar\"]";
		JsonArray jsonArray = new JsonArray(jsonString1);
		System.out.println(jsonArray.toString());
		
		//cách 2:
		JsonArray jsonArray2 = new JsonArray();
		
		// JsonArray cho phép các phần tử ở các kiểu khác nhau
		jsonArray2.add("abc")
		         .add(123)
		         .add(false);
		System.out.println(jsonArray2.toString());
		
		//=================================================== forEach =============
		List<String> ListOfJsonObject = jsonArray.getList();
		//java 8: lambda
		ListOfJsonObject.forEach(jsonJWS->{
			System.out.println(jsonJWS.toString());
		});
	

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
	
	public static void JsonArray2List(){
		//======================================================================
		Vertx vertx = Vertx.vertx();
		Buffer buffer = vertx.fileSystem().readFileBlocking(App81_Json.class.getResource("JWK.json").getPath());

		//===========================================convert String => JsonObject ===================
		JsonObject jsonObject = new JsonObject(buffer);
		
		System.out.println("================================== test JsonArray2List ==========");
		jsonObject.getJsonArray("keys").forEach(o->{
			if(o instanceof JsonObject) {
				//đoạn này ko nhảy vào
				System.out.println("JsonObject");
			}
		});
		
		// đoạn code này sinh exception: vì lỗi ép kiểu
//		List<JsonObject> list = jsonObject.getJsonArray("keys").getList();
		
		//Java8
		List<JsonObject> list = jsonObject.getJsonArray("keys").stream()
				                  .<JsonObject>map(o-> { return (JsonObject)o;})
				                  .collect(Collectors.<JsonObject>toList());
		
		list.forEach(o-> System.out.println(o.toString()));
	}
	
	public static void String2JsonObject() {
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
	}
	public static void JsonObject2String() {
		//==================================== create JsonObject ===============================
		JsonObject jsonObject2 = new JsonObject();
		jsonObject2.put("foo", "bar")
					.put("num", 123)
					.put("mybool", true);
		System.out.println(jsonObject2.toString());
	} 
	
	public static void Java2JsonObject(){
		//================================ convert java Object => JsonObject ================
		
		User user  = new User("Hungbeo",11);
		JsonObject  jsonObjectFromUser = JsonObject.mapFrom(user);
		System.out.println(jsonObjectFromUser.toString());
	}
	
	public static void JsonObject2Java(){
		//=================================== convert JsonObject => Java Object ================
		JsonObject jsonUser = new JsonObject().put("name", "Happy")
											.put("yearOld", 18);
		
		/**
		 *  lấy tên field ở JavaObject (giống Gson và Jackson) => dùng JPA để gen từ Table ra ok
		 */
		User user = jsonUser.mapTo(User.class);
		System.out.println("************* {" + user.getName() + "," + user.getYearOld() + "}" );
	}
	
	
	public static void Map2JsonObject() {
		//===================================convert Map => JsonObject ===================
		Map<String, Object> map = new HashMap<>();
		map.put("foo", "bar");
		map.put("xyz", 3);
		JsonObject jsonObject = new JsonObject(map);
		System.out.println(jsonObject.toString());
		
		
	}
	
	public static void JsonObject2Map() {
		Vertx vertx = Vertx.vertx();
		Buffer buffer = vertx.fileSystem().readFileBlocking(App81_Json.class.getResource("googleAuth2.json").getPath());

		//===========================================convert String => JsonObject ===================
		JsonObject jsonObject = new JsonObject(buffer);
//		JsonObject jsonObject = buffer.toJsonObject();
		System.out.println(jsonObject.toString());
		
		Map<String, Object> map = jsonObject.getMap();
		
		
	}


	public static void JsonFileToJava(){

		Vertx vertx = Vertx.vertx();
		Buffer buffer = vertx.fileSystem().readFileBlocking(App81_Json.class.getResource("googleAuth2.json").getPath());

		//===========================================convert String => JsonObject ===================
		JsonObject jsonObject = buffer.toJsonObject();
		System.out.println(jsonObject.toString());
		
		//=================================== convert JsonObject => Java Object ================
		GoogleOauth2 auth = jsonObject.mapTo(GoogleOauth2.class);
		
		System.out.println(auth.getWeb().getRedirectUris());
	}


}
