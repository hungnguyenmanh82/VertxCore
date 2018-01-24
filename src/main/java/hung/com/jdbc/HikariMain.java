package hung.com.jdbc;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.jdbc.JDBCClient;
import io.vertx.ext.sql.ResultSet;
import io.vertx.ext.sql.SQLClient;
import io.vertx.ext.sql.SQLConnection;


/**
 * xem config cho C3P0 để hiểu phương pháp:
 * http://vertx.io/docs/vertx-jdbc-client/java/
 * 
 * config cho Hikari:
 * https://github.com/brettwooldridge/HikariCP#configuration-knobs-baby
 */
public class HikariMain {

	public static void main(String[] args) {
		Vertx vertx = Vertx.vertx();
		
		//tùy vào C3P0 hay Hikari, BonCP mà tên các trường config sẽ khác nhau
		//xem tài liệu cụ thể của C300 và Hikari để biết tên các trường này
		JsonObject config = new JsonObject()
				.put("provider_class", "io.vertx.ext.jdbc.spi.impl.HikariCPDataSourceProvider")
				.put("url", "jdbc:hsqldb:mem:test?shutdown=true")  ////auto detect base on url
				.put("driverClassName", "org.hsqldb.jdbcDriver")  //= "driver_class" của C3P0
				.put("maximumPoolSize", 30)          //= "max_pool_size" của  C3P0
				.put("username", "user_name")        //= "user" của C3P0
				.put("password", "1234");

		//share: dùng chung giữa các Verticle
		SQLClient client = JDBCClient.createShared(vertx, config);
		        //MyDataSource: là tên database
		//		SQLClient client = JDBCClient.createShared(vertx, config, "MyDataSource");
		//		SQLClient client = JDBCClient.create(vertx, dataSource);
		        //ko share giữa các verticles
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

	}

}
