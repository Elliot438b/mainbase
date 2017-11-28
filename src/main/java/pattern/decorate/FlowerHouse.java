package pattern.decorate;

public class FlowerHouse extends Decorate {

    public FlowerHouse(House h) {
        super(h);
    }

    private void decorateFlowerAround() {
        logger.info("The room is full of flowers now.");
    }

    @Override
    public void show() {
        super.show();
        decorateFlowerAround();
    }

}
