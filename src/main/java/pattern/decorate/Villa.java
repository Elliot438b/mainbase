package pattern.decorate;

public class Villa extends House {

    @Override
    public void show() {
        logger.info("My villa has a very big yard.");
    }

}
