package javaS.IO.nioS;

import javaS.IO.socketS.Base;

public class NIOTCPClient extends Base {
  public static void main(String[] args) {
    new Thread(new ReactorClientHandler(), "nio-client-reactor-001").start();
  }
}
