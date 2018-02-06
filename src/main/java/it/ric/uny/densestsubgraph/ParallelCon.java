package it.ric.uny.densestsubgraph;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.RecursiveAction;

public class ParallelCon extends RecursiveAction {

    private static final int CUTOFF = 10000;

    private ArrayList<Edge> edges;

    private int nEdge;
    private ConcurrentHashMap<Integer, Integer> degreeMap;

    public ParallelCon(ArrayList<Edge> edges,
        ConcurrentHashMap<Integer, Integer> degreeMap, int nEdge) {
        this.edges = edges;
        this.nEdge = nEdge;
        this.degreeMap = degreeMap;
    }

    @Override
    protected void compute() {

        // Sequential
        if (edges.size() < CUTOFF) {
            for (Edge edge : edges) {
                int key = edge.getU();
                degreeMap.put(key, degreeMap.get(key) + 1);
            }

            return;
        }

        // Parallel
        double startTimeD = System.nanoTime();

        int nEdgeHalf = nEdge/2;
        ArrayList<Edge> degreeLeft = new ArrayList<>(edges.subList(0, nEdgeHalf));
        ArrayList<Edge> degreeRight = new ArrayList<>(edges.subList(nEdgeHalf, nEdge));

        double endTimeD = System.nanoTime();
        double timeD = (endTimeD - startTimeD)/1000000.0;
        //System.out.println("Arraylist split: " + timeD + "ms");

        ParallelCon left = new ParallelCon(degreeLeft, degreeMap, nEdgeHalf);
        ParallelCon right = new ParallelCon(degreeRight, degreeMap, nEdgeHalf);

        left.fork();
        right.compute();   // compute() strano wtf
        left.join();
    }

    //-------------------------------------------- GETTER --------------------------------------------


}
