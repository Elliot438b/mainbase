package javaS.IO.socketS;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Base {
    protected final static Logger logger = LogManager.getLogger();
    protected static String ipAddress = "127.0.0.1";
    protected static int port = 23451;
    protected static String EOFlag = "disconnect";
}
