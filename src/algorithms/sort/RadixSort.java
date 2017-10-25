package algorithms.sort;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class RadixSort extends Sort {

    @Override
    protected int[] sort(int[] array) {
        return radixSort(array);
    }

    private int[] radixSort(int[] arr) {
        int maxBit = String.valueOf(max(arr)).length();// 获得最大位数
        for (int i = 1; i <= maxBit; i++) {// 最大位数决定再分配收集的次数
            List<List<Integer>> list = new ArrayList<List<Integer>>();
            // list 初始化，位数的值无外乎[0,9]，因此长度为10
            for (int n = 0; n < 10; n++) {
                list.add(new LinkedList<Integer>());
            }
            // 分配
            for (int a : arr) {
                list.get(getBitValue(a, i)).add(a);// 将原数组的元素的位数的值作为下标，整个元素的值作为下标的值
            }
            // 收集
            int k = 0;
            for (int j = 0; j < list.size(); j++) {
                if (!list.get(j).isEmpty()) {// 加一层判断，如果list的某个元素不为空，再进入下面的元素内部循环
                    for (int a : list.get(j)) {
                        arr[k++] = a;
                    }
                }
            }
        }
        return arr;
    }

    /**
     * 获得某数字的某一个位的值，例如543的十位数为4
     * 
     * @param target
     *            待处理数字
     * @param BitNum
     *            从右向左第几位数，例如142512，BitNum为3的话，对应的值为5
     * @return
     */
    private int getBitValue(int target, int BitNum) {
        String t = String.format("%" + BitNum + "d", target).replace(" ", "0");// 如果位数不够，则用0补位
        return Integer.valueOf(String.valueOf(t.charAt(t.length() - BitNum)));
    }
}