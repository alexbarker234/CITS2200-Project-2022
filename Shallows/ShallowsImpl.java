import java.util.ArrayList;
import java.util.LinkedList;
import java.util.PriorityQueue;

// Alex Barker (23152009)

/**
 * An implementation of the Shallows problem from the 2022 CITS2200 Project
 */
public class ShallowsImpl implements Shallows {
    /**
     * {@inheritdoc}
     */
    public int[] maximumDraughts(int ports, Lane[] lanes, int origin) {
        // convert every lane into an adjacency list as its faster to loop through than adjacency matrix
        ArrayList<LinkedList<Edge>> adjacencies = new ArrayList<LinkedList<Edge>>(ports);
        for (int i = 0; i < ports; i++) 
            adjacencies.add(new LinkedList<Edge>());    
        for (Lane lane : lanes) 
            adjacencies.get(lane.depart).add(new Edge(lane.arrive,  lane.depth));
        

        boolean visited[] = new boolean[ports];
        int[] maxDraughts = new int[ports];
        maxDraughts[origin] = Integer.MAX_VALUE;

        PriorityQueue<Edge> queue = new PriorityQueue<>();
        queue.add(new Edge(origin, Integer.MAX_VALUE));
        while (queue.size() != 0) {
            int u = queue.poll().index;
            if (visited[u]) continue; // if its already been visited, skip searching
            visited[u] = true;

            // loop through each connected edge
            for (Edge neighbour : adjacencies.get(u)) {
                if (visited[neighbour.index])
                    continue;
                // get the largest depth that could fit through up to this point
                int num = Math.min(maxDraughts[u], neighbour.depth);
                if (num != 0 && num > maxDraughts[neighbour.index]) {
                    maxDraughts[neighbour.index] = num;
                    queue.add(neighbour);
                }
            }
        }
        return maxDraughts;
    }

    /**
     * Custom comparable class to represent an edge and the depth to its vertex
     * ---depth----> index
     * Does not store the vertex it comes from
     */
    private class Edge implements Comparable<Edge> {
        public int index;
        public int depth;

        public Edge(int index, int depth) {
            this.index = index;
            this.depth = depth;
        }

        public int compareTo(Edge other) {
            if (this.depth < other.depth)
                return 1;
            else if (this.depth > other.depth)
                return -1;
            else
                return 0;
        }
    }
}