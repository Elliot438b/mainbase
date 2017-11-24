package pattern.adapter;

import algorithms.sort.QuickSort;
import algorithms.sort.Sort;

public class GradeAdapter extends Grade {
    private Sort s = new QuickSort();

    @Override
    public int[] getRankList(int[] index) {
        index = s.sort(index);
        s.show(index);
        return index;
    }

}
