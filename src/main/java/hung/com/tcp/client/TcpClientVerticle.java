package hung.com.tcp.client;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetClient;
import io.vertx.core.net.NetSocket;

/**
 * read/write socket of server and client are the same
 * Phần này test với phần Server
 */
public class TcpClientVerticle extends AbstractVerticle {

	public void start() {
		NetClient tcpClient = vertx.createNetClient();
		
		tcpClient.connect(10000, "localhost",
				new  Handler<AsyncResult<NetSocket>>(){
			//asynchronous event when socket is connected => called only 1 time
			@Override
			public void handle(AsyncResult<NetSocket> result) {
				NetSocket netSocket = result.result();

				//====================== write data to socket =================
				Buffer outBuffer = Buffer.buffer(1000); //size of buffer
				outBuffer.appendString("<= client send(write) data to server asynchronously");

				//Vertx don't have event for write like NIO selector =>much check it
				if(netSocket.writeQueueFull() == false) {//asynchronous function check
					//asynchronous by Vertx, it finish when writeQueueFull() = false
					netSocket.write(outBuffer); 
				}

				//=============== read data from socket ====================== 
				netSocket.handler(new Handler<Buffer>() {
					//asynchronous event 'read' called many times => streaming data
					@Override
					public void handle(Buffer buffer) {
						//it run on context of Socket, not run on context of This vertical
						System.out.println("=> incoming data: length = "+buffer.length());
						System.out.println(buffer.getString(0, buffer.length()));
					}
				});
				
                //========================== close socket ======================
//              netSocket.close();
              netSocket.closeHandler(new Handler<Void>() {
					@Override
					public void handle(Void event) {
						System.out.println("socket is closed: ");
					}
				});

			}
		});

	}
}
