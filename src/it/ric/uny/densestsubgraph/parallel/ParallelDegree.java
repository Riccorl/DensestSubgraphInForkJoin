package it.ric.uny.densestsubgraph.parallel;

import it.ric.uny.densestsubgraph.model.Edge;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.RecursiveAction;

// Modifica con ConcurrentHashMap
// Source Code: https://goo.gl/tZqrkB
// Link utili:
// https://howtodoinjava.com/core-java/multi-threading/best-practices-for-using-concurrenthashmap/

public class ParallelDegree extends RecursiveAction {

    private static final int CUTOFF = 5000;

    // ArrayList contenente gli archi
    private List<Edge> edges;
    //private Edge[] edges;
    // Mappa (u, deg(u))
    private ConcurrentHashMap<Integer, Set<Integer>> degreeMap;

    private int start;
    private int end;

    public ParallelDegree(List<Edge> edges, ConcurrentHashMap<Integer, Set<Integer>> degreeMap) {
        this.edges = edges;
        this.degreeMap = degreeMap;
        this.end = edges.size();
    }

    private ParallelDegree(List<Edge> edges,
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
                if (edges.get(i) == null) {
                    continue;
                }
                // Nodo da aggiornare
                int u = edges.get(i).getU();
                int v = edges.get(i).getV();

                if (degreeMap.putIfAbsent(u, new HashSet<>()) != null) {
                    degreeMap.get(u).add(v);
                }

                if (degreeMap.putIfAbsent(v, new HashSet<>()) != null) {
                    degreeMap.get(v).add(u);
                }
            }
            return;
        }

        // Parallel
        int mid = (start + end) / 2;

//        ParallelDegree left = new ParallelDegree(edges, degreeMap, start, mid);
//        ParallelDegree right = new ParallelDegree(edges, degreeMap, mid, end);
        invokeAll(new ParallelDegree(edges, degreeMap, start, mid),
            new ParallelDegree(edges, degreeMap, mid, end));
//        left.fork();
//        right.compute();
//        left.join();
    }
}