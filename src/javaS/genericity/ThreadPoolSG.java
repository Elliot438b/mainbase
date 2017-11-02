package javaS.genericity;

import javaS.genericity.interfaceS.Generator;
import javaS.genericity.methodS.BasicGenerator;

/**
 * 举个栗子：线程池中有线程组，线程组中有三个元素。
 * 
 * @author Evsward
 *
 */
public class ThreadPoolSG {
    private TupleList<Integer, Thread, String> threadPool = new TupleList<Integer, Thread, String>();

    private int countId;

    // 使用生成器来生成线程组
    private class ThreadTupleGenerator implements Generator {

        @Override
        public ThreeTuple<Integer, Thread, String> next() {
            return new ThreeTuple(countId++, new Thread(), "xx" + countId);
        }

    }

    /**
     * 外部只提供该方法生成指定数量的线程池
     * 
     * @param n
     *            指定线程池的大小
     * @return
     */
    public TupleList<Integer, Thread, String> getThreadPool(int n) {
        // 先清空
        for (int i = 0; i < threadPool.size(); i++)
            threadPool.remove(i);
        for (int i = 0; i < n; i++)
            threadPool.add(new ThreadTupleGenerator().next());
        return threadPool;
    }

    public static void main(String[] args) {
        ThreadPoolSG tpsg = new ThreadPoolSG();
        for (ThreeTuple<Integer, Thread, String> t : tpsg.getThreadPool(5))
            System.out.println(t);
    }
}
