package hung.com.app21_future_promise.observable;

import java.util.ArrayList;
import java.util.List;

import io.vertx.core.Handler;

public class Observable<T> {
    private List<Handler<T>> successHandler = new ArrayList<Handler<T>>();
	
    Observable(){
	}
    
    private Observable(Handler<Observer<T>> handler) {
    	handler.handle(observer);
    }
	
    public static <T> Observable<T> create(Handler<Observer<T>> handler){
    	return new Observable<T>(handler);
    }
    
	private Observer<T> observer = new Observer<T>(){

		@Override
		public void next(T t) {
			Observable.this.successHandler.forEach(handler->{
				handler.handle(t);
			});
			
		}

		@Override
		public void error(String err) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void complete() {
			// TODO Auto-generated method stub
		}
		
	};
	
	public void subscribe(Handler<T> handlerSucess, Handler<String> handlerError, Handler<Void> handlerComplete) {
		successHandler.add(handlerSucess);
	}
	
	public Observer<T> getObserver(){
		return observer;
	}

}
