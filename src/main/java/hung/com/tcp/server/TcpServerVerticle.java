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
        	//Asynchronous Event only 1 time when Socket client connect to server
            @Override
            public void handle(NetSocket netSocket) {
            	System.out.println("incoming connect request!: thread="+Thread.currentThread().getId());
                                
                //=============== read data from socket ====================== 
                netSocket.handler(new Handler<Buffer>() {
                	//asynchronous event 'read' called many times => streaming data
                    @Override
                    public void handle(Buffer buffer) {
                    	System.out.println("=> read: thread="+Thread.currentThread().getId());
                        System.out.println("buffer size = "+buffer.length());
                        System.out.println(buffer.getString(0, buffer.length()));
                    }
                });
                
                //====================== write data to socket =================
                Buffer outBuffer = Buffer.buffer(1000); //size of buffer
                outBuffer.appendString("<= (send) response from server asynchronous");
                
                //Vertx don't have event khi write socket finish
                //writeQueue => chỉ số buffer đc lưu vào Queue để ghi vào socket
//                netSocket.setWriteQueueMaxSize(2);
                if(netSocket.writeQueueFull() == false) {//asynchronous function check
                	//asynchronous by Vertx, it finish when writeQueueFull() = false
                	netSocket.write(outBuffer); 
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
        
        //use java Lambda syntax here for the third parameter
/*        server.listen(1234, "localhost", res -> {
            if (res.succeeded()) {
              System.out.println("Server is now listening!");
            } else {
              System.out.println("Failed to bind!");
            }
          });*/
        
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