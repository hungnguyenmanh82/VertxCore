package hung.com.json.model;

public class User {
	private String name;
	private int yearOld;

	public User(){}

	public User(String name, int yearOld) {
		this.name = name;
		this.yearOld = yearOld;
	}
	
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
