package hung.com.json.model;

/**
 * ko cần construction, chỉ cần hàm set() là đủ
 */
public class User2 {
	private String name;
	private int yearOld;

	public User2(){}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getYearOld() {
		return yearOld;
	}
	public void setYearOld(int yearOld) {
		this.yearOld = yearOld;
	}
	
}
