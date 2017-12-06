package javaS.IO.socketS;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

public class Client {
    private final static Logger logger = LogManager.getLogger();
    private String ipAddress = "127.0.0.1";
    private int port = 23451;

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
    }

    @Test
    public void echoServer(){
        
    }
}
