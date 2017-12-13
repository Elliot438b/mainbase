package javaS.IO.nioS;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

import org.junit.Test;

import javaS.IO.socketS.Base;

public class NIOTCPClient extends Base {
    public static void main(String[] args) {
        new Thread(new ReactorClientHandler(), "nio-client-reactor-001").start();
    }

    @Test
    /**
     * 列出大致的TCPNIO客户端的执行顺序
     * 
     * @throws IOException
     */
    public void seqClient() throws IOException {
        // 1,open创建一个客户端通道实例
        SocketChannel sc = SocketChannel.open();
        // 2,设置通道关于TCP的属性
        sc.configureBlocking(false);
        sc.socket().setReuseAddress(true);// 关闭TCP连接时，该连接可能在关闭后的一段时间内保持超时状态
        sc.socket().setSendBufferSize(1024);// 将此Socket的SO_SNDBUF选项设置为指定的值。
        sc.socket().setReceiveBufferSize(1024);// 将此Socket的SO_RCVBUF选项设置为指定的值。
        // 3,open创建一个选择器的实例
        Selector selector = Selector.open();
        // 4,客户端连接服务端（远程主机端口）
        boolean connected = sc.connect(new InetSocketAddress(ipAddress, port));
        if (connected) {
            // 5, 判断连接如果成功，则注册到选择器的读操作位
            sc.register(selector, SelectionKey.OP_READ);
        } else {
            // 6,如果连接不成功，则注册到选择器的连接操作位，监听服务端的TCP的ACK应答
            sc.register(selector, SelectionKey.OP_CONNECT);
        }
        // 7,轮询选择器的keys
        Set<SelectionKey> set = selector.selectedKeys();
        Iterator<SelectionKey> it = set.iterator();
        SelectionKey key = null;
        while (it.hasNext()) {
            key = it.next();
            // 处理io event
            // 8，处理连接
            if (key.isConnectable()) {
                sc.register(selector, SelectionKey.OP_READ);// 连接成功以后就将读操作注册到多路复用选择器上
            } else if (key.isReadable()) {
                // 9，处理数据
                ByteBuffer bb = ByteBuffer.allocate(1024);// 分配1MB缓冲区。
                SocketChannel socketChannel = (SocketChannel) key.channel();
                int readBytes = socketChannel.read(bb);
                if (readBytes > 0) {
                    // 10，解码
                }
                // 11, 将客户端请求写回通道。
                socketChannel.write(bb);
            }
        }
    }
}
