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
 * xem config cho C3P0 để hiểu phương pháp:
 * http://vertx.io/docs/vertx-jdbc-client/java/
 * 
 * config cho BoneCP:
 * http://www.jolbox.com/index.html?page=http://www.jolbox.com/configuration.html
 */
public class App71_BoneCP_connectPool {

	public static void main(String[] args) {
		Vertx vertx = Vertx.vertx();
		
		//tùy vào C3P0 hay Hikari, BonCP mà tên các trường config sẽ khác nhau
		//xem tài liệu cụ thể của C300 và Hikari để biết tên các trường này
		JsonObject config = new JsonObject()
				.put("provider_class", "io.vertx.ext.jdbc.spi.impl.BoneCPDataSourceProvider")
				.put("url", "jdbc:hsqldb:mem:test?shutdown=true")
				.put("driver_class", "org.hsqldb.jdbcDriver")
				.put("max_pool_size", 30);

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
						//query callback ok.
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
