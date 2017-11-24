package pattern.strategy;

import org.junit.Test;

public class StrategyClient {

    @Test
    public void testStrategy() {
        Context context = new Context();
        context.setStrategy(new RedBlackBSTAdapter());// 运行时指定具体类型
        context.testSTStrategy();
        context.setStrategy(new BSTAdapter());// 运行时指定具体类型
        context.testSTStrategy();
    }
}
