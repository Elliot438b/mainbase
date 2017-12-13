> 就像新IO为java带来的革新那样，让我们也开启一段新的程序人生。

> 关键字：NIO，BIO，伪IO，AIO，多路复用选择器，通道，缓冲区，jdk研究

## java.nio 概述
### 历史背景
在java nio出现之前，java网络IO是只有输入输出流操作的基于同步阻塞的Socket编程，这在实际应用中效率低下，因此当时高性能服务器开发领域一直被拥有更接近UNIX操作系统的Channel概念的C\+\+和C长期占据。我们知道现代操作系统的根源都是来自于UNIX系统，它代表了操作系统层面底层的平台支撑，在UNIX网络编程中，它的IO模型包括阻塞IO，非阻塞IO，IO复用，信号驱动IO以及异步IO，对于他们的具体解释这里不便展开，未来如果有机会介绍UNIX网络编程再来详叙。总之，平台底层是支持多种IO模型的，而当时的java只有阻塞IO这么一种，这也是编码最容易实现的一种，但却极大的限制了java在服务端的发展。java为了获得更高的性能表现，在jdk1.4版本增加了对异步IO模型的支持，提高了java的高性能IO处理能力，今天java已经逐渐取代C\+\+成为了企业服务端应用开发的首选语言。java新增的这部分类库就是java nio。
### jdk1.6的nio源码结构
java.nio.*包中引入了新的java io类库，旨在提高速度，实际上旧的IO已经使用nio重新实现过，以便充分利用这种速度的提高，因此，即使我们不显式地用nio编写代码，也能获得受益效果。速度的提高在文件IO和网络IO中都有可能发生，概述部分结束以后我们会分别介绍。首先我们来看jdk 1.6的源码，分析一下java.nio相关的结构。

    java.nio 
    java.nio.channels 
    ~~java.nio.channels.spi ~~
    java.nio.charset 
    ~~java.nio.charset.spi ~~
    
### spi
那两个spi结尾的都是相关的提供者服务类，可以参考[前面讲解提供者模式的文章](http://www.cnblogs.com/Evsward/p/localizer.html#提供者模式)，简单来说就是，java.nio.channels.spi包是用于服务/决定/改变java.nio.channels对象的，同样的，java.nio.charset.spi包是用于服务/决定/改变java.nio.charset对象的。我们在官方文档上也能够看到，这两个spi包都是
> java.nio.channels.spi: 只有那些定义新的选择器提供者的开发人员才应直接使用此包。

> java.nio.charset.spi: 只有定义新charset的开发人员才需要直接使用此包。

我们没有开发新的charset和selector的需求，因此spi包可以忽略掉。

### nio包
#### ①缓冲区
nio包定义了作为数据容器的缓冲区，以及作为其他nio包的父类。主要是java.nio.Buffer类，以及它的子类。再加上一个java.nio.ByteOrder，它是一个字节顺序的类型安全枚举。（TODO:接下来会有专门介绍枚举的文章）

Buffer是一个对象，它包含一些写入或要读出的数据，这与旧IO最大的不同在于旧IO将数据直接读写到流对象中，少了这一个中间层。
> 在NIO中所有数据读写都用Buffer处理。

缓冲区实质上是一个数组，通常它是一个字节数组——ByteBuffer，也可以是其他数据类型的数组。同时缓冲区还提供了对数据结构化访问以及维护读写位置等信息。对于这些不同数据类型的Buffer类，他们都有相同的操作，只有ByteBuffer提供了更多的一些直接操作通道的方法。
> 这些Buffer类（Buffer类以及它的子类）定义了一个用于通过IO发送特定基本类型具体数据的有序线性表结构的容器，除了boolean，覆盖了：byte, short, int, long, float, double 和 char。

特别的是，ByteBuffer类存在一个单独的子类：MappedByteBuffer类。此类用特定于内存映射文件区域的操作扩展了ByteBuffer类，可用于内存映射文件的I/O 操作。

### channels包
该包定义了各种通道，这些通道表示到能够执行I/O操作的实体（如文件和套接字）的连接；定义了用于多路复用的、非阻塞I/O操作的选择器。

#### ①通道
> NIO所有网络IO都是通过Channel。举例来讲，如果通道是一条传送带，那上面的不同形状的箱子就是缓冲区Buffer，而箱子内部装有的才是真正的数据。

基本上，所有的NIO都是从一个Channel开始，数据可以从缓冲区传输到通道，也可以从通道传输到缓冲区。

> 全双工：通道与流的不同之处在于流是定向的，半双工的，InputStream只能是输入流，只可以读取数据；OutputStream是输出流，只能写入数据。没有一种既能写又能读的，但是通道可以，通道是双向的。Channels的设计更接近底层操作系统，因为操作系统的通道就是全双工的。

下面是包中的通道相关类：
- Channels：针对信道和流的实用工具方法。
- DatagramChannel：基于UDP数据报的网络IO通道
- FileChannel：读取、写入、映射和操作文件IO通道
- FileChannel.MapMode：文件映射模式的类型安全的枚举。
- FileLock：表示文件区域锁定的标记。
- Pipe：实现单向管道传送的通道对，一个可写入的sink通道和一个可读取的source通道。
- Pipe.SinkChannel：一个可写入的sink通道。
- Pipe.SourceChannel：一个可读取的source通道。
- ServerSocketChannel：类似于ServerSocket的基于TCP流的网络IO编程的服务端通道
- SocketChannel：类似于Socket的基于TCP流的网络IO编程的客户端通道

通道总体上来讲，可以分为两大类：
- 网络IO：SelectableChannel
- 文件IO：FileChannel

根据上一篇[介绍TCP和UDP的socket编程](http://www.cnblogs.com/Evsward/p/socket.html)，我们可以看出这些通道有着自己的一套基于TCP和UDP的Socket编程。其实Socket虽然没有在IO包中，但它是网络IO，也属于IO总范畴，通道中还有一个在[IO中介绍过的用于文件IO](http://www.cnblogs.com/Evsward/p/io.html#一file-文件流-randomaccessfile)的通道FileChannel。所以，以上内容除了管道以外，我们在之前的IO或Socket中都有过研究，可以作为我们在nio包中继续研究他们的基础。

至于管道，这是我们[在IO中留的坑](http://www.cnblogs.com/Evsward/p/io.html#java-io基础的总结)，本篇文章来填。
> 我们之前说管道处理的是进程间的通信，实际上网络编程中客户端和服务端也是通过端口进行端到端通信，也属于两个进程间的通信。

#### ②选择器
选择器Selector是NIO编程的基础，它是多路复用的、支持非阻塞IO操作的。

> 多路复用器提供选择已就绪任务的能力。

- Selector会不断地监听（通过轮询的手段）注册在其上的Channel，如果某个Channel上面发生事件（比如：连接打开，数据到达），这个Channel就处于就绪状态，会被Selector轮询出来，然后通过SelectionKey可获取就绪Channel的集合，进行后续IO操作。一个Selector可以同时轮询多个Channel，只需要一个线程负责Selector的轮询，就可以接入成千上万的客户端。

下面是包中的选择器相关类：

- SelectableChannel：可通过Selector实现多路复用的通道。
- SelectionKey：表示SelectableChannel在Selector中的注册的标记。
- Selector：SelectableChannel对象的多路复用器。

下面我们来分别认识一下多路复用和非阻塞IO：
- 多路复用：在IO编程过程中，当需要同时处理多个客户端接入请求时，可以利用多线程或者IO多路复用技术进行处理。简单来说多路复用技术是与多线程同级的，解决差不多问题的技术，它通过把多个IO的阻塞复用到同一个select的阻塞上，使得系统在单线程的情况下可同时处理多个客户端请求。与传统的多线程/多进程相比，IO多路复用的优势是系统开销小，不需要创建和维护新的线程。
- 非阻塞IO：nio之前，io操作都是同步阻塞IO（BIO），上面也讲过了，存在性能和可靠性的瓶颈。
    - BIO最主要的问题在于每当有一个新客户端请求接入时，服务端必须创建一个新的线程处理新接入的客户端链路，线程与客户端连接是一对一的关系，当有线程处于阻塞状态，cpu就要频繁使用上下文切换去更换到有效线程，然而上下文切换是一个比较消耗资源的操作，它要将当前线程的所有的上下文环境基础复制到另外一个有效的线程上去继续执行新的IO请求，而当多个线程阻塞时，cpu会无意义的在多个线程直接做上下文切换，在高性能服务器应用领域，往往要面向成千上万个客户端的并发连接，这种模型显然无法满足高性能、高并发接入的场景。
    - 伪异步IO，我们[在IO中介绍的线程池](http://www.cnblogs.com/Evsward/p/socket.html#高级线程池)虽然限制了线程的最大连接数，但是底层仍旧是基于BIO实现的，所以被称为伪异步IO。
    - BIO源码的阻塞，我们知道ServerSocket的accept方法会阻塞一个线程直到新的客户端连入，除此之外，流操作中的InputStream的read方法和OutputStream的write方法在读取操作过程中都会引发同步阻塞问题，阻塞的时间取决于对方IO线程的处理速度和网络IO的处理速度，本质上讲，这些对方的环境问题我们并没办法去保证。
    - 非阻塞IO（Non-block IO），它的缩写也为NIO，这与New IO其实并无区别，因为New IO就是增加的对Non-block IO的支持，因此我们说NIO可以是New IO，也可以是Non-block IO，不要太在意这方面的困惑。NIO通过对通道的阻塞行为的配置，可以实现非阻塞的通道，使得线程在等待时（旧IO中的阻塞）可以同时做其他事情，实现了线程的异步操作。
        - NIO相对于BIO来讲，虽然提供了高性能，高并发的支持，但是它也带来了较高的编程复杂度。
        - 如果是低负载、低并发的应用程序大可使用BIO，而不要一味赶NIO的时髦。
        - 由一个专门的线程来负责处理分发所有的IO事件。
        - 把整个IO任务切换成多个小任务，通过任务间协作完成。
        - 事务驱动：只有IO事件到的时候才去处理，而不是时刻保持监视事件。
        - 线程通讯：通过notify和wait等方式通信，保证每次的上下文切换是有意义的，减少无意义的切换。
        - 通过增加Pipe、Channel、Buffer和Selector等很多UNIX网络编程的概念，NIO实现了非阻塞IO，所以这是一个递归，在这里引用回了我们本篇博文的主题——NIO。

nio概述部分就算结束了，继续多说就太浮于表面了，下面针对NIO的几个主要特性进行代码实例的演示。

## 基于TCP的NIO实例
下面我们先分别介绍一下基于TCP的NIO的服务端和客户端的编写流程。
### 基于TCP的NIO服务端的编写流程
直接通过代码来展示：
```
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
```
### 基于TCP的NIO客户端的编写流程
同样直接通过代码来展示：

```
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
```
### 重新构建基于TCP的NIO实例
上面给出了大致的基于TCP的NIO编程的流程，大家不必过于研究他们，下面我们构建实例来说明，会得到更好的学习效果。本打算在之前写过的[java TCP socket实例](http://www.cnblogs.com/Evsward/p/socket.html#%E5%9F%BA%E4%BA%8Etcp%E7%9A%84%E6%9C%8D%E5%8A%A1%E7%AB%AF%E5%AE%A2%E6%88%B7%E7%AB%AF%E6%A8%A1%E5%9E%8B)的基础上进行改造，然而由于客户端等待标准输入就是一个阻塞的过程，这与异步非阻塞的NIO是相悖的，我确实做过尝试，发现一旦客户端阻塞在等待用户输入的位置，整个线程都被迫等待，那么依赖于线程的Selector就不能更好的发挥作用，服务端的响应消息也就永远无法给出。

换句话说，服务端的响应是无法像socket编程那样写在客户端发送消息以后，同步接收服务端数据，然后再阻塞等待用户新的输入，NIO是通过对key的轮询，这是异步的行为，也就是说客户端发送请求消息与服务端返回响应消息是完全解耦的，不存在同步的顺序执行的关系，所以当我们的线程被客户端的标准输入阻塞，key的轮询异步操作也就完全run不起来了。

因此，我们采用去掉客户端阻塞代码的方式，重新构建基于TCP的NIO实例：指定客户端请求消息，服务端接收以后返回当前时间作为响应。下面看代码：

我们在Base里面定义了一些常量。

```
public class Base {
  protected final static Logger logger = LogManager.getLogger();
  protected static String ipAddress = "127.0.0.1";
  protected static int port = 23451;
  protected static String TIMEQUERY = "query time";
  protected final static int BUFFER_SIZE = 1024;
}
```
客户端和服务端的入口都非常简单。

```
public class NIOTCPClient extends Base {
  public static void main(String[] args) {
    new Thread(new ReactorClientHandler(), "nio-client-reactor-001").start();
  }
}
```

```
public class NIOTCPServer extends Base {
  public static void main(String[] args) {
    new Thread(new ReactorServerHandler(), "nio-server-reactor-001").start();
  }
}

```
下面分别介绍ReactorClientHandler和ReactorServerHandler。

```
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

```

```
package javaS.IO.nioS;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Date;
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
public class ReactorServerHandler extends Base implements Runnable {
  private Selector selector;
  private ServerSocketChannel servChannel;// 定义一个服务端通道
  private volatile boolean stop;

  /**
   * 初始化多路复用选择器，绑定监听端口。
   * 
   * @param port
   *          监听的端口
   */
  public ReactorServerHandler() {
    try {
      // 初始化对象
      selector = Selector.open();// 通过静态方法open创建一个Selector实例。
      servChannel = ServerSocketChannel.open();// 通过静态方法open创建一个ServerSocketChannel实例，
      servChannel.configureBlocking(false);// 设置ServerSocketChannel通道为非阻塞。
      // 开始事务操作
      servChannel.socket().bind(new InetSocketAddress(ipAddress, port), BUFFER_SIZE);// 通道绑定并监听IP和端口，允许接入最多1024个连接。
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
   *          通道注册在选择器上的key
   */
  private void handleInput(SelectionKey key) throws IOException {
    if (key.isValid()) {
      // 处理新接入的请求消息
      if (key.isAcceptable()) {
        // 用一个服务端通道来接收key的通道
        ServerSocketChannel ssc = (ServerSocketChannel) key.channel();
        // accept 新连接（创建新通道相当于TCP三次握手，建立TCP物理链路，但并不创建新线程）
        SocketChannel sc = ssc.accept();
        sc.configureBlocking(false);
        // 增加客户端通道到选择器，注意：服务端通道都是OP_ACCEPT操作位，客户端通道都是OP_READ操作位。
        sc.register(selector, SelectionKey.OP_READ);
      }
      if (key.isReadable()) {
        // 读取客户端的请求消息
        SocketChannel sc = (SocketChannel) key.channel();
        ByteBuffer readBuffer = ByteBuffer.allocate(BUFFER_SIZE);// 开辟一个1MB的缓冲区
        int readBytes = sc.read(readBuffer);// 此时read为非阻塞的，因为我们已经为该通道设置为非阻塞。
        if (readBytes > 0) {// 读到了字节，对字节进行编解码。
          readBuffer.flip();// 将缓冲区当前limit设置为position，position设置为0，用于后续对缓冲区的读取操作。
          byte[] bytes = new byte[readBuffer.remaining()];
          readBuffer.get(bytes);
          String body = new String(bytes, "UTF-8");// 使用UTF-8解码
          logger.info("客户端请求信息：" + body);
          // TODO: 简单处理请求，直接返回当前时间作为响应消息。
          doWrite(sc, new Date().toString());
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
   *          客户端通道
   * @param response
   *          响应消息的内容
   * @throws IOException
   */
  private void doWrite(SocketChannel channel, String response) throws IOException {
    if (response != null && response.trim().length() > 0) {
      byte[] bytes = response.getBytes();
      ByteBuffer writeBuffer = ByteBuffer.allocate(bytes.length);
      writeBuffer.put(bytes);
      writeBuffer.flip();
      channel.write(writeBuffer);// 将缓冲区内容写入通道，发送出去
      /**
       * TODO 由于SocketChannel是异步非阻塞的，所以写消息发送时不会一下子全部发送完毕，所以会出现“写半包”的问题。
       * 我们需要注册写操作，不断轮询Selector，将没有发送完的ByteBuffer发送完毕。 然后可以通过ByteBuffer的hasRemain方法判断消息是否完整发送完毕。
       */
      if (!writeBuffer.hasRemaining()) {
        logger.info("服务端响应发送成功！");
      }
    }
  }
}

```
### ByteBuffer的flip操作解析
上面代码中总会出现关于ByteBuffer对象调用的flip方法，它的解释是
> 将缓冲区当前limit设置为position，position设置为0，用于后续对缓冲区的读取操作。

然而仍是一头雾水，下面从ByteBuffer的属性来解析这个flip方法的含义。首先来看ByteBuffer的几个属性：
- capacity：定义了ByteBuffer对象的容量。
- limit：定义了ByteBuffer在读写操作中的界限，读操作中limit代表有效数据的长度（肯定是小于等于capacity），写操作中等于capacity。
- position：读写操作的当前下标。
- mark：一个临时存放的下标，用来在ByteBuffer对象的中间进行读写操作。

以上属性可以通过以下方法进行设定：
- clear()：把position设为0，把limit设为capacity，一般在把数据写入Buffer前调用。
- flip()：把limit设为当前position，把position设为0，一般在从Buffer读出数据前调用。意思是将当前position即有效数据长度赋值给limit，然后将当前position调整到0，从0开始读取，一直读到limit。
- rewind()：把position设为0，limit不变，一般在把数据重写入Buffer前调用。

### 总结
以上关于该实例的所有代码都已经完整给出，至于内部的具体执行方式，我直接在代码行间做了充足的注释说明，我想比在这里用文字长篇累牍的效果要好得多。不过下面我还是要对该实例展现出的NIO特性以及该实例的局限性进行一个分析总结。
- 首先是该实例展现的NIO特性，无论服务端还是客户端，同一时间只需要唯一一个线程启动，由它维持着多路复用器的轮询工作，而实际上原来的多线程工作都转交给了这个多路复用器，通过多路复用器将通道上的每个IO操作注册进来，然后多路复用器有个休眠时间，selector.select(1000);每隔1s就会轮询一遍。实际上，整个selector的轮询工作本身是对当前线程的阻塞，但这是线程层面的阻塞，是为了保持多路复用器的轮询工作得以持续开展，而涉及到具体业务io操作的工作不会被阻塞，原Socket的工作方式是将这些业务操作都做了阻塞同步操作，而NIO将线程与多路复用器做了分层，在多路复用器层面，我们达到了对业务IO操作的异步非阻塞的目标。
- 接下来是分析该实例的局限性，就本例而言，最主要的问题还是异步请求的结果问题，正如上面我们分析过的，无法用该实例实现标准输入返回回声的基本业务需求，由于无法外部更新客户端的请求，客户端无法保持与服务端的连接，只能是发送请求以后则关闭，否则该通道就会完全阻塞废弃在那里。该实例只是为了展示NIO的使用方式，总结下来来看，相较于原Socket编程，即使是“简配版本”的NIO操作，也称不上方便。

## 新NIO
我们都知道NIO本就是新IO了，怎么又蹦出来一个新NIO。其实是这样的，上面的实例中我们也体会到了NIO对异步操作处理的力不从心，所以针对以上的问题，JDK1.7升级了NIO的类库，我们可以叫它为新NIO，也可以是NIO 2.0，java正式提供了对异步IO的支持，解决了我们上面实例中提到的关于异步结果无法读取的问题，客户端与服务端的通道可以随时不断地发送请求和返回响应，实现真正的客户端和服务端的长连接通道。对于新NIO中这部分支持异步通信的，我们称他们为AIO（Asynchronous IO)。

> AIO主要是通过回调函数解决了对异步操作结果的处理。该回调函数是通过实现CompletionHandler接口的completed方法。

### jdk1.7的nio源码结构
下面我们来看一下jdk1.7的nio源码架构:

    java.nio
    java.nio.channels
    java.nio.channels.spi
    java.nio.charset
    java.nio.charset.spi
    java.nio.file
    java.nio.file.attribute
    java.nio.file.spi

跟上面介绍过的jdk1.6版本的相同，spi包的可以去掉。那么增加的是java.nio.file类，于此同时，channels包中也发生了变化，

    *AsynchronousChannelGroup
    *AsynchronousFileChannel
    *AsynchronousServerSocketChannel：
    *AsynchronousSocketChannel
    Channels
    DatagramChannel
    FileChannel
    FileChannel.MapMode
    FileLock
    *MembershipKey
    Pipe
    Pipe.SinkChannel
    Pipe.SourceChannel
    SelectableChannel
    SelectionKey
    Selector
    ServerSocketChannel
    SocketChannel

以上开头带*的类是我标示出来的jdk7新增加的类。其中MembershipKey是代表互联网协议中多路广播组的一员，我们暂且不管它，重点研究Asynchrounous开头的异步支持类。先看他们的定义，

- AsynchronousChannelGroup：对异步通道进行分组，达到资源共享的目的。
- AsynchronousFileChannel：一个可以对文件读写操作的异步通道。
- AsynchronousServerSocketChannel：一个异步通道用作流导向的监听套接字，说白了就是服务端Socket通道。
- AsynchronousSocketChannel：一个异步通道用作流导向的连接套接字，就是客户端Socket通道。

> 这里我想对新nio中Client-Server架构进行一下理解。

其实在NIO中，因为全双工的缘故，服务端客户端的定义界限没有原始Socket那么严格，服务端在NIO中的体现在它是监听，而客户端是连接，通过这条通道，他们都可以给对方发送消息，我们通常称服务端发送给客户端的消息为响应，而客户端发送给服务端的消息为请求。

接着说回我们的新NIO，除了新增的AIO部分，其他内容都是微调整，下面我们主要针对AIO部分进行代码实例的学习。

### AIO编程
AIO编程中最大的不同就是取消了多路复用器，它不再使用多路复用器的“多线程”的实现方式，而是完全通过对一条线程的非阻塞高效使用来实现多任务并发，这就归功于它对操作结果的异步处理。
> 因为异步操作的回调函数本身就是一个额外的jvm底层的线程池启动的新线程负责回调并驱动读写操作返回结果，当结果处理完毕，它也就自动销毁了。

所以没有了多路复用器，又增加了真正异步的实现，AIO无论从编码上还是功能上都比旧的NIO要好很多。









