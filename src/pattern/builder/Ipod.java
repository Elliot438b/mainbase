package pattern.builder;

public class Ipod extends AppleBuilder {

	@Override
	public void buildCamera() {
		apple.setCamera("800 pixel");
	}

	@Override
	public void buildTouchScreen() {
		apple.setTouchScreen("NOVA");
	}

	@Override
	public void buildCommunication() {
		apple.setCommunication("none");
	}
}
