package com.cloud.jian.core.nio;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public class SelectorServer {

	private static final int PORT = 1234;

	private static ByteBuffer byteBuffer = ByteBuffer.allocate(1024);

	public static void main(String[] args) {
		try {
			ServerSocketChannel ssc = ServerSocketChannel.open();
			ssc.bind(new InetSocketAddress(PORT));
			ssc.configureBlocking(true);

			Selector selector = Selector.open();
			// 1 register
			ssc.register(selector, SelectionKey.OP_ACCEPT);

			while (true) {
				// 2 select
				int n = selector.select();
				if (n == 0) {
					continue;
				}
				// 3 轮询SelectionKey
				Iterator<SelectionKey> it = (Iterator<SelectionKey>) selector.selectedKeys().iterator();
				while (it.hasNext()) {
					SelectionKey key = it.next();
					// 如果满足isAcceptable条件，则必定是一个ServerSocketChannel
					if (key.isAcceptable()) {
						// 得到一个连接好的SocketChannel，并把它注册到selector上，兴趣操作为READ
						ServerSocketChannel ssChannel = (ServerSocketChannel) key.channel();
						SocketChannel scChannel = ssChannel.accept();
						scChannel.configureBlocking(false);
						scChannel.register(selector, SelectionKey.OP_READ);
					}
					// 如果满足isReadable，则必定是一个SocketChannel
					if (key.isReadable()) {
						// 读取通道中的数据
						SocketChannel sChannel = (SocketChannel) key.channel();
						readFromChannel(sChannel);
					}
					// 4 remove SelectionKey
					it.remove();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {

		}
	}

	private static void readFromChannel(SocketChannel channel) {
		int count = 0;
		byteBuffer.clear();
		try {
			while ((count = channel.read(byteBuffer)) > 0) {
				byteBuffer.flip();
				byte[] bytes = new byte[byteBuffer.remaining()];
				byteBuffer.get(bytes);
				System.out.println("read from client : " + new String(bytes));
			}
			if (count < 0) {
				channel.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


}
