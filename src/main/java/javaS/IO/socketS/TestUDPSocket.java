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
