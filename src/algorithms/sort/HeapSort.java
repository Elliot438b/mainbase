package algorithms.sort;

public class HeapSort extends Sort {

	/**
	 * �³���������k��ֵarray[k]�³�������λ�ã������߾�Ҫ���ӽڵ�Ƚ�
	 * kλ�õ���ֵ�����˴󶥶�����״̬��˵������ӽڵ㻹С����ʱ��Ҫ��k��ϴ���ӽڵ㻥��λ��
	 * �����ÿ��Ǳȸ��ڵ������⣬��Ϊѭ������鸸�ڵ��ʱ�����ɿ��Բ�������ӽڵ�С���߼���
	 *     		 7
	 *        /    \
	 * 		6		  3
	 *    /  \      /  \
	 *   4    5   1    2
	 * @param array
	 * @param k Ŀ��λ��
	 * @param right ����[k,right]
	 */
	private void sink(int[] array, int k, int right) {
		// ѭ����ֹ����1�����Ӳ�������,˵��kĿǰ��ΪҶ�ڵ�,�޴��ɳ�
		while (2 * k + 1 <= right) {
			int bigChildIndex = 2 * k + 1;// left child index:2 * k + 1��right child index:2 * k + 2
			// ��������ӽڵ�,�����Ӵ�������
			if (2 * k + 2 <= right && array[2 * k + 1] < array[2 * k + 2])
				bigChildIndex = 2 * k + 2;
			if (array[k] > array[bigChildIndex])
				// ѭ����ֹ����2��k��ֵ����λ���Ѷ�����,�޴��ɳ�,Ҳ����˵�������ӽڵ㣨һ�����������ӽڵ㣩����
				break;
			swap(array, k, bigChildIndex);// �³�������k��bigChildIndex
			k = bigChildIndex;// λ��k�Ѿ�������bigChildIndex
		}
	}

	/**
	 * �ϸ���������Ŀ��λ�õ�ֵ�ϸ�������λ�ã������߾�Ҫ�����ڵ�Ƚ�
	 * kλ�õ���ֵ������С��������״̬��˵����ȸ��ڵ㻹С����ʱ��Ҫ��k���丸�ڵ㻥��λ��
	 * �����ÿ��Ǳ��ӽڵ������⣬��Ϊѭ��������ӽڵ��ʱ�����ɿ��Բ�����ȸ��ڵ�С���߼���
	 * ������³��������ϸ������Ƚϼ��Ե�ԭ����kֻ��Ҫ��һ�����ڵ�Ƚϴ�С�����³���������Ҫ��һ���������ӽڵ�Ƚϴ�С����������ⲿ���߼�
	 *     		  1
	 *        /     \
	 * 	    2		  5
	 *    /  \      /  \
	 *   4    3   6    7 
	 * @param array
	 * @param k ����[0,k]
	 */
	private void swim(int[] array, int k) {
		if (k == 0)
			return;// k��λ���Ѿ��Ǹ��ڵ��ˣ�����Ҫ���ϸ��ˡ�
		// @@@@ ��ֹ����:k���������ڵ�һ�������������������ڵ㣨k==0����������;k�ҵ��˱ȸ��ڵ���λ�ã�����С���ѹ��������Ѿ�������
		while (k > 0 && array[k] < array[(k - 1) / 2]) {// k�ĸ��ڵ㣺(k - 1) / 2
			swap(array, k, (k - 1) / 2);// �ϸ�
			k = (k - 1) / 2;// k���������ĸ��ڵ��λ��
		}
	}

	/**
	 * �������³�������
	 * ע�⣺ͨ���³��������Եõ��󶥶�Ҳ���Եõ�С���ѣ�����ֻ����һ����������ܡ�
	 * @param array
	 * @return ��С��������
	 */
	private int[] sinkSort(int[] array) {
		int maxIndex = array.length - 1;// ����array������Ϊ [0,maxIndex]
		// �����
		int lastParentIndex = (maxIndex - 1) / 2;// ���һ�����ڵ�λ��
		// @@@@���ʹ���³�������һ��Ҫ�����һ�����ڵ㿪ʼ�����ڵ㵹���飬���ܱ�֤���ֵ���͵����ڵ�@@@@
		for (int i = lastParentIndex; i >= 0; i--) {// ����[0,lastParentIndex]Ϊ��ǰ�����ȫ�����ڵ�����
			sink(array, i, maxIndex);// ����[lastParentIndex,maxIndex]�������һ�����ڵ㿪ʼ��飬�³�����������������
		}
		System.out.println("the max one is "+array[0]);
		// �������ע�⣺������=�����򣬶�����ֻ�ܱ�֤���ڵ�����ֵ�������ܱ�֤�ӽڵ㼰��֦�ڵ�ͬ����Ĵ�С˳��
		while (maxIndex > 0) {
			swap(array, 0, maxIndex--);// ȡ�����ֵ
			sink(array, 0, maxIndex);// �޸���
		}
		return array;
	}
	
	/**
	 * ������ͨ���ϸ�������ʹ������
	 * @param array
	 * @param len ����[0,len]����Ķ�����
	 */
	private void headAdjustBySwim(int[] array, int len) {
		// @@@@���ʹ���ϸ�������һ��Ҫ�����һ��Ҷ�ڵ㿪ʼ�������ڵ�λ�ü�飬���ܱ�֤��Сֵ���͵����ڵ�@@@@
		for (int i = len; i > 0; i--) {// i����Ҫ���=0���������Ϊ���ڵ�û�и��ڵ��ˡ�
			swim(array, i);// ����[0,i]�������һ��Ҷ�ڵ㿪ʼ��飬�ϸ�����������������
		}
	}
	/**
	 * �������ϸ�������
	 * ע�⣺ͨ���ϸ��������Եõ��󶥶�Ҳ���Եõ�С���ѣ�����ֻ����һ����������ܡ�
	 * @param array
	 * @return �Ӵ�С����
	 */
	private int[] swimSort(int[] array) {
		int maxIndex = array.length - 1;// ����array������Ϊ [0,maxIndex]
		headAdjustBySwim(array, maxIndex) ;
		System.out.println("the min one is "+array[0]);
		// �������ע�⣺������=�����򣬶�����ֻ�ܱ�֤���ڵ�����ֵ�������ܱ�֤�ӽڵ㼰��֦�ڵ�ͬ����Ĵ�С˳��
		while (maxIndex > 0) {
			swap(array, 0, maxIndex--);// ȡ����Сֵ
			headAdjustBySwim(array, maxIndex) ;
		}
		return array;
	}

	@Override
	protected int[] sort(int[] array) {
		return swimSort(array);
	}
	
	
}