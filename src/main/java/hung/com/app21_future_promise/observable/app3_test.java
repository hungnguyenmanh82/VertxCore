package hung.com.app21_future_promise.observable;

public class app3_test {

	public static void main(String[] args) {
		Observable<String> observable = Observable.<String>create(observer->{
			// should be asynchronous here
//			observer.next("next");
			System.out.println(" async funtion here");
		});

		observable.subscribe(res->{
			System.out.println(res);
		}, null, null);
		
		// đưa observer cho Asynchronous function
		Observer<String> observer = observable.getObserver();
		
		// asynchronous function dùng observer để callback
		observer.next("ko co viec gi khó");
	}

}
