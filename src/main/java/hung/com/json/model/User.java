package hung.com.json.model;

public class User {
	/**
	 * dùng final hay hơn => code dễ nhìn
	 */
	private  final String name;
	private final int yearOld;

	public User(){
		this.name = null;
		this.yearOld = 0;
	}

	/**
	 * ko cần hàm set() vì đã khai báo final rồi
	 * chỉ thiết lập 1 lần lúc khởi tao
	 */
	public User(String name, int yearOld) {
		this.name = name;
		this.yearOld = yearOld;
	}
	
	public String getName() {
		return name;
	}

	public int getYearOld() {
		return yearOld;
	}

	
}
