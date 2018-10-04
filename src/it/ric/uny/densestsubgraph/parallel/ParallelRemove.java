package it.ric.uny.densestsubgraph.parallel;

import it.ric.uny.densestsubgraph.model.Edge;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.RecursiveTask;

public class ParallelRemove extends RecursiveTask<List<Edge>> {

    private static final int CUTOFF = 50000;

    private List<Edge> edges;
    private Map<Integer, Integer> degreeS;
    private int start;
    private int end;
    private double threshold;

    public ParallelRemove(List<Edge> edges,
        Map<Integer, Integer> degreeS, double threshold) {
        this.edges = edges;
        this.end = edges.size();
        this.degreeS = degreeS;
        this.threshold = threshold;
    }

    private ParallelRemove(List<Edge> edges,
        Map<Integer, Integer> degreeS, int start, int end, double threshold) {
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
            for (var i = start; i < end; i++) {
                var edge = edges.get(i);
                var u = edge.getU();
                var v = edge.getV();
                if (degreeS.get(u) > threshold && degreeS.get(v) > threshold)
                    newEdges.add(edge);
            }
            return newEdges;
        }
        // Parallel
        int mid = (start + end) / 2;
        var left = new ParallelRemove(edges, degreeS, start, mid, threshold);
        var right = new ParallelRemove(edges, degreeS, mid, end, threshold);

        left.fork();
        var rightArray = right.compute();
        var leftArray = left.join();
        leftArray.addAll(rightArray);
        return leftArray;
    }
}