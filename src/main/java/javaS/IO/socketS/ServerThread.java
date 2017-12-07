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

    public static void execute(Socket client) {
        try {
            PrintStream sendResponse = new PrintStream(client.getOutputStream());
            BufferedReader getRequest = new BufferedReader(new InputStreamReader(client.getInputStream()));
            sendResponse.println("clientID:" + client.hashCode());
            boolean flag = true;
            while (flag) {// 保持多客户端中的一个socket连接中服务端方面的监听状态
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

    @Override
    public void run() {
        execute(client);
    }

}
