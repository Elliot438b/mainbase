package pattern.bridge;

public class GACToyota extends SSSS {

    @Override
    public void ask() {
        ops.askPrice();
        ops.askInsurance();
    }

}
