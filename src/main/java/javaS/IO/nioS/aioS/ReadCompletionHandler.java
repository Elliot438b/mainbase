package javaS.IO.nioS.aioS;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.Date;

import javaS.IO.socketS.Base;

public class ReadCompletionHandler extends Base implements CompletionHandler<Integer, ByteBuffer> {
  private AsynchronousSocketChannel channel;

  /**
   * 通过构造器传入关联的AsynchronousSocketChannel实例作为成员变量，我们在回调要始终针对这个通道进行IO操作。
   * 
   * 其实从
   * 
   * @param channel
   */
  public ReadCompletionHandler(AsynchronousSocketChannel channel) {
    this.channel = channel;
  }

  @Override
  /**
   * attachment什么时候被赋的值？
   * 
   **** 答：回调的时候数据被填充到了attachment，返回结果是一个状态码存储与Integer result对象中。
   */
  public void completed(Integer result, ByteBuffer attachment) {
    attachment.flip();// 为读取数据做准备
    byte[] body = new byte[attachment.remaining()];// 创建一个与附件缓冲区大小相同的字节数组。
    attachment.get(body);// 将缓冲区内数据读到字节数组中去。
    try {
      String req = new String(body, "UTF-8");
      logger.info("客户端请求信息：" + req);
      doWrite(new Date().toString());// 将当前时间作为响应消息返回客户端
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    }
  }

  /**
   * 服务端写响应信息
   * 
   * @param rep
   */
  private void doWrite(String rep) {
    if (rep != null && rep.trim().length() > 0) {
      byte[] bytes = rep.getBytes();
      ByteBuffer writeBuffer = ByteBuffer.allocate(bytes.length);
      writeBuffer.put(bytes);
      writeBuffer.flip();
      /**
       * 开始向服务端异步套接字通道中写入数据，同样的任何出现异步IO操作的都要有CompletionHandler的实现来来做回调的处理。
       * 直接采用匿名内部类实现CompletionHandler接口
       */
      channel.write(writeBuffer, writeBuffer, new CompletionHandler<Integer, ByteBuffer>() {

        @Override
        public void completed(Integer result, ByteBuffer attachment) {
          if (attachment.hasRemaining()) {
            channel.write(writeBuffer, attachment, this);// 递归调用自己直到数据全部写入通道。
            /**
             * 这里的数据全部写入通道的控制与“写半包”并不相同，写半包是由于传输容器本身的大小限制正好对数据进行了分割导致，
             * 
             * 处理起来会更加复杂一些，这部分研究现在并不准备展开讨论。
             */
          }
        }

        @Override
        public void failed(Throwable exc, ByteBuffer attachment) {// 对异常的处理
          try {
            channel.close();
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
      // 失败则关闭当前通道
      this.channel.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

}
