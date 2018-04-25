package com.cloud.jian.core.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class ServerClient {

	static class Client extends Thread {
		
		private String name;
		private Random random = new Random(74);
		
		Client(String name) {
			this.name = name;
		}
		
		@Override
		public void run() {
			try {
				SocketChannel socketChannel = SocketChannel.open();
				socketChannel.configureBlocking(false);
				socketChannel.connect(new InetSocketAddress(1234));
				while (!socketChannel.finishConnect()) {
					TimeUnit.MILLISECONDS.sleep(100);
				}
				ByteBuffer buffer = ByteBuffer.allocate(1024);
				for (int i = 0; i < 5; i++) {
					TimeUnit.MILLISECONDS.sleep(100 * random.nextInt(10));
					String str = "message from " + name + ", number : " + i;
					buffer.put(str.getBytes());
					buffer.flip();
					while (buffer.hasRemaining()) {
						socketChannel.write(buffer);
					}
					buffer.clear();
				}
				socketChannel.close();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
}
