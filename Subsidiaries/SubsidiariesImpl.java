import java.util.*;

/**
 * An implementation of the Subsidiaries problem from the 2022 CITS2200 Project
 * @author Alex Barker (23152009)
 */

public class SubsidiariesImpl implements Subsidiaries {
    /**
     * {@inheritdoc}
     */
    public int[] sharedOwners(int[] owners, Query[] queries) {
        int[] result = new int[queries.length];
        CompanyTree tree = new CompanyTree(owners);
        // O(nlogn)
        tree.setupSparseTable(owners.length + 1);

        // query the sparse table with complexity O(1) for each query, O(q) total
        for (int i = 0; i < queries.length; i++) 
            result[i] = tree.findLCASparse(queries[i].payee, queries[i].payer);
        

        return result;
    }

    private class CompanyNode {
        int id;
        ArrayList<CompanyNode> subsidaries;

        CompanyNode(int id) {
            this.id = id;
            subsidaries = new ArrayList<>();
        }
    }

    /**
     * Used in the non-recursive DFS
     */
    private class DepthNodePair {
        public CompanyNode node;
        public int depth;

        public DepthNodePair(CompanyNode node, int depth) {
            this.node = node;
            this.depth = depth;
        }
    }

    /**
     * A tree to represent each company and its owner as the parent
     */
    private class CompanyTree {

        private CompanyNode root;
        public CompanyTree(int[] owners) {
            root = new CompanyNode(-1);

            CompanyNode[] nodes = new CompanyNode[owners.length + 1];
            nodes[0] = root;
            for (int i = 0; i < owners.length; i++) 
                nodes[i + 1] = new CompanyNode(i);
            
            for (int i = 0; i < owners.length; i++) 
                nodes[owners[i] + 1].subsidaries.add(nodes[i + 1]);
            
        }

        private int[] tour;
        private int[] lastOccurance;
        private int[] depths;
        private int[] companyIDs; // convert DFS indicies to company IDs

        private SparseTable sparseTable;

        /**
         * Precompute the sparse table
         * @param numNodes the number of nodes
         */
        public void setupSparseTable(int numNodes) {
            int tourSize = 2 * numNodes - 1;
            tour = new int[tourSize];
            companyIDs = new int[numNodes];
            depths = new int[tourSize];
            lastOccurance = new int[numNodes];
            // convert company ID's to DFS indicies, as the sparse table only works with DFS/BFS indicies
            int[] dfsIndexes = new int[numNodes]; 

            // non-recursive version of DFS for eularian tour
            Deque<DepthNodePair> stack = new ArrayDeque<DepthNodePair>();
            stack.push(new DepthNodePair(root, 0));
            int tourIndex = 0;
            int dfsIndex = 0;

            boolean[] visited = new boolean[numNodes + 1];
            while (!stack.isEmpty()) {
                DepthNodePair other = stack.pop();

                if (!visited[other.node.id + 1]) {
                    dfsIndexes[other.node.id + 1] = dfsIndex;
                    companyIDs[dfsIndex] = other.node.id;
                    dfsIndex++;
                }

                tour[tourIndex] = dfsIndexes[other.node.id + 1];
                depths[tourIndex] = other.depth;
                lastOccurance[other.node.id + 1] = tourIndex;
                tourIndex++;

                if (!visited[other.node.id + 1]) {
                    for (int i = other.node.subsidaries.size() - 1; i >= 0; i--) {
                        stack.push(other);
                        stack.push(new DepthNodePair(other.node.subsidaries.get(i), other.depth + 1));
                    }
                }
                visited[other.node.id + 1] = true;
            }

            sparseTable = new SparseTable(tour);
        }

        /**
         * Queries the sparse table to find the LCA between two nodes
         * @param node1 The first company ID
         * @param node2 The second company ID
         * @return the index of the Lowest Common Ancestor of node1 & node2
         */
        public int findLCASparse(int node1, int node2) {
            int l = Math.min(lastOccurance[node1 + 1], lastOccurance[node2 + 1]);
            int r = Math.max(lastOccurance[node1 + 1], lastOccurance[node2 + 1]);
            // find the DFS index, then find its corresponding company id 
            return companyIDs[tour[sparseTable.query(l, r)]];
        }
    }

    /**
     * 
     */
    private class SparseTable {
        // stores the minimum value in the range
        private int[][] sparseTable;

        // stores the index of the minimum value in the range
        private int[][] indexTable;

        public SparseTable(int[] values) {
            // this is the maximum power of 2 needed
            int numRows = (int) (Math.log(values.length) / Math.log(2)); // floor(log2(n)) using some log laws
            sparseTable = new int[numRows + 1][values.length];
            indexTable = new int[numRows + 1][values.length];

            for (int i = 0; i < values.length; i++) {
                sparseTable[0][i] = values[i];
                indexTable[0][i] = i;
            }

            // Build sparse table combining the values of the previous intervals.
            for (int i = 1; i <= numRows; i++) {
                // find min value for intervals 2^i
                for (int j = 0; j + (1 << i) <= values.length; j++) {
                    int leftInterval = sparseTable[i - 1][j];
                    int rightInterval = sparseTable[i - 1][j + (1 << (i - 1))];
                    sparseTable[i][j] = Math.min(leftInterval, rightInterval);

                    // Propagate the index of the best value
                    if (leftInterval <= rightInterval)
                        indexTable[i][j] = indexTable[i - 1][j];
                    else
                        indexTable[i][j] = indexTable[i - 1][j + (1 << (i - 1))];
                }
            }
        }

        // Returns the index of the minimum element in the range [l, r].
        public int query(int left, int right) {
            // highest power of 2 <= elements in range
            int power = (int) (Math.log(right - left + 1) / Math.log(2));
            if (sparseTable[power][left] <= sparseTable[power][right - (1 << power) + 1])
                return indexTable[power][left];
            return indexTable[power][right - (1 << power) + 1];
        }
    }
}
