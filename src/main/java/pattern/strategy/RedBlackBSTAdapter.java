package pattern.strategy;

import algorithms.search.ST;
import algorithms.search.STImpl.RedBlackBST;

public class RedBlackBSTAdapter extends Strategy {

    @Override
    public void algorithm() {
        logger.info(this.getClass().getName());
        ST<Integer, String> st;
        st = new ST<Integer, String>(new RedBlackBST<Integer, String>());
        testST(st);
    }

}
