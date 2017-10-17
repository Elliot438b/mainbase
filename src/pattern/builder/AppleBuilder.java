package pattern.builder;

public abstract class AppleBuilder {
	protected Apple apple = new Apple();

	public abstract void buildCamera();

	public abstract void buildTouchScreen();

	public abstract void buildCommunication();

	public boolean ifCommunication() {
		return false;
	}

	public Apple createApple() {
		return apple;
	}

	public static Apple construct(AppleBuilder ab) {
		ab.buildCamera();
		ab.buildTouchScreen();
		if (ab.ifCommunication()) {
			ab.buildCommunication();
		}
		Apple apple = ab.createApple();
		return apple;
	}
}
