package javaS.IO.nioS.aioS;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

import javaS.IO.socketS.Base;

/**
 * accept方法的回调操作
 * 
 * 泛型参数1：IO操作的回调结果的类型 泛型参数2：IO操作的附件对象的类型
 * 
 * 这两个参数对应的也就是回调函数completed的参数类型
 * 
 * @author Evsward
 *
 */
public class AcceptCompletionHandler extends Base
    implements CompletionHandler<AsynchronousSocketChannel, AsyncServerHandler> {

  @Override
  /**
   * 传入一个客户端异步套接字通道作为accept操作结果的接收者，一个异步非阻塞服务器处理类对象作为附件存储
   */
  public void completed(AsynchronousSocketChannel result, AsyncServerHandler attachment) {
    /**
     * 为什么要再次执行相同的accept方法，他们甚至参数都一样？
     * 
     * 因为上一个类中asyncServerChannel的accept方法执行以后，新的客户端连接结果会调用当前completed方法。
     * 但是服务端是支持多个客户端连接的，不能只有一个客户端连接成功以后，调用回调函数completed就结束了。
     * 因此我们要在第一个客户端连接结果的回调函数中再次开启一个accept方法以接收第二个客户端连接，递归调用，就可以支持accept无数个客户端连接了。
     */
    attachment.asyncServerChannel.accept(attachment, this);
    // 开辟一个1MB的临时缓冲区，将用于从异步套接字通道中读取数据包
    ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);
    /**
     * 回调函数对accept结果进行异步读操作，读取客户端请求，放入buffer容器中
     * 
     * 其中attachment依然作为其回调时的入参：读数据的时候，是通过ByteBuffer容器，无论是数据源还是结果存放，因此attachment也应该传入一个ByteBuffer对象
     * 
     * 最后一个参数为异步读操作的回调函数。
     */
    result.read(buffer, buffer, new ReadCompletionHandler(result));
  }

  @Override
  public void failed(Throwable exc, AsyncServerHandler attachment) {
    exc.printStackTrace();
    attachment.latch.countDown();// 倒计时一次，由于我们定义的初始化次数为1，所以当前线程直接往下运行，脱离阻塞状态。
  }

}
