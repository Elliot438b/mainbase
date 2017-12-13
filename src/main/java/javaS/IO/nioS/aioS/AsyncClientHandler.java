package javaS.IO.nioS.aioS;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.CountDownLatch;

import javaS.IO.socketS.Base;

/**
 * 异步非阻塞客户端处理类
 * 
 * 本类大量使用匿名内部类来处理回调，好处是客户端处理类只有本类一个，并无生成如服务端处理类相关的那么多类文件，
 * 
 * 坏处是本类层次结构较复杂，可读性差。
 * 
 * 注意：Void作为类型要首字母大写，就好像只有Integer可以作为泛型类型而不是int一样，但Void不是引用类型，这里只是一种表示void类型的情况。
 * 
 * @author Evsward
 *
 */
public class AsyncClientHandler extends Base
    implements CompletionHandler<Void, AsyncClientHandler>, Runnable {
  private AsynchronousSocketChannel asyncClientChannel;// 客户端异步套接字通道，成员变量
  private CountDownLatch latch;// 用倒计时门闩来控制线程阻塞等待的状态，而不是让线程自己中断退出

  public AsyncClientHandler() {
    try {
      asyncClientChannel = AsynchronousSocketChannel.open();
    } catch (IOException e) {
      e.printStackTrace();
    }

  }

  @Override
  public void run() {
    latch = new CountDownLatch(1);
    // 对象是本类，回调函数也是本类
    asyncClientChannel.connect(new InetSocketAddress(ipAddress, port), this, this);
    try {
      latch.await();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    try {
      asyncClientChannel.close();// 客户端在请求完毕以后要关闭掉
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void completed(Void result, AsyncClientHandler attachment) {
    // 客户端输入
    BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
    boolean flag = true;
    String str = null;
    while (flag) {
      logger.info("客户端>输入信息：");
      try {
        str = input.readLine();
      } catch (IOException e1) {
        e1.printStackTrace();
      }
      if (str == null || "".equals(str)) {
        logger.info("请不要输入空字符！");
        continue;
      }
      /**
       * 不fail的情况下，只有客户端输入bye，才会主动断开连接。
       */
      if ("bye".equals(str)) {
        logger.info("客户端终止请求，断开连接。");
        try {
          asyncClientChannel.close();
          latch.countDown();// 客户端通道写异常，释放线程，执行完毕。
        } catch (IOException e) {
          e.printStackTrace();
        }
        break;
      }
      // 客户端发起请求
      byte[] req = str.getBytes();
      ByteBuffer reqBuffer = ByteBuffer.allocate(req.length);
      reqBuffer.put(req);
      reqBuffer.flip();
      // 通道读取到缓冲区成功以后开始写请求
      asyncClientChannel.write(reqBuffer, reqBuffer, new CompletionHandler<Integer, ByteBuffer>() {

        @Override
        public void completed(Integer result, ByteBuffer attachment) {
          if (reqBuffer.hasRemaining()) {
            asyncClientChannel.write(reqBuffer, reqBuffer, this);
          } else {
            // 写完请求以后，开始接收响应消息，并对进行结果回调处理
            ByteBuffer readBuffer = ByteBuffer.allocate(BUFFER_SIZE);
            asyncClientChannel.read(readBuffer, readBuffer,
                new CompletionHandler<Integer, ByteBuffer>() {

                  @Override
                  public void completed(Integer result, ByteBuffer attachment) {
                    attachment.flip();
                    byte[] bytes = new byte[attachment.remaining()];
                    attachment.get(bytes);// 将缓冲区读到字节数组
                    try {
                      String body = new String(bytes, "UTF-8");
                      logger.info("服务端的响应消息：" + body);
                      logger.info("---------------------");
                    } catch (UnsupportedEncodingException e) {
                      e.printStackTrace();
                    }
                  }

                  @Override
                  public void failed(Throwable exc, ByteBuffer attachment) {
                    try {
                      asyncClientChannel.close();
                      latch.countDown();// 客户端通道读异常，释放线程，执行完毕。
                    } catch (IOException e) {
                      e.printStackTrace();
                    }
                  }

                });
          }
        }

        @Override
        public void failed(Throwable exc, ByteBuffer attachment) {
          try {
            asyncClientChannel.close();
            latch.countDown();// 客户端通道写异常，释放线程，执行完毕。
          } catch (IOException e) {
            e.printStackTrace();
          }
        }

      });
    }
  }

  @Override
  public void failed(Throwable exc, AsyncClientHandler attachment) {
    try {
      asyncClientChannel.close();
      latch.countDown();// 客户端通道IO操作异常，释放线程，执行完毕。
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

}
