package pattern.decorate;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

public class Client {
    public final static Logger logger = LogManager.getLogger();

    @Test
    public void testDecorate() {
        House a = new Apartment();
        a.show();
        logger.info("---------------");
        House aPlus = new GreenWallHouse(a);
        aPlus.show();
        logger.info("---------------");
        /**
         * 因为他们都继承了House，是同一个基类，所以可以无限套用装饰类去循环。
         */
        House aPPlus = new GreenWallHouse(new FlowerHouse(a));
        aPPlus.show();
    }
}
