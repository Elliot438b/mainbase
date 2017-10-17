package algorithms.sort;

public class InsertSort extends Sort {
	public int[] sort(int[] array) {
		// 从第二张牌开始比较
		for (int i = 1; i < array.length; i++) {
			int target = array[i];
			int j = i;
			// 如果比前一个大，就把前一个放到当前目标牌的位置，把前一个的位置空出来，然后继续跟更前一个比较，循环去找到最准确的目标位置
			while (j > 0 && target > array[j - 1]) {
				array[j] = array[j - 1];
				j--;
			}
			// 在目标位置的插入操作
			array[j] = target;
		}
		return array;
	}
}
