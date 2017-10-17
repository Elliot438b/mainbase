package pattern.builder;

public class IPhone extends AppleBuilder {

	@Override
	public void buildCamera() {
		apple.setCamera("1200 pixel");
	}

	@Override
	public void buildTouchScreen() {
		apple.setTouchScreen("retina");
	}

	@Override
	public void buildCommunication() {
		apple.setCommunication("TDMA");
	}

	@Override
	public boolean ifCommunication() {
		return true;
	}

}
