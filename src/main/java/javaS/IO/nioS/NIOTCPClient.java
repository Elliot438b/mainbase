package javaS.IO.nioS;

import javaS.IO.nioS.aioS.AsyncClientHandler;
import javaS.IO.socketS.Base;

public class NIOTCPClient extends Base {
  public static void main(String[] args) {
    new Thread(new AsyncClientHandler(), "nio-client-reactor-001").start();
  }
}
