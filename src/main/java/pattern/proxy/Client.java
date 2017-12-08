package pattern.proxy;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

public class Client {
    private final static Logger logger = LogManager.getLogger();
    private Proxy proxy = null;

    @Test
    public void testSimpleProxy() {
        proxy = new SuperProxy();
        long id = proxy.getId();
        String pwd = proxy.getPassword();
        logger.info("id:" + id + " password:" + pwd);
    }
}
