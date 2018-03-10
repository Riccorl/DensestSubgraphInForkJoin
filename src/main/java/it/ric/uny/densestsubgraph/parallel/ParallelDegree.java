package it.ric.uny.densestsubgraph.parallel;

import it.ric.uny.densestsubgraph.Edge;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.RecursiveAction;

// Modifica con ConcurrentHashMap
// Source Code: https://goo.gl/tZqrkB
// Link utili:
// https://howtodoinjava.com/core-java/multi-threading/best-practices-for-using-concurrenthashmap/

public class ParallelDegree extends RecursiveAction {

    private static final int CUTOFF = 5000;

    // ArrayList contenente gli archi
    private ArrayList<Edge> edges;
    //private Edge[] edges;

    private ConcurrentHashMap<Integer, Integer> degreeMap;

    private int start;
    private int end;

    public ParallelDegree(ArrayList<Edge> edges,
        ConcurrentHashMap<Integer, Integer> degreeMap, int end) {
        this.edges = edges;
        this.degreeMap = degreeMap;
        this.start = 0;
        this.end = end;
    }

    private ParallelDegree(ArrayList<Edge> edges,
        ConcurrentHashMap<Integer, Integer> degreeMap, int start, int end) {
        this.edges = edges;
        this.degreeMap = degreeMap;
        this.start = start;
        this.end = end;
    }

    @Override
    protected void compute() {

        // Sequential
        if (end - start < CUTOFF) {
            for (int i = start; i < end;i++) {
                // Nodo da aggiornare
                int u = //edges[i].getU();
                 edges.get(i).getU();

                int v = edges.get(i).getV();

                if (degreeMap.putIfAbsent(u, 1) != null) {
                    degreeMap.put(u, degreeMap.get(u) + 1);
                }

                if (degreeMap.putIfAbsent(v, 1) != null) {
                    degreeMap.put(v, degreeMap.get(v) + 1);
                }
                //degreeMap.put(key, degreeMap.get(key) + 1);
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

    //-------------------------------------------- GETTER --------------------------------------------


}
