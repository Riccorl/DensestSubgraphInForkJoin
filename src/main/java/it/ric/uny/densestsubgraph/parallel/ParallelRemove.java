package it.ric.uny.densestsubgraph.parallel;

import it.ric.uny.densestsubgraph.Edge;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.RecursiveAction;

public class ParallelRemove extends RecursiveAction {

    private static final int CUTOFF = 50000;

    private ArrayList<Edge> edges;
    ArrayList<Edge> edgesRemoved;
    private Set<Integer> s;
    Map<Integer, Integer> degreeS;
    private int start;
    private int end;
    private float threshold;

    public ParallelRemove(ArrayList<Edge> edges, ArrayList<Edge> edgesRemoved, Set<Integer> s, Map<Integer, Integer> degreeS,
        float threshold) {

        this.edges = edges;
        this.edgesRemoved = edgesRemoved;
        this.s = s;
        this.degreeS = degreeS;
        this.end = edges.size();
        this.threshold = threshold;
    }

    private ParallelRemove(ArrayList<Edge> edges,
                            ArrayList<Edge> edgesRemoved,
                            Set<Integer> s,
                            Map<Integer, Integer> degreeS,
                            int start,
                            int end,
                            float threshold) {

        this.edges = edges;
        this.edgesRemoved = edgesRemoved;
        this.s = s;
        this.degreeS = degreeS;
        this.start = start;
        this.end = end;
        this.threshold = threshold;
    }

    @Override
    protected void compute() {

        // Sequential
        if (end - start < CUTOFF) {
            for (int i = start; i < end; i++) {
                Edge edge = edgesRemoved.get(i);
                int u = edge.getU();
                int v = edge.getV();

                if (degreeS.get(u) <= threshold) {
                    s.remove(u);
                    edges.remove(edge);
                }
                if (degreeS.get(v) <= threshold) {
                    s.remove(v);
                    edges.remove(edge);
                }
            }
            return;
        }

        // Parallel
        int mid = (start + end) / 2;

        ParallelRemove left = new ParallelRemove(edges, edgesRemoved, s, degreeS, start, mid, threshold);
        ParallelRemove right = new ParallelRemove(edges, edgesRemoved, s, degreeS, mid, end, threshold);

        left.fork();
        right.compute();
        left.join();
    }
}
