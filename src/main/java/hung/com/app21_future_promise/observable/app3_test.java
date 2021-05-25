package hung.com.app21_future_promise.observable;

public class app3_test {

	public static void main(String[] args) {
		/**
		 * Học tập cách mà RxJs trên javascript làm
		 */
		Observable<String> observable = Observable.<String>create(observer->{
			// should be asynchronous here
			System.out.println(" async funtion start here");
			
			
			observer.next("next1");
			
			//
			observer.next("next2");
			
			observer.complete();
			
			
		});

		// chỉ khi hàm này đc gọi thì Handler<Observer<String>> mới đc kích hoạt
		observable.subscribe(new Observer<String>() {
			
			@Override
			public void next(String t) {
				System.out.println(t);
				
			}
			
			@Override
			public void error(String err) {
				System.out.println(err);
				
			}
			
			@Override
			public void complete() {
				System.out.println("complete Observable called");
				
			}
		});
		

	}

}
