package pattern.proxy;

import org.junit.Test;

public class Client {
    private Proxy proxy = null;

    @Test
    public void testSimpleProxy() {
        proxy = new Proxy();
        proxy.getId();
        proxy.getPassword();
    }
}
