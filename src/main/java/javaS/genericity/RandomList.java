package javaS.genericity;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RandomList<T> {
    private List<T> storage = new ArrayList<T>();
    // new Random(long seed); Random是个伪随机，通过seed随机规则来生成随机数。
    // seed可以理解为random生成随机数的规则，如果seed相同，那么random生成的随机数也肯定一样。
    // 如果不指定seed，则每次生成随机数的seed不同，那么random每一次执行生成的随机数也不同，真的随机了。
    private Random random = new Random();

    public void add(T item) {
        storage.add(item);
    }

    public T select() {
        return storage.get(random.nextInt(storage.size()));
    }

    public static void main(String[] args) {
        RandomList<String> rlist = new RandomList<String>();
        for (String a : "this is a value".split(" ")) {
            rlist.add(a);
        }
        for (int i = 0; i < rlist.storage.size(); i++) {
            System.out.println(rlist.select());
        }
    }
}