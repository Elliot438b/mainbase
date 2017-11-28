package pattern.decorate;

public class Cabin extends House {

    @Override
    public void show() {
        logger.info("This cabin is for saving the tools.");
    }

}
