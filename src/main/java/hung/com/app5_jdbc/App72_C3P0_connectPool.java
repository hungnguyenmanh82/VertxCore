package hung.com.app5_jdbc;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.jdbc.JDBCClient;
import io.vertx.ext.sql.ResultSet;
import io.vertx.ext.sql.SQLClient;
import io.vertx.ext.sql.SQLConnection;


/**
 * http://vertx.io/docs/vertx-jdbc-client/java/
 * 
 * Cần add lib của MySQl, Hsqldb... vào Maven thì mới chạy đc
 * 
 */
public class App72_C3P0_connectPool {

	public static void main(String[] args) {
		Vertx vertx = Vertx.vertx();
		
		// config: chứa các config của database
		// C3P0: default by VertX
		// .put("provider_class", "io.vertx.ext.jdbc.spi.impl.C3P0DataSourceProvider")
		//tùy vào C3P0 hay Hikari, BonCP mà tên các trường config sẽ khác nhau
		//xem tài liệu cụ thể của C300 và Hikari để biết tên các trường này
		JsonObject config = new JsonObject()				
				.put("url", "jdbc:hsqldb:mem:test?shutdown=true")
				.put("driver_class", "org.hsqldb.jdbcDriver")    //auto detect base on url
				.put("max_pool_size", 30)
				.put("user", "user_name")
				.put("password", "1234");

		//share: dùng chung giữa các Verticle
		SQLClient client = JDBCClient.createShared(vertx, config);
		//MyDataSource: là tên database
		//		SQLClient client = JDBCClient.createShared(vertx, config, "MyDataSource");
		//		SQLClient client = JDBCClient.create(vertx, dataSource);
		//		SQLClient client = JDBCClient.createNonShared(vertx, config);


		client.getConnection(new Handler<AsyncResult<SQLConnection>>() {
			// a socket connect ok to Database including authentication
			@Override
			public void handle(AsyncResult<SQLConnection> res) {
				if (res.succeeded()) {
					SQLConnection connection = res.result();
					connection.query("SELECT * FROM some_table",new Handler<AsyncResult<ResultSet>>() {
						//query callback ok. sau khi dữ liệu trả về toàn bộ từ câu lệnh query
						@Override
						public void handle(AsyncResult<ResultSet> res2) {
							if (res2.succeeded()) {
								ResultSet rs = res2.result();  //result save in Ram
								// Do something with results
							}

						}
					});


				} else {
					// Failed to get connection - deal with it
				}

			}
		});

		//Java Lambda syntax => the same above (ko nên dùng)
		/*		client.getConnection(res -> {
			  if (res.succeeded()) {

			    SQLConnection connection = res.result();

			    connection.query("SELECT * FROM some_table", res2 -> {
			      if (res2.succeeded()) {

			        ResultSet rs = res2.result();
			        // Do something with results
			      }
			    });
			  } else {
			    // Failed to get connection - deal with it
			  }
			});*/

	}

}
