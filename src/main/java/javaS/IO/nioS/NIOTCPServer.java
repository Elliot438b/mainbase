package javaS.IO.nioS;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

import javaS.IO.socketS.Base;

public class NIOTCPServer extends Base {

    public static void main(String[] args) {
        new Thread(new ReactorTask(port), "nio-reactor-001").start();
    }

    /**
     * nio服务端与io的ServerSocket的编写流程极为相似，nio包已经封装好相关方法。
     */
    public void seqNio() throws IOException {
        // 1，open服务端Socket通道。
        ServerSocketChannel ssc = ServerSocketChannel.open();
        // 2，服务端通道绑定端口。
        ssc.bind(new InetSocketAddress(ipAddress, port));
        ssc.configureBlocking(false);// 设置通道为非阻塞
        // 3，open一个多路复用选择器
        Selector selector = Selector.open();
        new Thread().start();// 开启一个线程用于维护选择器
        // 4，将服务端通道注册到选择器，监听接入操作
        ssc.register(selector, SelectionKey.OP_ACCEPT);
        // 5，选择器轮询它身上的keys
        Set<?> selectKeys = selector.selectedKeys();
        Iterator<?> it = selectKeys.iterator();
        while (it.hasNext()) {
            SelectionKey key = (SelectionKey) it.next();
            System.out.println(key);
            // deal with the I/O event.
        }
        // 6，服务端通道accept处理新客户端请求
        SocketChannel channel = ssc.accept();
        // 7，设置客户端通道属性
        channel.configureBlocking(false);
        channel.socket().setReuseAddress(true);
        // 8，注册客户端通道到选择器，监听读操作。
        channel.register(selector, SelectionKey.OP_READ);
        // 9，直接操作客户端通多进行异步读操作
        ByteBuffer bb = ByteBuffer.allocateDirect(1024);
        channel.read(bb);
        // 10，解码decode读取的信息
        // 11，异步写响应信息回客户端通道
        channel.write(bb);
    }

}
