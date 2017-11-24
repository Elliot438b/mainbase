package pattern.bridge.taolu;

/**
 * 4S店操作的具体话术套路：东北版
 * 
 * @author Evsward
 *
 */
public class NorthTaolu implements SpeakIn4S {

    @Override
    public void scanTypes() {
        System.out.println("你这都有啥车型？");
    }

    @Override
    public void askPrice() {
        System.out.println("我认识人，能便宜点不？");
    }

    @Override
    public void askInsurance() {
        System.out.println("是全保不？");
    }

}
