package it.ric.uny.densestsubgraph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.RecursiveTask;

public class ParallelDegree extends RecursiveTask<HashMap<Integer, Integer>> {

    private static final int CUTOFF = 10000000;

    // Set di nodi
    private ArrayList<Integer> nodeSet;

    // Mappa delle connessioni
    // Chiave: Nodo
    // Valore: Nodi adiacenti
    private HashMap<Integer, HashSet<Integer>> connections;

    private int low;
    private int high;

    public ParallelDegree(HashMap<Integer, HashSet<Integer>> connections,
        ArrayList<Integer> nodeList) {

        this.nodeSet = nodeList;

        this.connections = connections;
        this.high = this.nodeSet.size();
    }

    private ParallelDegree(ArrayList<Integer> nodeSet,
        HashMap<Integer, HashSet<Integer>> connections,
        int high) {

        this.nodeSet = nodeSet;
        this.connections = connections;
        this.high = high;
    }

    @Override
    protected HashMap<Integer, Integer> compute() {

        // Sequential
        if (nodeSet.size() < CUTOFF) {
            HashMap<Integer, Integer> degreeMap = new HashMap<>();
            for (int x : nodeSet) {

                // Il grado di un nodo è banalmente la dimensione dell'insieme contenente i nodi adiacenti.
                int value = connections.get(x).size();
                degreeMap.put(x, value);
            }

            return degreeMap;
        }

        // Parallel

        // Divisione dell' insieme più grande in due più piccoli

        double startTimeD = System.nanoTime();

        ArrayList<Integer> degreeLeft = new ArrayList<>(nodeSet.subList(0, nodeSet.size() / 2));
        ArrayList<Integer> degreeRight = new ArrayList<>(nodeSet.subList(nodeSet.size() / 2,
            nodeSet.size()));

        double endTimeD = System.nanoTime();
        double timeD = (endTimeD - startTimeD)/1000000.0;
        //System.out.println("Arraylist split: " + timeD + "ms");

        ParallelDegree left = new ParallelDegree(degreeLeft,
            connections, degreeLeft.size());
        ParallelDegree right = new ParallelDegree(degreeRight,
            connections, degreeRight.size());

        left.fork();
        HashMap<Integer, Integer> degreeMapRight = right.compute();
        HashMap<Integer, Integer> degreeMapLeft = left.join();

        // Merge dei risultati
        double startTime = System.nanoTime();

        HashMap<Integer, Integer> degreeMap = new HashMap<>(degreeMapRight);
        degreeMapLeft.forEach((k, v) -> degreeMap.merge(k, v, (x,y) -> x+y ));

        double endTime = System.nanoTime();
        double time = (endTime - startTime)/1000000.0;
        //System.out.println("HashMap merge: " + time + "ms");

        return degreeMap;
    }
}
