package pattern.bridge;

public class ChanganFord extends SSSS {

    @Override
    public void ask() {
        ops.scanTypes();
        ops.askPrice();
        ops.askInsurance();
    }
}
