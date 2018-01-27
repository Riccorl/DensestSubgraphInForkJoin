package it.ric.uny.densestsubgraph;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.RecursiveTask;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ParallelDegree extends RecursiveTask<HashMap<Integer, Integer>> {

  private static final int CUTOFF = 5000;

  // Set di nodi
  private ArrayList<Integer> nodeSet;

  // Mappa delle connessioni
  // Chiave: Nodo
  // Valore: Nodi adiacenti
  private HashMap<Integer, HashSet<Integer>> connections;

  private HashMap<Integer, Integer> degreeMap;

  private int low;
  private int high;

  public ParallelDegree(HashMap<Integer, HashSet<Integer>> connections) {

    this.nodeSet = new ArrayList<>(connections.keySet());
    this.connections = connections;
    this.high = this.nodeSet.size();
    this.degreeMap = new HashMap<>();
  }

  private ParallelDegree(ArrayList<Integer> nodeSet,
      HashMap<Integer, HashSet<Integer>> connections,
      HashMap<Integer, Integer> degreeMap,
      int high) {

    this.nodeSet = nodeSet;
    this.connections = connections;
    this.high = high;
    this.degreeMap = degreeMap;
  }

  @Override
  protected HashMap<Integer, Integer>  compute() {

    // Sequential
    if (nodeSet.size() < CUTOFF) {

      for (int x : nodeSet) {

        // Il grado di un nodo è banalmente la dimensione dell'insieme contenente i nodi adiacenti.
        int value = connections.get(x).size();
        degreeMap.put(x, value);
      }

      return degreeMap;
    }

    // Parallel

    // Divisione dell' insieme più grande in due più piccoli

    ArrayList<Integer> degreeLeft = new ArrayList<>(nodeSet.subList(0, nodeSet.size() / 2));
    ArrayList<Integer> degreeRight = new ArrayList<>(
        nodeSet.subList(nodeSet.size() / 2, nodeSet.size()));

    ParallelDegree left = new ParallelDegree(degreeLeft,
        connections, degreeMap, degreeLeft.size());
    ParallelDegree right = new ParallelDegree(degreeRight,
        connections, degreeMap, degreeRight.size());

    left.fork();
    HashMap<Integer, Integer> degreeMapRight = right.compute();
    HashMap<Integer, Integer> degreeMapLeft = left.join();

    // Merge dei risultati

    // Merge con HashMap
    /*HashMap<Integer, Integer> degreeMap = new HashMap<>(degreeMapRight);
    degreeMapLeft.forEach((key, value) -> degreeMap
        .merge(key, value, (v1, v2) -> v1 + v2));*/

    return degreeMap;
  }
}
