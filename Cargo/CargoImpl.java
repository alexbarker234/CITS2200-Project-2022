
/**
 * An implementation of the Cargo problem from the 2022 CITS2200 Project
 * @author Alex Barker (23152009)
 */
public class CargoImpl implements Cargo {
    /**
     * {@inheritdoc}
     */
    public int[] departureMasses(int stops, Query[] queries) {
        int[] departureMasses = new int[queries.length];
        FenwickTree tree = new FenwickTree(stops);
        for (int q = 0; q < queries.length; ++q) {
            Query query = queries[q];
            tree.updateRange(query.collect, query.deliver - 1, query.cargoMass);
            departureMasses[q] = tree.pointQuery(query.collect);
        }
        return departureMasses;
    }

    /**
     * An implementation of a Fenwick Tree/Binary Indexed Tree to execute range updates and point queries
     */
    private class FenwickTree{
        private int treeArray[];
        /**
         * Construct a new empty Fenwick Tree
         * @param arraySize the original arrays size
         */
        public FenwickTree(int arraySize){
            treeArray = new int[arraySize + 1]; // n + 1, index 0 is ignored as Fenwick Trees are 1-indexed
        }

        /**
         * Returns the value at the index in O(logn) time
         * @param index the index you want to get the value of
         * @return the value at the index
         */
        public int pointQuery(int index){
            int sum = 0;
            // increments index as it is 1-indexed and the original is 0-indexed
            // flips the last set bit, using the prefix totals in the tree array
            // O(logn) - repeats however many 1 bits there are in index
            for (++index; index > 0; index -= index & -index)
                sum += treeArray[index];  
            return sum;
        }

        /**
         * Adds to the Fenwick Tree within a range in O(logn) time
         * @param startIndex the start of the range (inclusive)
         * @param endIndex the end of the range (inclusive)
         * @param value the value to add in the range
         */
        public void updateRange(int startIndex, int endIndex, int value) {
            // this affects the entire array from startIndex onwards
            pointUpdate(startIndex, value);
            // counter above by subtracting it from the end of the range onwards
            pointUpdate(endIndex + 1, -value);
        }

        /**
         * Adds a value to an index, propagating the change to the right in O(logn) time
         * @param index the index in which to increase the value
         * @param value the amount to increase the index by
         */
        public void pointUpdate(int index, int value) {
            // increments index as it is 1-indexed and the original is 0-indexed
            // adds the least significant bit to the index
            for (++index; index < treeArray.length; index += index & -index)
                treeArray[index] += value;       
        }
    }
}