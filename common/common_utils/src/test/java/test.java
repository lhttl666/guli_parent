import java.util.Arrays;

public class test {
    public static void main(String[] args) {
        int[] arr = {3, 5, 8, 0, 2, 4, 1, 6, 89, 55, 23, 66, 90, 18, 52, 83, 99, 43, 7, 2, -3, 9, -5, 898,-81};
        mergeSort02(arr);

        System.out.println(Arrays.toString(arr));
    }

    public static void mergeSort(int[] arr) {
        if (arr == null || arr.length < 2) {
            return;
        }
        process(arr, 0, arr.length - 1);
    }

    public static void process(int[] arr, int l, int r) {
        if (l >= r) {
            return;
        }
        int m = l + ((r - l) >> 1);
        process(arr, l, m);
        process(arr, m + 1, r);
        merge(arr, l, m, r);
    }

    public static void merge(int[] arr, int l, int m, int r) {
        int p1 = l;
        int p2 = m + 1;
        int[] help = new int[r - l + 1];
        int i = 0;
        while (p1 <= m && p2 <= r) {
            help[i++] = arr[p1] <= arr[p2] ? arr[p1++] : arr[p2++];
        }
        while (p1 <= m) {
            help[i++] = arr[p1++];
        }
        while (p2 <= r) {
            help[i++] = arr[p2++];
        }
        for (int j = 0; j < help.length; j++) {
            arr[l + j] = help[j];
        }
    }

    public static void mergeSort02(int[] arr) {
        if (arr == null || arr.length < 2) {
            return;
        }
        int mergeSize = 1;
        int N = arr.length;
        while (mergeSize < N) {
            int l = 0;
            while (l < N) {
                int m = l + mergeSize - 1;
                if (m >= N) {
                    break;
                }
                int r = Math.min(m + mergeSize, N - 1);
                merge(arr, l, m, r);
                l = r + 1;
            }
            mergeSize <<= 1;
        }
    }


    public static void swap(int[] arr, int i, int j) {
        int temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
    }

}
