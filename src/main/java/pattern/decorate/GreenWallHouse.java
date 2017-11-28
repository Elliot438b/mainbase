package pattern.decorate;

public class GreenWallHouse extends Decorate {

    public GreenWallHouse(House h) {
        super(h);
    }

    private void painGreenOnWall() {
        logger.info("The wall is green now.");
    }

    @Override
    public void show() {
        super.show();
        painGreenOnWall();
    }

}
