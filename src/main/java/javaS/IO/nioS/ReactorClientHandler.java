package javaS.IO.nioS;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

import javaS.IO.socketS.Base;

public class ReactorClientHandler extends Base implements Runnable {
  private Selector selector;
  private SocketChannel sc;// 定义一个客户端通道
  private volatile boolean stop;

  /**
   * 构造期间初始化对象，客户端构造函数不做事务操作
   * 
   * @param ip
   * @param port
   */
  public ReactorClientHandler() {
    try {
      selector = Selector.open();
      sc = SocketChannel.open();
      // 设置客户端通道属性以及TCP参数
      sc.configureBlocking(false);
      sc.socket().setSendBufferSize(BUFFER_SIZE);
      sc.socket().setReceiveBufferSize(BUFFER_SIZE);
    } catch (IOException e) {
      e.printStackTrace();
      System.exit(1);
    }

  }

  @Override
  public void run() {
    try {
      // TODO: 连接成功，未考虑重连操作
      doConnect();
    } catch (IOException e) {
      e.printStackTrace();
    }
    while (!stop) {
      try {
        selector.select(1000);
        Set<SelectionKey> set = selector.selectedKeys();
        Iterator<SelectionKey> it = set.iterator();
        SelectionKey key = null;
        // 异步非阻塞单线程轮询多路复用器选择器keys
        while (it.hasNext()) {
          key = it.next();
          it.remove();// 处理一个Key就移除一个，然而在handleInput中还会注册进来读操作
          try {
            handleInput(key);
          } catch (Exception e) {
            if (key != null) {
              // 关闭key
              key.cancel();
              // 关闭在handleInput方法中打开的key的通道
              if (key.channel() != null) {
                key.channel().close();
              }
            }
          }
        }
      } catch (IOException e) {
        e.printStackTrace();
        System.exit(1);
      }
    }

    /**
     * 多路复用器关闭后，所有注册在上面的Channel和Pipe等资源都会被自动去关闭，不需要重复释放资源。
     */
    if (selector != null) {
      try {
        selector.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * 通过轮询key来处理通道的数据（包括发送请求和接收响应）
   * 
   * @param key
   * @throws IOException
   */
  private void handleInput(SelectionKey key) throws IOException {
    if (key.isValid()) {// 首先判断key是否有效
      SocketChannel sc = (SocketChannel) key.channel();// 用一个客户端通道来接收key的通道
      if (key.isConnectable()) {// 判断是否是连接状态，说明服务端已返回ACK应答消息，但是连接还没有最终建立
        if (sc.finishConnect()) {// 判断是否连接成功，建立成功
          // 注册该客户端通道到多路复用器上，注册读操作位，监听网络读操作
          sc.register(selector, SelectionKey.OP_READ);
          // 发送请求消息给服务端。
          sendReq(sc, TIMEQUERY);
        } else {// 连接失败
          System.exit(1);
        }
      } // 格外注意这里的判断终止符,key的状态如果是Read就不是Connect了，要分开判断。
      if (key.isReadable()) {
        // 读取服务器的应答响应消息，如果客户端接收到了服务端的响应消息，则SocketChannel是可读的。
        ByteBuffer readBuffer = ByteBuffer.allocate(BUFFER_SIZE);// 无法事先判断码流大小，开辟一个1MB的缓冲区
        int readBytes = sc.read(readBuffer);// 此时read为异步非阻塞的，因为我们已经为该通道设置为非阻塞。
        /**
         * 只要涉及异步读，就要判断读取的结果
         */
        if (readBytes > 0) {// 读到了字节，对字节进行编解码。
          readBuffer.flip();// 将缓冲区当前limit设置为position，position设置为0，用于后续对缓冲区的读取操作。
          byte[] bytes = new byte[readBuffer.remaining()];
          readBuffer.get(bytes);
          String body = new String(bytes, "UTF-8");// 使用UTF-8解码
          logger.info("服务端的响应消息：" + body);
          this.stop = true;// 得到服务端响应则断开连接线程（线程断了，jvm上面所有的通道，key等资源都自动没了，所以不必去重复释放资源）。
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
   * 客户端发起连接的操作
   * 
   * @throws IOException
   */
  private void doConnect() throws IOException {
    if (sc.connect(new InetSocketAddress(ipAddress, port))) {
      // 如果已连接，则注册客户端通道的读操作到选择器
      sc.register(selector, SelectionKey.OP_READ);
    } else {
      /**
       * 如果未连接，不代表连接失败，可能还在等待服务端返回TCP握手应答消息， 所以此时注册客户端通道的连接操作到选择器，等候轮询执行连接操作, 当服务端返回TCP
       * syn-ack消息后，Selector就能够轮询到这个SocketChannel处于连接就绪状态
       */
      sc.register(selector, SelectionKey.OP_CONNECT);
    }
  }

  /**
   * 客户端通道发送消息
   * 
   * @param sc
   *          客户端通道
   * @param strReq
   *          待发送消息
   * @throws IOException
   */
  private void sendReq(SocketChannel sc, String strReq) throws IOException {
    byte strBytes[] = strReq.getBytes();
    // 构造请求消息体ByteBuffer，只有通过ByteBuffer才能操作通道发送消息
    ByteBuffer reqBuffer = ByteBuffer.allocate(strBytes.length);// 由于已知码流大小为strBytes.length，所以建立一个一样大的缓冲区
    reqBuffer.put(strBytes);// 将请求的数据放入发送缓冲区（以字节数组的形式）
    reqBuffer.flip();
    sc.write(reqBuffer);
    /**
     * 由于发送请求是异步的，不会一次性全部发送成功，会存在“写半包”的问题， 所以要通过hasRemaining方法对发送结果进行判断，如果缓冲区中消息全部发送完成，则打印发送成字样提示用户。
     */
    if (!reqBuffer.hasRemaining()) {
      logger.info("客户端请求发送成功！");
    }
  }
}
