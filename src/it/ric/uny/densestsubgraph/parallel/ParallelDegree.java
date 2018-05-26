package it.ric.uny.densestsubgraph.parallel;

import it.ric.uny.densestsubgraph.model.Edge;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.RecursiveTask;

// Modifica con ConcurrentHashMap
// Source Code: https://goo.gl/tZqrkB
// Link utili:
// https://howtodoinjava.com/core-java/multi-threading/best-practices-for-using-concurrenthashmap/

public class ParallelDegree extends RecursiveTask<Map<Integer, Integer>> {

    private static final int CUTOFF = 70000;

    private List<Edge> edges;

    private int start;
    private int end;

    public ParallelDegree(List<Edge> edges) {
        this.edges = edges;
        this.end = edges.size();
    }

    private ParallelDegree(List<Edge> edges, int start,
        int end) {
        this.edges = edges;
        this.start = start;
        this.end = end;
    }

    @Override
    protected Map<Integer, Integer> compute() {

        // Sequential
        if (end - start < CUTOFF) {
            Map<Integer, Integer> degreeMap = new HashMap<>();
            for (int i = start; i < end; i++) {
                // Nodes to update
                int u = edges.get(i).getU();
                int v = edges.get(i).getV();

                degreeMap.putIfAbsent(u, 0);
                degreeMap.putIfAbsent(v, 0);

                degreeMap.put(u, degreeMap.get(u) + 1);
                degreeMap.put(v, degreeMap.get(v) + 1);
            }
            return degreeMap;
        }

        // Parallel
        int mid = (start + end) / 2;

        ParallelDegree left = new ParallelDegree(edges, start, mid);
        ParallelDegree right = new ParallelDegree(edges, mid, end);

        left.fork();
        Map<Integer, Integer> rightMap = right.compute();
        Map<Integer, Integer> leftMap = left.join();
        rightMap.forEach((k, v) -> leftMap.merge(k, v, Integer::sum));
        return leftMap;
    }
}