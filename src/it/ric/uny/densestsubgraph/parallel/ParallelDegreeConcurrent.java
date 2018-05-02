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

public class ParallelDegreeConcurrent extends RecursiveAction {

    private static final int CUTOFF = 5000;

    // ArrayList contenente gli archi
    private List<Edge> edges;
    //private Edge[] edges;
    // Mappa (u, deg(u))
    private ConcurrentHashMap<Integer, Set<Integer>> degreeMap;

    private int start;
    private int end;

    public ParallelDegreeConcurrent(List<Edge> edges, ConcurrentHashMap<Integer, Set<Integer>> degreeMap) {
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

        ParallelDegreeConcurrent left = new ParallelDegreeConcurrent(edges, degreeMap, start, mid);
        ParallelDegreeConcurrent right = new ParallelDegreeConcurrent(edges, degreeMap, mid, end);

        left.fork();
        right.compute();
        left.join();
    }
}