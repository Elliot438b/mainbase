package pattern.proxy.search;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

public class Client {
    private final static Logger logger = LogManager.getLogger();

    @Test
    public void testSearcher() {
        Searcher searcher = new ProxySearcher();// 创建一个代理类对象，而不是RealSearcher
        logger.info(searcher.doSearch("jhon", 1002));
        logger.info(searcher.doSearch("jhon", 1001));
        logger.info(searcher.doSearch("jack", 1002));
        logger.info(searcher.doSearch("java", 1003));
    }
}
