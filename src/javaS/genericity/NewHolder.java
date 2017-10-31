package javaS.genericity;

import algorithms.sort.QuickSort;

public class NewHolder<T> {

    private T a;

    public NewHolder(T a) {
        this.a = a;
    }

    public void setA(T a) {
        this.a = a;
    }

    public T getA() {
        return a;
    }

    public static void main(String[] args) {
        NewHolder<Integer> n = new NewHolder<Integer>(100);// 使用的时候将泛型定义为整型，那么只能限制设置a为整型值
        n.setA(1);// 将a赋值为整型数字1
        System.out.println(n.getA());
    }

}
