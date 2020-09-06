package hung.com.app21_future_promise.observable;

/**
 * phải đổi tên để tránh trùng với thư viện của java
 */
public interface Observer<T> {
	void next(T t);
	void error(String err);
	void complete();
}
