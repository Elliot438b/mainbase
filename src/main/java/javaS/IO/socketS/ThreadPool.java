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
