package hung.com.future;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.Context;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.file.FileSystem;

/**
 * 


 */
public class FutureCreateFileVerticle extends AbstractVerticle {

	private Future<Void> futureCreateFile;
	
	
	public FutureCreateFileVerticle(Future<Void> futureCreateFile) {
		super();
		this.futureCreateFile = futureCreateFile;
	}

	
	@Override
	public void start(Future<Void> startFuture) throws Exception {	
		
		System.out.println("FutureCreateFileVerticle: thread=" + Thread.currentThread().getId());
		// Context context = vertx.getOrCreateContext();
		// file sẽ đc create/write trên threadpool của Verticle context (đã test)
		FileSystem fs = vertx.fileSystem();
		// "foo.txt" đc tạo ra ở project folder khi Debug Run
		fs.createFile("foo1.txt", futureCreateFile); //fut1.completer()
		
		//nếu CreateFile hoàn thành hoặc fail thì nó sẽ send Event tới Context của futureCreateFile bằng cách
		// call futureCreateFile.handle(AsyncResult<Void> result) => event chứa result
		// tại context của futureCreateFile ta dùng hàm futureCreateFile.setHandler() để bắt Event lấy result
	}

	@Override
	public void stop(Future<Void> stopFuture) throws Exception {
		System.out.println("MyVerticle.stop(): thread=" + Thread.currentThread().getId());
	}

}
