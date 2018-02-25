package it.ric.uny.densestsubgraph;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.RecursiveAction;

// Modifica con ConcurrentHashMap
// Source Code: https://goo.gl/tZqrkB
// Link utili:
// https://howtodoinjava.com/core-java/multi-threading/best-practices-for-using-concurrenthashmap/

public class ParallelCon extends RecursiveAction {

    private static final int CUTOFF = 40000;

    // ArrayList contenente gli archi
    private ArrayList<Edge> edges;

    private int nEdge;
    private ConcurrentHashMap<Integer, Integer> degreeMap;

    private int start;
    private int end;

    public ParallelCon(ArrayList<Edge> edges,
        ConcurrentHashMap<Integer, Integer> degreeMap, int start, int end) {
        this.edges = edges;
        this.nEdge = nEdge;
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
                int key = edges.get(i).getU();
                degreeMap.put(key, degreeMap.get(key) + 1);
            }

            return;
        }

        // Parallel
        int mid = (start + end) / 2;

        ParallelCon left = new ParallelCon(edges, degreeMap, start, mid);
        ParallelCon right = new ParallelCon(edges, degreeMap, mid, end);

        left.fork();
        right.compute();
        left.join();
    }

    //-------------------------------------------- GETTER --------------------------------------------


}
