package pattern.bridge;

import pattern.bridge.taolu.SpeakIn4S;

public abstract class SSSS {
    protected SpeakIn4S ops;// 通过对象的组合关系，将4S店类与问话操作关联起来。

    public void setOps(SpeakIn4S ops) {
        this.ops = ops;
    }

    public abstract void ask();// 增加一个用于调用具体操作的抽象方法。
}
