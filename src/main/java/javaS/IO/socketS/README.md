> 关键字：互联网协议，网络分层，socket，TCP/IP协议，jdk源码，多线程，线程池，ExecutorService

本文的主要目的是面向程序员，所以涉及到程序编码上面比较多的是TCP/IP协议以及Socket协议，这里会重点介绍，而在学习这两个协议之前，对于整个互联网的运行原理要了解熟悉，所以会先快速过一遍互联网协议。

### 互联网协议（Internet Protocol Suite）
首先，根据各种信息的总结，这里我们将互联网协议分为五层，自上而下分别是应用层，传输层，网络层，链接层，实体层。下面我们倒过来自下而上的分析他们。

#### ①实体层
传输的是基于二进制数据的电信号，主要包括光缆、电缆、双绞线，WIFI无线电波等物理连接手段。
#### ②链接层
传输的是基于以太网协议的数据包“帧”，分为头部和数据部分，头部包含的是用于识别接收机的MAC地址，数据部分则包含的是传输内容。通过MAC地址广播到网络中其他主机，是子网络中的主机与主机的通信。

header1 | data
---|---

#### ③网络层
网络层是真正意义上不限边界的整个网络中的主机与主机的通信，传输的是基于IP协议的数据包，它是对帧的进一步细化，在以太网数据包的data中细化出来一部分作为IP数据包的头header2，剩余部分仍旧为data

header1 |header2 | data
---|---|---

原因是仅通过广播的手段去遍历互联网中的每一台主机，想想就可怕。因此，网络层区分出来一个子网络的概念，即：
- 如果两台主机（发送方和接收方）在同一个子网络下的时候，依然才去链接层通过MAC地址广播的方式去通信，数据包中关于接收方信息的内容为主机IP和主机MAC地址；
- 如果不在同一个子网络下，则需要交给两个子网络的连接处——网关（gateway）去处理，数据包中关于接收方信息的内容为主机IP和网关MAC地址。

> 那么如何判断两台主机是否在同一个子网络下呢？

这就是通过IP协议的规定，IP协议包括IP地址，子网掩码，网关IP地址，DNS IP地址，这里面的内容由于比较基础，计科的同学都学过，我就不展开细聊了。当我们在浏览器中输入一个网址的时候，会首先请求DNS服务通过DNS协议的要求将网址转为实际的IP地址，对应的就是一台主机，然后通过将两台主机（本地主机和服务端主机）的IP地址与本地的子网掩码做AND运算，结果如果一致则为同一子网络，不一致则为不同的子网络。（可能经过多层网关转发，直到找到与远端主机的子网的网关，默认网关直接相当于在同一子网，这是我的理解，如有异议，欢迎讨论）。

> 网络层只认IP地址，MAC地址是链接层的概念，也就是说MAC地址只在子网络中使用，所以在网络层中理论上原始IP数据包只有IP地址，那么如何通过IP地址获得MAC地址呢？

首先两台机器在同一个子网络的时候，可以使用ARP协议：原数据包包含IP地址，MAC地址为六对均为F的十六进制地址，作为广播地址标示，子网络每台机器接收到这个数据包都会与自己的IP地址进行比对，如果一致则返回MAC地址，如果不一致则扔掉不处理。

#### ④传输层
传输层是网络数据包与主机内部程序的联系，参数为端口（Port），实际上端口就是每一个使用主机网卡的程序编号，这一层是端到端的通信。主要包含的通信协议是我们熟知的UDP和TCP协议。关于UDP，它又是在IP数据包的基础上对其data部分的进一步细化，划分出来一个头部header3，填充的是端口。

header1 |header2|header3 | data
---|---|---|---

UDP协议的出现是理所当然的，顺应着以太网数据包到IP数据包的变换，直接加入了端口的参数，但是UDP协议是不安全的，会出现丢包的行为，因此引出了升级的，复杂的，确认式的TCP协议，它是加入了一个TCP头，而不是赤裸的端口号。

#### ⑤应用层
这是属于应用程序解读网络数据包的一层。这里有不同的协议进一步划分了原数据包中data的部分，加入了对SMTP，DNS，Telnet，HTTP，FTP等传输协议的支持，存储在data新划分出来的头部header4，这些协议都属于应用层协议。

header1 |header2|header3|header3 | data
---|---|---|---|---

#### 互联网协议总结
这次我们从上而下，模仿一个普通浏览google的用户，它的网络数据包package是如何发出的
- package本是一串二进制电信号
- 加入本地MAC地址，加入数据内容
- 寻找访问主机的MAC地址：先将域名www.google.com通过DNS服务器转为IP地址，然后和本地地址比对发现不是同一子网络，则丢给当前本地主机的网关地址，经过多层网关转发找到了访问主机的网关地址，然后通过ARP协议，找到网关所在子网络中的IP相等的主机，找到它的MAC地址，加入package，以太网数据包组装完毕。
- 加入本地主机和访问主机的IP，IP数据包组装完毕
- 加入TCP头（或者直接是使用UDP协议，加入端口号），传输层数据包组装完毕
- 加入HTTP头，网络数据包package全部组装完毕。

最终的数据包的结构是

2个MAC地址 |2个IP地址|TCP头|HTTP头 | data
---|---|---|---|---

最后，目前整个互联网协议采用的是TCP/IP协议族，也就是上面各层出现的协议都属于TCP/IP协议的一部分。

---

## Socket
套接字，socket本质是编程接口(API)，对TCP/IP的封装，TCP/IP也要提供可供程序员做网络开发所用的接口，这就是Socket编程接口；HTTP是轿车，提供了封装或者显示数据的具体形式；Socket是发动机，提供了网络通信的能力。

### Socket与IO
之前介绍了[IO相关的内容](http://www.cnblogs.com/Evsward/p/io.html)。IO读写流遵循Open-Read-Write-Close的操作范式。当一个进程open了一个io流以后，可以对其进行多次的读写操作，然后在将其close。socket与这种IO流十分类似，也遵循一个打开socket（open），接收或发送socket（读写），关闭socket（close）的操作范式。

> 一个socket地址是由网络地址和端口号组成的通信标识符。进程间通信操作需要一对儿socket。

### Socket与TCP/UDP
Socket编程可基于两种协议：TCP或UDP。

- 数据报通信
即基于UDP的Socket编程。UDP是一种无连接的协议，每次通信都要额外带上双方的socket信息（即IP加端口等描述性信息）

-  流通信
即基于TCP的Socket编程。TCP是一种基于连接的协议，每次通信要先通过双方的socket信息建立一个连接通道。其中一个作为服务端监听连接请求，另一个作为客户端发送连接请求。连接一旦建立，就可以单向或者双向的多次数据传输。

UDP是不可靠的协议，有可能丢包，但是它不必耗时建立连接，但是每个数据报的大小有64kb的限制，所以在某些实时性要求较高但是对丢包不太敏感的场景下比较适合使用。

TCP是可靠的协议，不会丢包，但是由于是基于连接的，所以在创立连接的时候会耗时，连接建立以后就与正常的IO一样，数据传输并没有大小的限制，同时数据传输是有顺序的，发包顺序和接包顺序是一致的。在远程登录或FTP文件传输过程中，使用TCP是适合的，因为它对未知的数据流量大小并不敏感。

### java TCP socket编程

- 客户端：java.net.Socket
- 服务端：java.net.ServerSocket

> 此类实现客户端套接字（也可以就叫“套接字”）。套接字是两台机器间通信的端点。套接字的实际工作由 SocketImpl 类的实例执行。应用程序通过更改创建套接字实现的套接字工厂可以配置它自身，以创建适合本地防火墙的套接字。

上面是jdk官方解释，注意关键语句实际工作由SocketImpl类的实例执行，而这个实例是通过更改Socket工厂来创建的。下面进入代码，我们先来创建一个客户端Socket实例，

```
Socket socket = new Socket();
```
可以看到构造函数是无参的，我们进入源码去看一下（下方源码是经过我调整的，以方便集中注意力查看），

```
    SocketImpl impl;// 真正的Socket执行类
    
    private static SocketImplFactory factory = null;// 类的静态属性Socket工厂实例初始化为空。
    
    // 实际上是通过外部调用该方法来设置Socket工厂，而此具体Socket工厂是由我们自己开发的，是SocketImplFactory接口的实现类。
    public static synchronized void setSocketImplFactory(SocketImplFactory fac)
        throws IOException
    {
        if (factory != null) {
            throw new SocketException("factory already defined");
        }
        SecurityManager security = System.getSecurityManager();
        if (security != null) {
            security.checkSetFactory();
        }
        // 上面都是一些考虑周到的安全性检查，我们仍旧集中注意力即可。
        factory = fac;
    }
    
    // 这是Socket的无参构造方法
    public Socket() {
        if (factory != null) {
            impl = factory.createSocketImpl();// 利用Socket工厂（实现类）创建Socket实例
            checkOldImpl();// 通过本地方法通过Socket实例创建连接
        } else {
            // No need to do a checkOldImpl() here, we know it's an up to date SocketImpl!
            impl = new SocksSocketImpl();// 如果没有传入Socket工厂实现类，则创建一个SocketImpl子类SocksSocketImpl extends PlainSocketImpl implements SocksConsts
            // class PlainSocketImpl extends AbstractPlainSocketImpl
            // abstract class AbstractPlainSocketImpl extends SocketImpl
            // 这个继承关系看出来了吧，SocksSocketImpl是一个继承了好几代的SocketImpl子类，同时加入了很多接口实现，增加了很多功能。
        }
        if (impl != null)
            impl.setSocket(this);        
    }
```
> 题外话：java中接口的对象声明，初始化只能为null，无法创建接口对象。

理解Socket最简单的就是把它当做一个普通的IO节点流，socket是一个设备文件。Socket有自己的getInputStream()方法来获得输入流读取Socket内容，也有自己的getOutputStream()方法来获得输出流写到Socket中去，[具体的IO的操作](http://www.cnblogs.com/Evsward/p/io.html)可以查看我前面的博文。

```
    @Test
    /**
     * 客户端测试
     */
    public void clientRequest() throws UnknownHostException, IOException {
        Socket client = new Socket(ipAddress, port);
        // 利用缓冲器来写入client内容
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
        bw.write("test client...");
        // 关闭IO对象，释放资源
        bw.close();
        client.close();
    }
```
然后写一个服务端监听服务：


```
@Test
    public void serverListening() throws IOException {
        ServerSocket server = new ServerSocket(port);// 服务端Socket开始监听端口port
        Socket socket = server.accept();// 当没有客户端连入时会保持监听状态
        // 连入客户端以后开始往下执行，最终随着客户端断开，连接自动关闭
        BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        StringBuilder sb = new StringBuilder();
        String str;
        while ((str = br.readLine()) != null) {
            sb.append(str);
        }
        // 关闭IO对象，释放资源
        br.close();
        socket.close();
        server.close();
        // 打印读取的数据
        logger.info(sb);
    }
```
开始测试，我们首先开启服务端监听服务，开启后会发现服务端会始终保持监听状态。然后执行客户端请求方法，会发现此时服务端开始读取客户端传入的数据（实际上也可以理解为写入socket设备文件的内容），打印出来并且随着客户端请求方法的执行结束断开连接以后，服务端也跟着断开连接。


#### 基于TCP的服务端客户端模型

一个标准的基于TCP的服务器客户端模型需要满足几个条件：
- 服务端应该同时处理多个客户端的请求，因此我们要在服务端引入多线程
- 连接建立以后，我们希望这个连接能够保持，因此要在服务端客户端双向的socket关闭之前做一定的处理（无限while循环，直到接受结束指令）
- 多个客户端连接到同一个服务端，我们希望能够有客户端的编号以作区分
- 客户端向服务端发起请求，服务端可以接受请求并处理，返回响应，客户端接受响应也可做按照指令做相关处理。
- 客户端可以发起“连接关闭”的请求来通知服务端自己主动断开了连接，服务端也可以发起“连接关闭”的响应来通知客户端自己主动断开了连接，双方都有主动断开的能力。

> 关于客户端编号的问题，我尝试了几种方案，最终选择了使用客户端socket.hashCode()作为编号，然而该编号在多个客户端发起请求时值是相等的，但是服务端接收的客户端socket的hashCode是不同的，也就是说，在服务端可以通过socket.hashCode()来作为多客户端的编号区分，但是在客户端方面，无法认知自己是哪个编号。因此，我对代码做了一些调整，socket建立连接是发生在socket实例创建时，创建完成以后，首先会从服务端发起一个响应（我们默认所有从服务端发出的消息都为响应，而所有从客户端发起的消息都为请求。），将该socket.hashCode传给客户端，客户端获得该编号以后立即打印出来以标明自己的身份。

下面看代码，首先先写一个socket的多线程类。


```
package javaS.IO.socketS;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

/**
 * 服务端多线程，一个服务端可以同时处理多个客户端的请求
 * 
 * @author Evsward
 *
 */
public class ServerThread extends Base implements Runnable {
    private Socket client = null;

    public ServerThread(Socket client) {
        this.client = client;
    }

    @Override
    public void run() {
        try {
            PrintStream sendResponse = new PrintStream(client.getOutputStream());
            BufferedReader getRequest = new BufferedReader(new InputStreamReader(client.getInputStream()));
            sendResponse.println("clientID:" + client.hashCode());
            boolean flag = true;
            while (flag) {
                String req = getRequest.readLine();
                if (req == null || "".equals(req)) {
                    logger.info("获得空请求，关闭该连接：" + client.hashCode());
                    break;
                }
                if ("disconnect".equals(req)) {
                    flag = false;// 获得客户端终止标示的请求，则断开连接。
                } else {
                    logger.info(req);
                    sendResponse.println("echo:" + req);
                }
            }
            getRequest.close();
            sendResponse.close();
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

```
这个类中，我们定义了一个客户端Socket的实例属性作为类的成员变量。该类实现了Runnable接口，要复写run方法（多线程的基本知识这里就不再多说了）。run方法中，我们先获取socket的输入输出流，然后加入了一个“无限循环”用来保持该连接中服务端的监听状态，然后是对socket中请求内容的处理（这里有一个特殊情况，就是当该连接中客户端连接断开以后，服务端连接不会自动断开，而是不断接收到null的请求，所以这里要针对这种情况，主动将该连接的服务端方面断开。）然后是将”disconnect”作为连接终止信号进行处理断开该连接中服务端方面的Socket。接着就是正常的打印出发自客户端的请求内容，同时加入“echo”拼串作为简单处理响应返回给客户端。最后关闭所有流和连接，释放资源。

下面我们正式编写该服务端客户端模型的测试类。


```
package javaS.IO.socketS;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import org.junit.Test;

/**
 * 客户端和服务端都要有一个while循环，用于保持他们的监听状态。
 * 
 * @author Evsward
 *
 */
public class TestSocket extends Base {
    @Test
    /**
     * 客户端
     */
    public void clientRequest() throws UnknownHostException, IOException {
        Socket client = new Socket(ipAddress, port);
        // 客户端输入
        BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
        // socket写入流，向服务端发送请求
        PrintStream request = new PrintStream(client.getOutputStream());
        // socket读取流，用来读取服务端返回信息
        BufferedReader response = new BufferedReader(new InputStreamReader(client.getInputStream()));
        String clientID = response.readLine();
        boolean flag = true;
        while (flag) {
            logger.info("客户端:<" + clientID + ">输入信息：");
            String str = input.readLine();
            if (str == null || "".equals(str)) {
                logger.info("请不要输入空字符！");
                continue;
            }
            if ("bye".equals(str)) {
                logger.info("客户端终止请求，断开连接。");
                request.println("disconnect");
                break;
            }
            // 客户端发起请求
            request.println(str);
            // 客户端等待响应
            String echo = response.readLine();
            logger.info("response: " + echo);
            logger.info("---------------------");
        }
        request.close();
        client.close();
    }

    @Test
    /**
     * 服务端
     */
    public void serverListening() throws IOException {
        ServerSocket server = new ServerSocket(port);
        boolean f = true;
        Socket client = null;
        while (f) {// 保持整个客户端的监听状态
            client = server.accept();// 阻塞，等待客户端的连接，创建一个新的socket实例
            logger.info("客户端:<" + client.hashCode() + ">连接成功！");
            new Thread(new ServerThread(client)).start();
        }
        server.close();
    }

}

```
这里面服务端部分只是做了建立连接，保持监听，同时开启多线程维护多个与客户端建立的socket连接。这里要注意的部分是在开启多线程外部仍旧要有一个循环，它与SocketThread中run方法内部的循环不同，前者是为了保持整个客户端的监听状态，而后者是为了保持多客户端中的一个socket连接中服务端方面的监听状态，是父子包含的关系。

接下来是客户端部分，这部分的所有操作基本与多线程中服务端的run方法内部的操作极其类似，开启双向流，发起请求，接收响应，处理消息数据，主动被动断开连接等等。唯一要注意的是客户端的Socket在初始化时的构造函数是包含IP和端口两个参数，而服务端ServerSocket的构造函数只有端口，这一点想一想也能明白，因为服务端不用去找主机，它只要区分处理本机的不同应用程序所在的端口即可，而客户端是网络中的其他主机，上面“互联网协议”部分介绍的很清楚，或许客户端主机与服务器主机不在一个子网络，或许在一个子网络，中间处理方式是不同的，但无论如何，客户端想与服务端主机通信，必须要加入服务端主机的IP作为地址去寻找才行。

下面开始测试：
- 首先启动serverListening()方法，会发现控制台没有任何输出，但始终保持监听状态。
- 然后开启一个clientRequest()，会发现server的控制台输出了包含一个客户端编号的“连接成功”的字样，同时客户端控制台也打印出了包含他自身编号的“输入信息”字样，这两个编号是一致的，说明他们在同一个连接中。
- 然后再开启一个clientRequest()，客户端控制台效果同上，但是服务端会又输出一行包含一个新的客户端编号的“连接成功”的字样，不言而喻，这个编号与当前客户端打印出来的自身编号是一致的，这都归功于我们建立的机制：在连接刚刚建立的时候就从服务端识别出客户端编号然后向客户端发起了一个响应告诉它自己的编号是什么。
- 可以继续开启多个clientRequest()，效果均与上面相同，说明一台服务器可以同时处理多个客户端的请求。
- 在其中一个客户端控制台上面输入一串字符，按下回车，会发现控制台打印出来这串字符并发送给了服务端，再去查看服务端控制台，确实发现了该字符串的信息，然后服务端会拼个带“echo”的响应信息，再回到客户端控制台，会发现客户端控制台将这个拼串的响应信息也打印了出来，说明整条链路的通信功能是正常有效的。当然了实际测试中运行速度非常快，客户端输入字符回车以后，立即就能看到带“echo”的响应信息了。
- 在其他客户端仍旧可以重复上面的操作，每次服务端打印时都可以加入客户端编号，以区分这条信息的来源。

> 阻塞和监听的区别是什么？

- 监听，代码中socket.close之前的“无限循环”，作用就是始终保持该socket在线，不关闭，随时可处理消息。
- 阻塞，代码中server.accept()方法，如果没有客户端连接，则会阻塞，阻塞的意思是该线程停止运行，持续等待，直到有连接进来或者超时。

所以区别是什么，监听和阻塞都是随时待命的意思，但是监听是线程还在运行，并未停止，只是没有消息的时候，是空转而已，但是阻塞是线程停止，持续等待，这是很大的区别。

#### 基于线程池的java TCP socket服务器
以上代码中我们使用到了多线程，实现了一个服务端可同时处理多个客户端请求的功能需求，然而，这样直接使用new Thread(new ServerThread(client)).start();会有两个弊端：
- 每个新创建的线程都会消耗系统资源：它有自己的数据结构，会消耗内存，占用CPU周期
- 如果一个线程遇到阻塞，JVM会保存其状态，选择另一个线程运行，同时在上下文转换的时候恢复阻塞线程的状态，这无疑为JVM增加了工作量，当线程增多时，上下文转换将花费更多时间。

下面我们引入线程池，针对以上问题，线程池给出了答案：
- 线程池预先就规定好了线程的总数，并可重复使用线程。
- 当用户请求线程池时，会被分配到一个线程，该线程执行完毕仍旧回到线程池等待下一个请求。
- 当请求过来发现线程池中所有线程均被占用时，就会进入一个队列来等候空闲的线程出来，关于消息队列，常见的框架有RabbitMQ，ActiveMQ等。

> 码前准备：

- 仍旧先创建一个ServerSocket实例。
- 然后创建总数为N个线程，每个线程反复循环，从ServerSocket实例中接收客户端请求，也就是这些线程都阻塞在ServerSocket的accept方法，直到有一个线程连接成功，剩下的继续阻塞。
- 线程完成一个客户端请求的任务以后，不终止而是继续阻塞。
- 如果客户端连接创建时，没有线程在accept方法阻塞，说明所有线程都在运行中，系统会将新的连接排列在一个队列中，直到有线程阻塞在accept方法。

```
package javaS.IO.socketS;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * 服务端线程池
 * 
 * @author Evsward
 *
 */
public class ThreadPool extends Base {
    private static final int THREADPOOLSIZE = 2;

    /**
     * 服务端，启动main方法，客户端启动TestTCPSocket的clientRequest()Junit方法。
     */
    public static void main(String[] args) throws IOException {
        @SuppressWarnings("resource")
        final ServerSocket server = new ServerSocket(port);
        for (int i = 0; i < THREADPOOLSIZE; i++) {
            new Thread() {
                public void run() {
                    Socket client = null;
                    boolean f = true;
                    while (f) {
                        try {
                            client = server.accept();// 阻塞，等待客户端的连接
                            logger.info("客户端:<" + client.hashCode() + ">连接成功！Server Thread ID:" + this.getId());
                            ServerThread.execute(client);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }.start();
        }
        // 不能关闭server.close();
    }
}

```
新建一个线程池类，启动其main方法作为服务端，循环创建2个线程都阻塞在accept方法，此时仍旧使用TestTCPSocket的clientRequest()Junit方法连入一个客户端，服务端的1个线程从阻塞转为运行去处理该客户端请求，再启动一个clientRequest，服务端的另一个线程也投入使用，此时服务端线程池已全部被使用，此时再启动第三个客户端请求，会被放入队列中，直到前面有一个客户端断开，释放出了服务端的一个线程才会处理第三个客户端请求。

#### 高级线程池
上面我们创建了一个总数为2的线程池，为了测试方便，只分配了2个线程，然而实际工作中，这个总数的设定是个很重要的考量因素，创建太多会无端占用那么多系统资源，创太少，并发处理客户端请求的能力又太弱，因为大部分的请求都被堆在了队列里。这一部分内容我们引入高级线程池，针对以上问题，高级线程池给出了答案。
- 线程池大小根据负载情况自动调整。
- 负载增加时，扩展线程池的大小。
- 负载减轻时缩减线程池的大小，例如如果一个线程空闲了 60 秒以上，则将其移出线程池。

> 作为高级线程池的一种实现，jdk 中的 Executor 接口的定义非常简单。


```
public interface Executor {

    /**
     * Executes the given command at some time in the future.  The command
     * may execute in a new thread, in a pooled thread, or in the calling
     * thread, at the discretion of the {@code Executor} implementation.
     *
     * @param command the runnable task
     * @throws RejectedExecutionException if this task cannot be
     * accepted for execution
     * @throws NullPointerException if command is null
     */
    void execute(Runnable command);
}
```
但是Java 提供了大量的内置 Executor 接口实现，它们都可以简单方便地使用，ExecutorService 接口继承于Executor 接口，它提供了一个更高级的工具来关闭服务器，包括正常的关闭和突然的关闭。

```
package javaS.IO.socketS;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ServerExecutor extends Base {
    public static void main(String[] args) throws IOException {
        // 通过调用Executors类的静态方法，创建一个ExecutorService实例
        // ExecutorService接口是Executor接口的子接口
        Executor service = Executors.newCachedThreadPool();
        ServerSocket server = new ServerSocket(port);
        boolean f = true;
        Socket client = null;
        while (f) {// 保持整个客户端的监听状态
            client = server.accept();// 阻塞，等待客户端的连接
            logger.info("客户端:<" + client.hashCode() + ">连接成功！");
            // 调用execute()方法时，如果必要，会创建一个新的线程来处理任务，但它首先会尝试使用已有的线程，
            // 如果一个线程空闲60秒以上，则将其移除线程池；
            // 另外，任务是在Executor的内部排队，而不是在网络中排队
            service.execute(new ServerThread(client));
        }
        server.close();
    }
}

```
只是这里我们无法获取线程的ID了，测试时没办法通过线程ID查看线程的生命周期，但是在客户端方面，这样的线程池可以满足尽可能创建客户端连接，而客户端对线程池的存在并无感觉，好似在使用最初的第一版的时候。


### java UDP socket编程

- 通信工具：java.net.DatagramSocket
- 数据包：java.net.DatagramPacket

> 客户端和服务器端都通过DatagramSocket的send()方法和receive()方法来发送和接收数据，用DatagramPacket来包装需要发送或者接收到的数据。


```
package javaS.IO.socketS;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import org.junit.Test;

/**
 * UDP socket编程
 * 
 * @author Evsward
 *
 */
public class TestUDPSocket extends Base {
    // 这是为了限制客户端的，为了避免receive方法的永久阻塞，限制超时时间和重发次数。
    private static final int TIMEOUT = 5000; // 设置接收数据的超时时间
    private static final int MAXNUM = 5; // 设置重发数据的最多次数
    private static final int BUFSIZE = 1024;// 用于临时接收数据的字节中转站
    private static final int CLIENT_PORT = 3000;// 客户端所在端口
    private static final int SERVER_PORT = 3100;// 服务端所在端口

    @Test
    /**
     * 在端口23451建立socket，发送数据到端口3000
     * 
     * 客户端操作一次就结束了，自动断开连接，可以发起多个客户端请求服务端
     */
    public void clientRequest() throws IOException {
        byte buf[] = new byte[BUFSIZE];
        InetAddress loc = InetAddress.getLocalHost();
        DatagramSocket client = new DatagramSocket(CLIENT_PORT);
        String msgRequest = "hello, this is client." + client.hashCode();
        client.setSoTimeout(TIMEOUT);
        // 定义UDP请求消息数据包，发送到服务端的端口上。
        DatagramPacket dp_send = new DatagramPacket(msgRequest.getBytes(), msgRequest.length(), loc, SERVER_PORT);
        DatagramPacket dp_receive = new DatagramPacket(buf, BUFSIZE);// 做一个DatagramPacket实例用来接受响应数据。
        boolean flag = true;
        int tries = 0;
        while (flag && tries < MAXNUM) {
            client.send(dp_send);
            try {
                client.receive(dp_receive);
                flag = false;
            } catch (InterruptedIOException e) {
                tries++;
                logger.error("Time out! Try " + (MAXNUM - tries) + " more times.");
            }
        }
        if (!flag) {
            String msgResponse = new String(dp_receive.getData(), 0, dp_receive.getLength()) + " from "
                    + dp_receive.getAddress().getHostAddress() + ":" + dp_receive.getPort();
            logger.info(msgResponse);
        } else {
            // 重发最大次数后，仍未接到服务器响应
            logger.info("Still no response -- give up.");
        }
        client.close();
    }

    @Test
    /**
     * 服务端应该一直在线
     */
    public void serverResponse() throws IOException {
        byte buf[] = new byte[BUFSIZE];
        InetAddress loc = InetAddress.getLocalHost();
        DatagramSocket server = new DatagramSocket(SERVER_PORT);// 不必设置超时时间，让服务端一直在线
        String msgResponse = "hello, this is server." + server.hashCode();
        DatagramPacket dp_receive = new DatagramPacket(buf, BUFSIZE);// 做一个DatagramPacket实例用来接受客户端请求数据。
        boolean flag = true;
        while (flag) {
            server.receive(dp_receive);
            String msgRequest = new String(dp_receive.getData(), 0, dp_receive.getLength()) + " from "
                    + dp_receive.getAddress().getHostAddress() + ":" + dp_receive.getPort();
            logger.info(msgRequest);
            // 定义UDP响应消息数据包，发送到客户端的端口上。
            DatagramPacket dp_send = new DatagramPacket(msgResponse.getBytes(), msgResponse.length(), loc, CLIENT_PORT);
            server.send(dp_send);
            // DatagramPacket的内部消息长度值在接收数据后会发生改变，变为实际接收到的数据的长度值。
            // 这里由于还在循环里，不要影响新一轮循环的使用，因此要再次将其长度改为初始化长度。
            dp_receive.setLength(1024);
        }
        server.close();
    }
}

```
我们来看代码，UDP的socket编程明显气质与TCP不同，因为这里面的客户端和服务端是我们自己创造的，他们都使用DatagramSocket来通信，并没有TCP中Socket和ServerSocket的分别。另外UDP的传输格式是以数据包的形式，而不是TCP中的流，它没有那么多的流相关的操作，发送和接收都只要装配好DatagramPacket对象即可。

从代码中可以看出，我们针对服务端，为了让它保持一直在线的特性，我们对它进行了“无限循环”的操作，它可以随时receive一个DatagramPacket，然后返回一个响应信息发送给客户端。而客户端则不然，我们为了让它保有客户端的特性，我们为它增加了超时限制，重复发送次数限制等，而且它不会始终在线，当它发送完客户端请求，接收服务端的响应信息以后就会自动断开连接。

这里要注意的地方就是DatagramSocket并不会像ServerSocket的accpet方法那样，每次连接客户端时要创建一个新的socket实例，而是始终用的是同一个DatagramSocket实例。UDP是不基于连接的协议，因此这里面并不存在如TCP那种连接的定义，一个客户端连接断开了，服务端的DatagramSocket可以仍旧继续监听，服务端始终只有一个DatagramSocket实例来随时接收来自客户端的请求。

另外就是DatagramPacket，对于该数据包的装配也是要注意的点，它有很多构造方法，要指定它的IP和端口，这个数据包的结构就像上面“互联网协议”中UDP定义的那样，无论是服务端还是客户端，都要指定好双方的IP和端口，这一点并不像TCP，服务端只需要指定端口即可，客户端也只是创建一个基于端口的连接即可，在发送客户端请求时并不需要指定服务端的端口。


### 传输数据的格式
上面我们介绍了java中TCP和UDP的socket编程，其中UDP有明确的传输数据类DatagramPacket，该类对数据包的各种属性都做了封装，尤其是它对传输内容的边界和长度进行了定义，无论是发送和接收都使用相同的格式DatagramPacket，因此可以做到自动解析传输数据。而TCP并没有明确的传输数据类型，它支持各种IO流的方式来传输数据，所以就有了成帧和解析的技术。
- 成帧，就是设定传输数据的定界符和长度，在TCP中，无法确认边界和长度常常引发TCP粘包和拆包的问题。
- 解析，与成帧对应的，当接收到成帧的数据以后，根据同样的规则将源数据解析出来。

我们还可以针对TCP socket编程，自定义传输数据类，就像DatagramPacket那样，但是会涉及到很多底层麻烦的处理。

通常来讲，我们在实际项目中传输的数据都是包含数据的序列化的对象，序列化技术我们在IO中已经介绍过，有着自己的实现方式，但这些与很多主流编解码开源框架相比仍然是繁琐且易出错，所以，我们应该被鼓励使用更多编解码开源框架，例如Google的Protobuf，Facebook的Thrift以及JBoss的Marshalling，其中我之前接触较多的是Protobuf框架。下面我们针对Protobuf进行简单介绍。

#### Protobuf介绍
Protobuf全成Google Protocol buffers，他将数据结构以.proto文件进行描述，通过代码生成工具可以生成对应数据结构的POJO对象和Protobuf相关的方法和属性。它的特点包括：
- 结构化数据存储格式（XML,JSON等）
- 高效的编解码性能
- 跨语言，跨平台，易扩展

其实我在上一篇[javaIO的序列化部分](http://www.cnblogs.com/Evsward/p/io.html#三对象流-序列化)，省略了XML的介绍，XML可以作为标准的对象序列化技术将包含数据的对象在网络中传输，但尽管XML的可读性和扩展性都非常好，也非常适合描述数据结构，但是XML解析的时间开销和XML为了可读性而牺牲的空间开销都非常大，因此不适合做高性能的通信协议，这也是为什么实际项目中普遍不使用XML做对象传输的原因，也是我在java IO直接选择忽略它的原因。相较之下，Protobuf使用二进制编码，性能，码流占用空间都有极大优势，所以Protobuf无论在java还是C++的后台服务中都频繁出现。

Protobuf最好的两个特点就是：
- 数据描述文件
- 代码生成机制

关于代码生成机制，可以了解我之前的[关于泛型的生成器部分](http://www.cnblogs.com/Evsward/p/genericity.html#基于generator的泛型方法)，以及[Jenkins源码中介绍的通过Maven插件自动生成国际化类](http://www.cnblogs.com/Evsward/p/localizer.html#maven-localizer-plugin)的文章。java中所有的代码自动生成，包括像ORM框架mybatis自动生成PO代码等，都是通过IO创建类文件，批量写入映射字段的方式。

书归正传，Protobuf也有同样的代码生成机制，可以根据数据描述文件自动生成相关代码（这个生成出来的类是不允许更改的，要做修改的话去修改数据描述文件，然后再次生成一遍即可）。与Jenkins国际化属性文件相同的是，Protobuf的数据描述文件都是结构化的文档，比起代码中定义一堆常量，更容易管理和维护。

关于Protobuf的具体使用方法以及代码实例，我会在NIO或者Netty的文章中进行详细介绍。

### 源码位置
[Evsward的github](https://github.com/evsward/mainbase/tree/master/src/main/java/javaS/IO/socketS)

### 其他更多内容请转到[醒者呆的博客园](http://www.cnblogs.com/Evsward/)






