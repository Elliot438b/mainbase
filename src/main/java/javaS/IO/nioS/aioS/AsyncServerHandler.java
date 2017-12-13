package javaS.IO.nioS.aioS;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.util.concurrent.CountDownLatch;

import javaS.IO.socketS.Base;

/**
 * 异步非阻塞服务器处理类
 * 
 * @author Evsward
 *
 */
public class AsyncServerHandler extends Base implements Runnable {

  AsynchronousServerSocketChannel asyncServerChannel;// 服务端异步套接字通道
  CountDownLatch latch;// 倒计时门闩

  /**
   * 构造器对象初始化
   */
  public AsyncServerHandler() {
    try {
      // 与NIO相同的操作，通过open静态方法创建一个AsynchronousServerSocketChannel的实例。
      asyncServerChannel = AsynchronousServerSocketChannel.open();
      asyncServerChannel.bind(new InetSocketAddress(ipAddress, port), 1024);// 一样的操作，绑定IP端口。
      logger.info("server is listening in address -> " + ipAddress + ":" + port);
    } catch (IOException e) {
      e.printStackTrace();
      System.exit(1);
    }
  }

  @Override
  public void run() {
    latch = new CountDownLatch(1);// 初始化倒计时次数为1
    doAccept();
    try {
      latch.await();// 倒计时门闩开始阻塞，知道倒计时为0，如果在这期间线程中断，则抛异常：InterruptedException。
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  public void doAccept() {
    /**
     * 开始接收客户端连接，将当前服务端异步套接字通道对象作为附件传入保存，
     * 
     * 同时传入一个 CompletionHandler<AsynchronousSocketChannel, ? super A>的实现类对象接收accept成功的消息。
     */
    asyncServerChannel.accept(this, new AcceptCompletionHandler());
  }

}
