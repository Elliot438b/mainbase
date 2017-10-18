package algorithms.sort;

public class MergeSort extends Sort {

	private int[] temp;

	@Override
	protected int[] sort(int[] array) {
		temp = new int[array.length];// �½�һ����ԭ���鳤����ͬ�Ŀյĸ�������
		divide(array, 0, array.length - 1);
		return array;
	}

	private void divide(int[] array, int left, int right) {
		if (left >= right)// ���ε���ϸ��
			return;
		int mid = (right + left) / 2;
		divide(array, left, mid);// ��벿�ֵݹ����
		divide(array, mid + 1, right);// �Ұ벿�ֵݹ����
		merge(array, left, mid, right);
	}

	private void merge(int[] array, int left, int mid, int right) {
		// �������� [left, mid]�� �������� [mid+1, right]
		int i = left;
		int j = mid + 1;
		for (int k = left; k <= right; k++) {// ��left��right���Ƶ�temp������
			temp[k] = array[k];
		}
		for (int k = left; k <= right; k++) {// ͨ���жϣ������������е�ֵ���մ�С�鲢��ԭ����
			if (i > mid)// �����þ�����ȡ�Ұ��Ԫ��
				array[k] = temp[j++];
			else if (j > right)// �Ұ���þ�����ȡ����Ԫ��
				array[k] = temp[i++];
			else if (array[j] > temp[i])// �Ұ�ߵ�ǰԪ�ش������ߵ�ǰԪ�أ�ȡ�Ұ��Ԫ�أ��Ӵ�С����
				array[k] = temp[j++];
			else// ���ߵ�ǰԪ�ش����Ұ�ߵ�ǰԪ�أ�ȡ����Ԫ�أ��Ӵ�С����
				array[k] = temp[i++];
		}
	}
}