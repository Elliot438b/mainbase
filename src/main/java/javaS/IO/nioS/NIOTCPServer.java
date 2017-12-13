package javaS.IO.nioS;

import javaS.IO.nioS.aioS.AsyncServerHandler;
import javaS.IO.socketS.Base;

public class NIOTCPServer extends Base {
  public static void main(String[] args) {
    new Thread(new AsyncServerHandler(), "nio-server-reactor-001").start();
  }
}
