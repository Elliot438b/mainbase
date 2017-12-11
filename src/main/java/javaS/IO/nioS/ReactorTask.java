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

/**
 * 基于TCP的NIO服务端多路复用类（多路是基于单线程实现的"多线程"）
 * 
 * 该类实现了Runnable接口，是一个独立的线程，负责轮询多路复用器Selector，可以处理多个客户端的并发接入。
 * 
 * @author Evsward
 *
 */
public class ReactorTask extends Base implements Runnable {
    private Selector selector;
    private ServerSocketChannel servChannel;
    private volatile boolean stop;

    /**
     * 初始化多路复用选择器，绑定监听端口。
     * 
     * @param port
     *            监听的端口
     */
    public ReactorTask(int port) {
        try {
            selector = Selector.open();// 通过静态方法open创建一个Selector实例。
            servChannel = ServerSocketChannel.open();// 通过静态方法open创建一个ServerSocketChannel实例，
            servChannel.configureBlocking(false);// 设置ServerSocketChannel通道为非阻塞。
            servChannel.socket().bind(new InetSocketAddress(ipAddress, port), 1024);// 通道绑定并监听IP和端口，允许接入最多1024个连接。
            servChannel.register(selector, SelectionKey.OP_ACCEPT);// 服务器通道注册到多路复用选择器上。
            logger.info("server is listening in port: " + port);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    // 向外部提供一个停止监听的方法。
    public void stop() {
        this.stop = true;
    }

    @Override
    public void run() {
        while (!stop) {
            try {
                selector.select(1000);// 设置休眠时间1s，每隔1s运行一次，也可以无参，当有就绪Channel时触发执行，从而实现网络的异步读写操作
                // 多路复用器轮询Keys
                Set<SelectionKey> selectedKeys = selector.selectedKeys();
                Iterator<SelectionKey> it = selectedKeys.iterator();
                SelectionKey key = null;
                while (it.hasNext()) {
                    key = it.next();
                    it.remove();// 处理key以后，移除该key
                    try {
                        handleInput(key);
                    } catch (IOException e) {
                        if (key != null) {
                            key.cancel();
                            if (key.channel() != null) {
                                key.channel().close();
                            }
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 通过SelectionKey来处理客户端请求以及响应。
     * 
     * @param key
     *            通道注册在选择器上的key
     */
    private void handleInput(SelectionKey key) throws IOException {
        if (key.isValid()) {
            // 处理新接入的请求消息
            if (key.isAcceptable()) {
                // accept 新连接（创建新通道相当于TCP三次握手，建立TCP物理链路，但并不创建新线程）
                ServerSocketChannel ssc = (ServerSocketChannel) key.channel();
                SocketChannel sc = ssc.accept();
                sc.configureBlocking(false);
                // 增加客户端通道到选择器，注意：服务端通道都是OP_ACCEPT操作位，客户端通道都是OP_READ操作位。
                sc.register(selector, SelectionKey.OP_READ);
            }
            if (key.isReadable()) {
                // 读取客户端的请求消息
                SocketChannel sc = (SocketChannel) key.channel();
                ByteBuffer readBuffer = ByteBuffer.allocate(1024);// 开辟一个1MB的缓冲区
                int readBytes = sc.read(readBuffer);// 此时read为非阻塞的，因为我们已经为该通道设置为非阻塞。
                if (readBytes > 0) {// 读到了字节，对字节进行编解码。
                    readBuffer.flip();// 将缓冲区当前limit设置为position，position设置为0，用于后续对缓冲区的读取操作。
                    byte[] bytes = new byte[readBuffer.remaining()];
                    readBuffer.get(bytes);
                    String body = new String(bytes, "UTF-8");// 使用UTF-8解码
                    logger.info("客户端请求信息：" + body);
                    // TODO: 处理请求，返回响应，这里直接采用回声的方式简单处理。
                    doWrite(sc, body);
                } else if (readBytes < 0) {// 返回值为-1，链路已关闭，需要手动关闭SocketChannel
                    key.cancel();
                    sc.close();
                } else {// 没有读到字节，多数情况，忽略。
                    ;
                }
            }
        }
    }

    /**
     * 将响应消息异步发送回客户端
     * 
     * @param channel
     *            客户端通道
     * @param response
     *            响应消息的内容
     * @throws IOException
     */
    private void doWrite(SocketChannel channel, String response) throws IOException {
        if (response != null && response.trim().length() > 0) {
            byte[] bytes = response.getBytes();
            ByteBuffer writeBuffer = ByteBuffer.allocate(bytes.length);
            writeBuffer.put(bytes);
            writeBuffer.flip();// flip操作？？？
            channel.write(writeBuffer);// 将缓冲区内容写入通道，发送出去
            /**
             * TODO 由于SocketChannel是异步非阻塞的，所以写消息发送时不会一下子全部发送完毕，所以会出现“写半包”的问题。
             * 我们需要注册写操作，不断轮询Selector，将没有发送完的ByteBuffer发送完毕。
             * 然后可以通过ByteBuffer的hasRemain方法判断消息是否完整发送完毕。
             */
        }
    }
}
