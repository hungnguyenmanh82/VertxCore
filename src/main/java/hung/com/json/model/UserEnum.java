package hung.com.json.model;

import io.vertx.core.json.JsonObject;

/**
 * ko cần construction, chỉ cần hàm set() là đủ
 */
public class UserEnum {
	public final String name;
	public final int yearOld;
	public final EUserState state;



	public UserEnum(String name, int yearOld, EUserState state) {
		super();
		this.name = name;
		this.yearOld = yearOld;
		this.state = state;
	}



	@Override
	public String toString() {
		/**
		 * test thu Enum xem the nao
		 */
		return JsonObject.mapFrom(this).toString();
	}

}
