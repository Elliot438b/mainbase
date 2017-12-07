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
public class TestTCPSocket extends Base {
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
