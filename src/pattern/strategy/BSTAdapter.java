package pattern.strategy;

import algorithms.search.ST;
import algorithms.search.STImpl.BST;

public class BSTAdapter extends Strategy {
    @Override
    public void algorithm() {
        logger.info(this.getClass().getName());
        ST<Integer, String> st;
        st = new ST<Integer, String>(new BST<Integer, String>());
        testST(st);
    }

}
