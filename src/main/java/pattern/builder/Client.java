package pattern.builder;

import tools.XMLUtil;

public class Client {
    public static void main(String[] args) {
        Apple apple = AppleBuilder.construct((AppleBuilder) XMLUtil.getBean("builder"));
        System.out.println("" + apple.getCamera());
    }
}
