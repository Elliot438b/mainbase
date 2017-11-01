package pattern.builder;

public class Client {
	public static void main(String[] args) {
		Apple apple = AppleBuilder.construct((AppleBuilder) XMLUtil.getBean());
		System.out.println("" + apple.getCamera());
	}
}
