package pattern.bridge.taolu;

/**
 * 4S店操作的具体话术套路：普通版
 * 
 * @author Evsward
 *
 */
public class CommonTaolu implements SpeakIn4S {

    @Override
    public void scanTypes() {
        System.out.println("你家共有几种车？");
    }

    @Override
    public void askPrice() {
        System.out.println("能不能再给些优惠？");
    }

    @Override
    public void askInsurance() {
        System.out.println("保险都包括哪些内容？");
    }

}
