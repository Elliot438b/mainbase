package pattern.bridge;

import pattern.bridge.taolu.SpeakIn4S;
import tools.XMLUtil;

public class Client {
    static void askACar() {
        SSSS c;
        SpeakIn4S taolu;
        // 第一个维度：选择进入哪一家4S店。
        c = (SSSS) XMLUtil.getBean("ssss");
        // 第二个维度：进了4S店说什么。
        taolu = (SpeakIn4S) XMLUtil.getBean("taolu");
        // 两个维度相互关联上。
        c.setOps(taolu);
        c.ask();
    }

    public static void main(String[] args) {
        askACar();
    }
}
