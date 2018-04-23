package it.ric.uny.densestsubgraph.parallel;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Streams;
import it.ric.uny.densestsubgraph.Model.Edge;
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
    private float threshold;
    private int cutoff = 10000;

    public ParallelRemove(List<Edge> edges, Map<Integer, Set<Integer>> degreeS,
        float threshold) {

        this.edges = edges;
        this.end = edges.size();
        this.degreeS = degreeS;
        this.threshold = threshold;
    }

    public ParallelRemove(List<Edge> edges, Map<Integer, Set<Integer>> degreeS,
        float threshold, int cutoff) {

        this.edges = edges;
        this.end = edges.size();
        this.degreeS = degreeS;
        this.threshold = threshold;
        this.cutoff = cutoff;
    }

    private ParallelRemove(List<Edge> edges, Map<Integer, Set<Integer>> degreeS, int start,
        int end, float threshold, int cutoff) {

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
            List<Edge> newEdge = new ArrayList<>(end - start);
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
        List<Edge> rightArray = right.compute();
        List<Edge> leftArray = left.join();
        /*Iterable<Edge> combinedIterables = Iterables.unmodifiableIterable(
                Iterables.concat(leftArray, rightArray));

        return Lists.newArrayList(combinedIterables);*/
        List<Edge> returnList = new ArrayList<>(end-start);
//        returnList = Streams.concat(leftArray.stream(), rightArray.stream()).collect(Collectors.toList());
//        return returnList;
        returnList.addAll(rightArray);
        returnList.addAll(leftArray);
        return returnList;
    }
}