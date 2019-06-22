package hung.com.tcp.server2;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.Context;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetSocket;

/**
 * 

 */
public class TcpServletVerticle extends AbstractVerticle {

	private NetSocket mNetSocket;
	
	
	
	public TcpServletVerticle(NetSocket mNetSocket) {
		super();
		this.mNetSocket = mNetSocket;
	}


	@Override
	public void start() throws Exception {
		super.start();  //phải gọi hàm này thì vertx.deploymentIDs() mới cập nhật giá trị.
		
        //=============== read event from Socket ======================
        mNetSocket.handler(new Handler<Buffer>() {	
        	//asynchronous event 'read' called many times => streaming data
        	// vì thế đoạn này phải chạy trên Standard Verticle (ko dùng Worker Verticle đc)
			@Override
			public void handle(Buffer buffer) {
				// read Buffer then write back to Socket
				System.out.println("read socket then write back to socket: thread="+Thread.currentThread().getId());
				System.out.println("length of buffer allocated = "+buffer.length());
                System.out.println(buffer.getString(0, buffer.length()));
                
                
                //====================== write data to socket =================
                // Read và write trên cùng 1 Thread cũng ok vì bản chất vẫn là Asynchronous Event
                Buffer outBuffer = Buffer.buffer(1000); //size of buffer
                outBuffer.appendString("<= (send) response from server asynchronous");
                
                //Vertx don't have event khi write socket finish
                //writeQueue => chỉ số buffer đc lưu vào Queue để ghi vào socket
                
                mNetSocket.setWriteQueueMaxSize(4);
                if(mNetSocket.writeQueueFull() == false) {//asynchronous function check
                	//asynchronous by Vertx, it finish when writeQueueFull() = false
                	mNetSocket.write(outBuffer); 
                }else{ //socket write buffer full
                	mNetSocket.pause(); // dừng Read socket nếu cần (vd: RAM hết).
                	
                	// bắt sự kiện khi Socket write buffer ready to write sau khi full.
                	mNetSocket.drainHandler(done -> {
                		mNetSocket.resume(); //khởi động lai kiện socket read buffer (lệnh Pause() trc đó đã dừng handler này lại.
                      });

                }
				
			}
		});


	}


	@Override
	public void stop() throws Exception {
		super.stop(); //phải gọi hàm này thì vertx.deploymentIDs() mới cập nhật giá trị.
		System.out.println("TcpServletVerticle.stop()");
	}






}
