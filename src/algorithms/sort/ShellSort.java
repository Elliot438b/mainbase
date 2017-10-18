package algorithms.sort;

public class ShellSort extends Sort {

	@Override
	protected int[] sort(int[] array) {
		int lastStep = 0;// ����ѭ��������������һ��step�������ظ�
		for (int d = 2; d < array.length; d++) {
			int step = array.length / d;
			if (lastStep != step) {
				lastStep = step;
			} else {
				break;
			}
			System.out.println(step);// ���step��shellSortִ�д���
			shellSort(array, step);
		}
		return array;
	}

	private void shellSort(int[] array, int step) {
		for (int i = 0; i < array.length - step; i++) {
			if (array[i] < array[i + step]) {
				swap(array, i, i + step);
			}
		}
	}

}
