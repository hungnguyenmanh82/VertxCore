package hung.com.tcp.server;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetServer;
import io.vertx.core.net.NetServerOptions;
import io.vertx.core.net.NetSocket;


public class TcpServerVerticle extends AbstractVerticle {

    @Override
    public void start() throws Exception {
        NetServer server = vertx.createNetServer(); //default is all network (localhost, other network).
        
/*        NetServer server = vertx.createNetServer(
                new NetServerOptions().setPort(1234).setHost("localhost")
            );
            
            */
        
        server.connectHandler(new Handler<NetSocket>() {
        	//Handler Event đc gắn với Verticle Context của currentThread
            // current thread => TcpServerVerticle context
            @Override
            public void handle(NetSocket netSocket) {
            	System.out.println("******** new socket connected : thread="+Thread.currentThread().getId()+ ", ThreadName="+Thread.currentThread().getName());
                 
            	
                //=============== read data from socket ====================== 
                netSocket.handler(new Handler<Buffer>() {
                	//asynchronous event 'read' called many times => streaming data
                	//NetSocket đã đc gắn với context của Server rồi. 
                	//code này chạy trên thread của Verticle context
                	//kể cả khi chuyển sang Vertical thì cũng chạy trên thread khác
                    @Override
                    public void handle(Buffer buffer) {
                    	System.out.println("=> read: thread="+Thread.currentThread().getId());
                        System.out.println("bufferSize = "+buffer.length());
                        System.out.println("content = "+ buffer.getString(0, buffer.length()));
                    }
                });
                
                //====================== write data to socket =================
                Buffer outBuffer = Buffer.buffer(1000); //size of buffer
                outBuffer.appendString("Content from server1,");
                
                //Vertx don't have event khi write socket finish
                //writeQueue => chỉ số buffer đc lưu vào Queue để ghi vào socket
//                netSocket.setWriteQueueMaxSize(2);
                if(netSocket.writeQueueFull() == false) {//asynchronous function check
                	//asynchronous by Vertx, it finish when writeQueueFull() = false
                	netSocket.write(outBuffer); 
                }else{ //socket write buffer full
                	netSocket.pause(); // dừng Read socket nếu cần (vd: RAM hết).
                	
                	// bắt sự kiện khi Socket write buffer ready to write sau khi full.
                	netSocket.drainHandler(done -> {
                		netSocket.resume(); //khởi động lai kiện socket read buffer (lệnh Pause() trc đó đã dừng handler này lại.
                      });
//                	netSocket.resume(); // khởi động lại việc bắt sự kiện Read Socket
                }
                
                //==========================close socket ======================
//                netSocket.close();
                netSocket.closeHandler(new Handler<Void>() {
					@Override
					public void handle(Void event) {
						System.out.println("socket is closed: ");
					}
				});
            }
        });
         
//        server.listen(); //trường hợp đã set tcp port
        server.listen(10000);

        
//        Thread.currentThread().sleep(5000);//5000ms
        //server.close();
/*        server.close(new Handler<AsyncResult<Void>>() {
            @Override
            public void handle(AsyncResult result) {
                if(result.succeeded()){
                	 System.out.println("tcp server was closed");
                }
            }
        });*/
    }
}