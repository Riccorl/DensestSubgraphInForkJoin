package it.ric.uny.densestsubgraph.parallel;

import com.google.common.collect.Streams;
import it.ric.uny.densestsubgraph.model.Edge;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.RecursiveTask;
import java.util.stream.Collectors;

public class ParallelRemove extends RecursiveTask<List<Edge>> {

    //private static final int CUTOFF = 50000;

    private List<Edge> edges;
    private Map<Integer, Set<Integer>> degreeS;
    private int start;
    private int end;
    private double threshold;
    private int cutoff = 40000;

    public ParallelRemove(List<Edge> edges, Map<Integer, Set<Integer>> degreeS,
        double threshold) {

        this.edges = edges;
        this.end = edges.size();
        this.degreeS = degreeS;
        this.threshold = threshold;
    }

    public ParallelRemove(List<Edge> edges, Map<Integer, Set<Integer>> degreeS,
        double threshold, int cutoff) {

        this.edges = edges;
        this.end = edges.size();
        this.degreeS = degreeS;
        this.threshold = threshold;
        this.cutoff = cutoff;
    }

    private ParallelRemove(List<Edge> edges, Map<Integer, Set<Integer>> degreeS, int start,
        int end, double threshold, int cutoff) {

        this.edges = edges;
        this.degreeS = degreeS;
        this.start = start;
        this.end = end;
        this.threshold = threshold;
        this.cutoff = cutoff;
    }

    @Override
    protected List<Edge> compute() {

        // Sequential
        if (end - start < cutoff) {
//            int counter = 0;
            List<Edge> newEdges = new ArrayList<>(end - start);
            for (int i = start; i < end; i++) {
//                if (edges.get(i) == null) continue;
                Edge edge = edges.get(i);
                int u = edge.getU();
                int v = edge.getV();

                if (degreeS.get(u).size() > threshold && degreeS.get(v).size() > threshold) {
//                    edges.set(i, null);
//                    counter++;
                    newEdges.add(edge);
                }
            }
            return newEdges;
        }

        // Parallel
        int mid = (start + end) / 2;

        ParallelRemove left = new ParallelRemove(edges, degreeS, start, mid, threshold, cutoff);
        ParallelRemove right = new ParallelRemove(edges, degreeS, mid, end, threshold, cutoff);

        left.fork();
//        int r = right.compute();
//        int l = left.join();
        List<Edge> rightArray = right.compute();
        List<Edge> leftArray = left.join();
//        return r + l;
//        return returnList;
//        returnList.addAll(rightArray);
//        returnList.addAll(leftArray);
        return Streams.concat(leftArray.stream(), rightArray.stream()).collect(Collectors.toList());
    }
}