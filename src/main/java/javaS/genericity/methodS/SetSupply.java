package javaS.genericity.methodS;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

/**
 * Set通过使用泛型方法来封装Set的自有方法。
 * 
 * @author Evsward
 *
 */
public class SetSupply {
    // 求集合A、B的并集
    public static <T> Set<T> union(Set<T> setA, Set<T> setB) {
        Set<T> result = new HashSet<T>(setA);// 不要直接操作setA，请保持setA的纯真
        result.addAll(setB);
        return result;
    }

    // 求集合A、B的交集
    public static <T> Set<T> intersection(Set<T> setA, Set<T> setB) {
        Set<T> result = new HashSet<T>(setA);
        result.retainAll(setB);
        return result;
    }

    // 求集合A、B的差集
    public static <T> Set<T> difference(Set<T> setA, Set<T> setB) {
        Set<T> result = new HashSet<T>(setA);
        result.removeAll(setB);
        return result;
    }

    // 求集合A、B的并集-集合A、B的交集
    public static <T> Set<T> complement(Set<T> setA, Set<T> setB) {
        return difference(union(setA, setB), intersection(setA, setB));
    }

    public static void main(String[] args) {
        Set<Colors> setA = EnumSet.range(Colors.Red, Colors.Orange);
        Set<Colors> setB = EnumSet.range(Colors.Black, Colors.Blue);
        System.out.println(SetSupply.union(setA, setB));
        System.out.println(SetSupply.intersection(setA, setB));
        System.out.println(SetSupply.difference(setA, setB));
        System.out.println(SetSupply.complement(setA, setB));
    }
}
