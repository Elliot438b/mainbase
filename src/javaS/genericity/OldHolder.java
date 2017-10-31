package javaS.genericity;

import algorithms.sort.QuickSort;

public class OldHolder {
    private Object a;

    public OldHolder(Object a) {
        this.a = a;
    }

    public void setA(Object a) {
        this.a = a;
    }

    public Object getA() {
        return a;
    }

    public static void main(String[] args) {
        OldHolder old = new OldHolder(new QuickSort());// 将OldHolder中的属性a赋值为QuickSort的实例
        old.setA(1);// 将a赋值为整型数字1
        old.setA("this is a value");// 将a赋值为String类型字符串
        System.out.println(old.getA());
    }
}
