package hung.com.json.model;

import java.util.HashMap;
import java.util.Map;


/**
 * State ở trong User Table để tối ưu hóa size 
 */

/**
 *   state = 1. User có quyền sử dụng
 *   state = 0. User bị ban nick ko thể sử dụng đc => chưa đc cấp quyền sử dụng
 *	 state = -1. User bị delete rồi
 *	 state = -2  user is waiting for activating  (lần đầu đăng nhập, cần phải xác nhận email để activate)
 */
public enum EUserState {
	NORMAL(1),
	BAN(0),
	DELETE(-1),
	WAIT_ACTIVATE(-2);
	public final int state;

	
	private static final Map<Integer,EUserState> map = new HashMap<Integer, EUserState>();
	
	// static sẽ đc thực hiên ngay khi class-load 1 lần duy nhất
	// values(): là array of EUserState
	static {
		for(EUserState v: values()) {
			map.put(v.state, v);
		}
	}
	

	private EUserState(int state) {
		this.state = state;
	}
	
	/**
	 * 
	 * @param state
	 * @return
	 */
	public static EUserState search(int state) {
		EUserState res = map.get(state);
		
		if(res== null) {
			return EUserState.BAN;
		}
		return res;
	}
}
