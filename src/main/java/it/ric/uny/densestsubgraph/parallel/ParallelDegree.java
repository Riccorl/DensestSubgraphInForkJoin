package it.ric.uny.densestsubgraph.parallel;

import it.ric.uny.densestsubgraph.Edge;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.RecursiveAction;
import lombok.Data;

// Modifica con ConcurrentHashMap
// Source Code: https://goo.gl/tZqrkB
// Link utili:
// https://howtodoinjava.com/core-java/multi-threading/best-practices-for-using-concurrenthashmap/

@Data
public class ParallelDegree extends RecursiveAction {

    private static final int CUTOFF = 5000;

    // ArrayList contenente gli archi
    private ArrayList<Edge> edges;
    //private Edge[] edges;

    // Mappa (u, deg(u))
    private ConcurrentHashMap<Integer, Set<Integer>> degreeMap;

    private int start;
    private int end;

    public ParallelDegree(ArrayList<Edge> edges,
        ConcurrentHashMap<Integer, Set<Integer>> degreeMap, int end) {
        this.edges = edges;
        this.degreeMap = degreeMap;
        this.start = 0;
        this.end = end;
    }

    private ParallelDegree(ArrayList<Edge> edges,
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

        ParallelDegree left = new ParallelDegree(edges, degreeMap, start, mid);
        ParallelDegree right = new ParallelDegree(edges, degreeMap, mid, end);

        left.fork();
        right.compute();
        left.join();
    }

}
