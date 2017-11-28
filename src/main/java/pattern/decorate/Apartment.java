package pattern.decorate;

public class Apartment extends House {

    @Override
    public void show() {
        logger.info("This is my apartment, which is on the high floor.");
    }

}
