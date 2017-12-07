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
