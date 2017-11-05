package pattern.bridge.taolu;

import pattern.bridge.thirdparty.SmartTalking;

public class SmartTalkingAdapter implements SpeakIn4S {
    private SmartTalking st = new SmartTalking();

    @Override
    public void scanTypes() {
        st.askCar();
    }

    @Override
    public void askPrice() {
        st.askMoney2();
    }

    @Override
    public void askInsurance() {
        st.askAfterSale();
    }

}
