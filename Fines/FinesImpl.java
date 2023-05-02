// Alex Barker (23152009)

/**
 * An implementation of the Fines problem from the 2022 CITS2200 Project
 */
public class FinesImpl implements Fines {
    long totalCount = 0;
    /**
     * {@inheritdoc}
     */
    public long countFines(int[] priorities) {
        totalCount = 0;
        mergeSort(priorities, 0, priorities.length - 1);
        return totalCount;
    }

    /**
     * Executes the merge sort algorithm sorting the argument array. 
     * Mutates the inputted array, no return.
     *
     * @param a the array of long integers to be sorted
     * @param p the start index
     * @param r the end index
     */
    private void mergeSort(int[] a, int p, int r) {
        if (p < r) {
            int q = p + (r - p) / 2; // midpoint

            // sort the halves
            mergeSort(a, p, q);
            mergeSort(a, q + 1, r);

            // merge halves
            merge(a, p, q, r);
        }
    }

    private void merge(int[] a, int p, int q, int r) {
        // sizes of split arrays
        int n1 = q - p + 1;
        int n2 = r - q;

        // create arrays
        int L[] = new int[n1];
        int R[] = new int[n2];

        // put data in arrays
        for (int i = 0; i < n1; i++) {
            L[i] = a[p + i];
        }
        for (int j = 0; j < n2; j++) {
            R[j] = a[q + j + 1];
        }

        // merge the two arrays - walks along both arrays adding the smallest number
        // if L[i] is under R[j] then add that to array, otherwise add r[J] to the array

        int higher = 0;

        int i = 0, j = 0, k = p;
        while (i < n1 && j < n2) {
            if (L[i] < R[j]) {
                a[k++] = L[i++];
                higher++; // increment temp variable if left array value is added
            } else {
                totalCount += higher; // increase the count otherwise as that is the number of smaller values on the left
                a[k++] = R[j++];
            }
        }
        // occurs after an array is exhausted, the rest of the numbers will be higher
        // than whats in the array so just add them in
        while (i < n1) {
            a[k++] = L[i++];
        }
        while (j < n2) {
            totalCount += higher;
            a[k++] = R[j++];
        }
    }
}