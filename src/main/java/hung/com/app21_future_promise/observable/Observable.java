package hung.com.app21_future_promise.observable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.vertx.core.Handler;

/**
 * Tự thiết kế Observable pattern giống với Rxjs trên javaScript
 * 
 * Vì thư viện Reactive (Rx) của Vertx rất khác => ko giống với RxJs  
 */
public class Observable<T> {
    private Observer<T> observerSubscribe = null;
	private Handler<Observer<T>> handler = null;
    
    /**
     * để private để dùng .<T>create() giống cách mà VertxFuture dùng
     */
    private Observable(Handler<Observer<T>> handler) {
    	// Rxjs khi nào hàm .subscribe() đc gọi thì mới bắt đầu call handler.handle()
//    	handler.handle(observer);
    	
    	this.handler = handler;
    }
	
    /**
     * Thiết kế giống cách Future.<T>future(Handler<Promise<T>> handler)
     * 
      */
    public static <T> Observable<T> create(Handler<Observer<T>> handler){
    	return new Observable<T>(handler);
    }
    
	private Observer<T> observer = new Observer<T>(){

		@Override
		public void next(T t) {
			// thư viện Vertx check null kiểu này
			Objects.requireNonNull(t);
			
			observerSubscribe.next(t);
			
		}

		@Override
		public void error(String err) {
			Objects.requireNonNull(err);
			
			observerSubscribe.error(err);
			
		}

		@Override
		public void complete() {
			
			observerSubscribe.complete();
		}
		
	};
	
	public void subscribe(Observer<T> observer) {
		observerSubscribe = observer;
		// Rxjs khi nào hàm này đc gọi thì mới bắt đầu call handler.handle()
		handler.handle(observer);
	}
	
	public Observer<T> getObserver(){
		return observer;
	}

}
