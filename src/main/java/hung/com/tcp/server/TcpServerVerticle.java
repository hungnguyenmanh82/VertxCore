package hung.com.tcp.server;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetServer;
import io.vertx.core.net.NetSocket;

public class TcpServerVerticle extends AbstractVerticle {

    @Override
    public void start() throws Exception {
        NetServer server = vertx.createNetServer();
        
        //Asynchronous Event only 1 time when Socket client connect to server
        server.connectHandler(new Handler<NetSocket>() {

            @Override
            public void handle(NetSocket netSocket) {
                System.out.println("Incoming connection!");
                
                //=============== read data from socket ====================== 
                netSocket.handler(new Handler<Buffer>() {
                	//asynchronous event 'read' called many times => streaming data
                    @Override
                    public void handle(Buffer buffer) {
                        System.out.println("=> incoming data: "+buffer.length());
                        System.out.println(buffer.getString(0, buffer.length()));
                    }
                });
                
                //====================== write data to socket =================
                Buffer outBuffer = Buffer.buffer(1000); //size of buffer
                outBuffer.appendString("<= (send) response from server asynchronous");
                
                //Vertx don't have event for write =>much check it
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
         
        server.listen(10000);
//        Thread.currentThread().sleep(5000);//5000ms
        //server.close();
        server.close(new Handler<AsyncResult<Void>>() {
            @Override
            public void handle(AsyncResult result) {
                if(result.succeeded()){
                	 System.out.println("tcp server was closed");
                }
            }
        });
    }
}