package it.ric.uny.densestsubgraph.parallel;

import it.ric.uny.densestsubgraph.model.Edge;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.RecursiveTask;

public class ParallelRemove extends RecursiveTask<List<Edge>> {

    private static final int CUTOFF = 40000;

    private List<Edge> edges;
    private Map<Integer, Integer> degreeS;
    private int start;
    private int end;
    private double threshold;

    public ParallelRemove(List<Edge> edges, Map<Integer, Integer> degreeS,
        double threshold) {

        this.edges = edges;
        this.end = edges.size();
        this.degreeS = degreeS;
        this.threshold = threshold;
    }

    private ParallelRemove(List<Edge> edges, Map<Integer, Integer> degreeS, int start,
        int end, double threshold) {

        this.edges = edges;
        this.degreeS = degreeS;
        this.start = start;
        this.end = end;
        this.threshold = threshold;
    }

    @Override
    protected List<Edge> compute() {

        // Sequential
        if (end - start < CUTOFF) {
            List<Edge> newEdges = new ArrayList<>(end - start);
            for (int i = start; i < end; i++) {
                Edge edge = edges.get(i);
                int u = edge.getU();
                int v = edge.getV();

                if (degreeS.get(u) > threshold && degreeS.get(v) > threshold) {
                    newEdges.add(edge);
                }
            }
            return newEdges;
        }

        // Parallel
        int mid = (start + end) / 2;

        ParallelRemove left = new ParallelRemove(edges, degreeS, start, mid, threshold);
        ParallelRemove right = new ParallelRemove(edges, degreeS, mid, end, threshold);

        left.fork();
        List<Edge> rightArray = right.compute();
        List<Edge> leftArray = left.join();
        leftArray.addAll(rightArray);

        return leftArray;
    }
}