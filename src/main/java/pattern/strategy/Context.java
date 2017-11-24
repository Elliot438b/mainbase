package pattern.strategy;

public class Context {
    private Strategy strategy;

    public void setStrategy(Strategy strategy) {
        this.strategy = strategy;
    }

    public void testSTStrategy() {
        strategy.algorithm();
    }
}
