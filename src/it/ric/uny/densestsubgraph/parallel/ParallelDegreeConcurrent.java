package it.ric.uny.densestsubgraph.parallel;

import it.ric.uny.densestsubgraph.model.Edge;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.RecursiveAction;

// ConcurrentHashMap
// Source Code: https://goo.gl/tZqrkB
// Links:
// https://howtodoinjava.com/core-java/multi-threading/best-practices-for-using-concurrenthashmap/

public class ParallelDegreeConcurrent extends RecursiveAction {

    private static final int CUTOFF = 5000;

    // ArrayList od edges
    private List<Edge> edges;
    // Map (u, deg(u))
    private ConcurrentHashMap<Integer, Set<Integer>> degreeMap;
    private int start;
    private int end;

    public ParallelDegreeConcurrent(List<Edge> edges,
        ConcurrentHashMap<Integer, Set<Integer>> degreeMap) {
        this.edges = edges;
        this.degreeMap = degreeMap;
        this.end = edges.size();
    }

    private ParallelDegreeConcurrent(List<Edge> edges,
        ConcurrentHashMap<Integer, Set<Integer>> degreeMap, int start, int end) {
        this.edges = edges;
        this.degreeMap = degreeMap;
        this.start = start;
        this.end = end;
    }

    @Override
    protected void compute() {
        // Sequential
        if (end - start < CUTOFF) {
            for (int i = start; i < end; i++) {
                // Update nodes
                var u = edges.get(i).getU();
                var v = edges.get(i).getV();

                if (degreeMap.putIfAbsent(u, new HashSet<>()) != null)
                    degreeMap.get(u).add(v);

                if (degreeMap.putIfAbsent(v, new HashSet<>()) != null)
                    degreeMap.get(v).add(u);
            }
            return;
        }
        // Parallel
        int mid = (start + end) / 2;
        var left = new ParallelDegreeConcurrent(edges, degreeMap, start, mid);
        var right = new ParallelDegreeConcurrent(edges, degreeMap, mid, end);
        left.fork();
        right.compute();
        left.join();
    }
}