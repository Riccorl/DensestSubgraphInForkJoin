package it.ric.uny.densestsubgraph.parallel;

import it.ric.uny.densestsubgraph.Edge;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.RecursiveTask;

public class ParallelRemove extends RecursiveTask<ArrayList<Edge>> {

    //private static final int CUTOFF = 50000;

    private ArrayList<Edge> edges;
    private Map<Integer, Set<Integer>> degreeS;
    private int start;
    private int end;
    private float threshold;
    private int cutoff;

    public ParallelRemove(ArrayList<Edge> edges, Map<Integer, Set<Integer>> degreeS,
        float threshold, int cutoff) {

        this.edges = edges;
        this.end = edges.size();
        this.degreeS = degreeS;
        this.threshold = threshold;
        this.cutoff = cutoff;
    }

    private ParallelRemove(ArrayList<Edge> edges, Map<Integer, Set<Integer>> degreeS, int start,
        int end, float threshold, int cutoff) {

        this.edges = edges;
        this.degreeS = degreeS;
        this.start = start;
        this.end = end;
        this.threshold = threshold;
        this.cutoff = cutoff;
    }

    @Override
    protected ArrayList<Edge> compute() {

        // Sequential
        if (end - start < cutoff) {
            ArrayList<Edge> newEdge = new ArrayList<>();
            for (int i = start; i < end; i++) {
                Edge edge = edges.get(i);
                int u = edge.getU();
                int v = edge.getV();

                if (degreeS.get(u).size() > threshold && degreeS.get(v).size() > threshold) {
                    newEdge.add(edge);
                }
            }
            return newEdge;
        }

        // Parallel
        int mid = (start + end) / 2;

        ParallelRemove left = new ParallelRemove(edges, degreeS, start, mid, threshold, cutoff);
        ParallelRemove right = new ParallelRemove(edges, degreeS, mid, end, threshold, cutoff);

        left.fork();
        ArrayList<Edge> rightArray = right.compute();
        ArrayList<Edge> leftArray = left.join();
        leftArray.addAll(rightArray);

        return leftArray;
    }
}