package algorithms.sort;

public class HeapSort extends Sort {
	public int[] sort(int[] array) {
		int size = array.length;
		for (int k = size / 2 - 1; k >= 0; k--) {// �����һ�����ڵ����ر������и��ڵ㣬ֱ�����ڵ�
			// sink(array, k);
			swim(array, k);
		}
		while (size > 1) {
			swap(array, 0, --size);// �����Ѷ������һ��Ԫ�أ���ÿ�ν�ʣ��Ԫ���е�����߷ŵ������
			// sink(array, 0);
			swim(array, 0);
		}
		return array;
	}

	/**
	 * �󶥶� �³�����Ŀ�꣺ ����Ҷ�ڵ㣨����ˣ� ���� �����������ӽڵ����ĸ��ڵ�λ��
	 * 
	 * @param array
	 *            ��ˮ�
	 * @param k
	 *            ָ��λ�õĽڵ�����³�����
	 */
	private void sink(int[] array, int k) {
		int size = array.length;
		while (k <= size / 2 - 1) {// ���ڵ�������������˵���Ѿ���Ҷ�ڵ㣬���Ѵ��Ŀ��1
			int i = 2 * k + 1;
			int fatherNode = array[k];// ��Ӧ�����ӽڵ�Ϊ��array[i]���Һ�array[i + 1]
			if (i < size && array[i] < array[i + 1])
				i++;
			// ��������ǲ�ȷ���󺢴����Һ��󣬵���array[i]�϶��Ǵ�
			if (fatherNode > array[i]) {
				break;// ���ڵ�ȴ󺢻����Ѵ��Ŀ��2
			} else {// ���򸸽ڵ�ʹ󺢽���λ�á�
				swap(array, i, k);
			}
		}
	}

	/**
	 * С���� �ϸ�����ָ���ڵ�ȸ��ڵ㻹С��ʱ��Ҫ����λ�ã���С���Ƶ��ϲ㡣
	 * 
	 * @param array
	 * @param k
	 *            (k + 1) / 2�� Ϊָ���ڵ�ĸ��ڵ�λ��
	 */
	private void swim(int[] array, int k) {
		while (k > 0 && array[(k + 1) / 2] < array[k]) {
			swap(array, (k + 1) / 2, k);
			k = (k + 1) / 2;
		}
	}

}
