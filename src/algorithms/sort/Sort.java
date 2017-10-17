package algorithms.sort;

public abstract class Sort {
	protected int[] sort(int[] array) {
		return null;
	};

	/**
	 * 互换位置的方法
	 * 
	 * @param array
	 *            要换位置的目标数组
	 * @param i
	 *            数组位置1
	 * @param j
	 *            数组位置2
	 * @return 换好位置以后的数组
	 */
	protected int[] swap(int[] array, int i, int j) {
		int t = array[i];
		array[i] = array[j];
		array[j] = t;
		return array;
	}

	protected void show(int[] array) {
		for (int a : array) {
			System.out.println(a);
		}
	}
}
