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
import hung.com.json.model.User2;
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
		
//		JsonObject_BytesArray();
		testUTF8_String_bytes();

	}
	
	/**
	 * Base64 sẽ lưu 6bits = 1 char = 1 byte
	 * vd: 
	 *    32 bytes array = 256 bit/6 = 42.6 = 43 bytes Base64
	 * 1 char base64 value = 1 char ASCII = 1char UTF8 = 1 byte  (2 bít đầu = 0)   
	 * Lưu trên SQL Base64 dùng char-set = ASCII, 1 char = 1 byte (vì Base64 ko có ký tự đặc biệt)
	 * UTF8 sql chỉ lưu character từ 1 tới 3 bytes (ko hỗ trợ 4 bytes).
	 * String in java là UTF16 ko phải Unicode. Nghĩa là có character sẽ chiếm 4bytes
	 * 
	 *  JsonObject vertx cho phép lưu ByteArray và tự động chuyển qua Base64 nếu JsonObject.getString() or .toString
	 *  SQL cho phép lưu dữ liệu dạng binary, BLOB => vì thế JDBC đương nhiên phải cung cấp cách truyền Binary và BLOB
	 *  
	 */
	
	public static void JsonObject_BytesArray() {
		 
		byte[] bytes = new byte[] {(byte) 0xAB,(byte) 0xCD,(byte) 0xEF, 0x00};
		
		//================================ bytes Array to JsonObject ========================
		/**
		 * Json chỉ có kiểu String UTF8, và number, boolean. Kiểu bytes bản chất là utf8 bytes
		 * nhưng JsonObject lại cho phép lưu kiểu Byte[] thuần trong nó => điểm mạnh.
		 * nếu dùng JsonObject.getBinary() nó sẽ convert ngược Base64.decode(StringBase64) = byte[] => sai
		 *  
		 */
		JsonObject object = new JsonObject().put("byte", bytes);

		System.out.println("object.getString('byte') = " + object.getString("byte"));   //bytes đc convert ra base64
		System.out.println("base64 = " + Base64.getEncoder().encodeToString(bytes));   //bytes đc convert ra base 64 như trên
		
		//=============================== JsonObject to bytes Array ================
		// trong jsonObject lưu ở định dạng String (ko có ký tự đặc biệt vi phạm chuẩn Json)
		// khi gọi getBinary() thì nó tự hiểu là convert String Base64 về dạng byte[]
		byte[] bytes2 = object.getBinary("byte"); // = bytes
		
		
		System.out.println("bytes2 base64 = " + Base64.getEncoder().encodeToString(bytes2)); //kết quả như trên
	}
	
	public static void testUTF8_String_bytes() {
		//
		JsonObject o = new JsonObject().put("password", "tự học");
		
		/**
		 * Json chỉ có kiểu String UTF8, và number, boolean. Kiểu bytes bản chất là utf8 bytes
		 * nhưng JsonObject lại cho phép lưu kiểu Byte[] thuần trong nó => điểm mạnh.
		 * nếu dùng JsonObject.getBinary() nó sẽ convert ngược Base64.decode(StringBase64) = byte[] => sai
		 *  
		 */
		// exception vì string ko phải base64, ko convert ngược đc
//		byte[] bytes1 = o.getBinary("password");
		
//		System.out.println("bytes1 base64 = " + Base64.getEncoder().encodeToString(bytes1));
		
		byte[] bytes2 = o.getString("password").getBytes(); //default charset = utf8
		
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
		// các thành phần của user phải có hàm .toString()
		User user  = new User("Hungbeo",11);
		JsonObject  jsonObjectFromUser = JsonObject.mapFrom(user);
		System.out.println(jsonObjectFromUser.toString());
	}
	
	public static void JsonObject2Java(){
		try {
			//=================================== convert JsonObject => Java Object ================
			JsonObject jsonUser = new JsonObject().put("name", "Happy")
												.put("yearOld", 18);
			
			/**
			 *  lấy tên field ở JavaObject (giống Gson và Jackson) ko cần anotation=> dùng JPA để gen từ Table ra ok
			 *  dùng tool online cũng ok: https://www.site24x7.com/tools/json-to-java.html
			 *  Dùng constructor cho final field => ko dùng set()
			 */
			User user = jsonUser.mapTo(User.class);
			System.out.println("************* {" + user.getName() + "," + user.getYearOld() + "}" );
			
			/**
			 * ko cần construction, chỉ cần hàm set() là đủ
			 */
			User2 user2 = jsonUser.mapTo(User2.class);
			System.out.println("************* {" + user2.getName() + "," + user2.getYearOld() + "}" );
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
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
