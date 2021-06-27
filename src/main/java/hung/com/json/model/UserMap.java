package hung.com.json.model;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;

public class UserMap {
	/**
	 * Ko cần constructor, ko cần setter/getter vẫn ok
	 */
	public   String name;
	public  int yearOld;
	/**
	 * dùng getter rồi nên để là private
	 */
	private Map<String, String> mapInfo;  // từ động convert Map về dạng Json luôn (giống như List về Array)
	
	/**
	 * phải có default constructor cho Jackson
	 */
	public UserMap() {
		
	}
	
	public UserMap(String name, int yearOld, Map<String, String> mapInfo) {
		super();
		this.name = name;
		this.yearOld = yearOld;
		this.mapInfo = mapInfo;
	}
	
	public static UserMap newInstanceSample() {
		Map<String, String> mapInfo = new HashMap<String, String>();
		
		mapInfo.put("chucVu", "giám đốc");
		mapInfo.put("tenVo", "Võ thị Sáu");
		
		return new UserMap("Năm Cam", 49, mapInfo);
	}

	public Map<String, String> getMapInfo() {
		return mapInfo;
	}
	
	
	
}
