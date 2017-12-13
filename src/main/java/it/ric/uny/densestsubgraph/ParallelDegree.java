package it.ric.uny.densestsubgraph;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.RecursiveTask;
import java.util.stream.Collectors;

public class ParallelDegree extends RecursiveTask<HashMap<Integer, Integer>> {

  // Set di nodi
  private Set<Integer> nodeSet;

  // Mappa delle connessioni
  // Chiave: Nodo
  // Valore: Nodi adiacenti
  private HashMap<Integer, HashSet<Integer>> connections;

  private int low;
  private int high;
  private int cutoff;

  public ParallelDegree(Set<Integer> nodeSet,
      HashMap<Integer, HashSet<Integer>> connections) {

    this.nodeSet = nodeSet;
    this.connections = connections;
    this.high = this.nodeSet.size();
    this.cutoff = this.high / 4;
  }

  public ParallelDegree(Set<Integer> nodeSet,
      HashMap<Integer, HashSet<Integer>> connections,
      int parallelism) {

    this.nodeSet = nodeSet;
    this.connections = connections;
    this.high = this.nodeSet.size();
    this.cutoff = this.high / parallelism;
  }

  private ParallelDegree(Set<Integer> nodeSet,
      HashMap<Integer, HashSet<Integer>> connections,
      int high, int cutoff) {

    this.nodeSet = nodeSet;
    this.connections = connections;
    this.high = high;
    this.cutoff = cutoff;
  }

  @Override
  protected HashMap<Integer, Integer> compute() {

    // Sequential
    if (nodeSet.size() < cutoff) {

      HashMap<Integer, Integer> degreeMap = new HashMap<>();
      for (int x : nodeSet) {

        // Il grado di un nodo è banalmente la dimensione dell'
        // insieme contenente i nodi adiacenti.
        int value = connections.get(x).size();
        degreeMap.put(x, value);
      }

      return degreeMap;
    }

    // Parallel

    // Divisione dell' insieme più grande in due più piccoli
    Set<Integer> degreeLeft = nodeSet.stream().limit(nodeSet.size() / 2).collect(Collectors.toSet());
    Set<Integer> degreeRight = nodeSet.stream().skip(nodeSet.size() / 2).collect(Collectors.toSet());

    ParallelDegree left = new ParallelDegree(degreeLeft,
        connections, degreeLeft.size(), cutoff);
    ParallelDegree right = new ParallelDegree(degreeRight,
        connections, degreeRight.size(), cutoff);


    left.fork();
    HashMap<Integer, Integer> degreeMapRight = right.compute();
    HashMap<Integer, Integer> degreeMapLeft = left.join();

    // Merge dei risultati
    HashMap<Integer, Integer> degreeMap = new HashMap<>(degreeMapRight);
    degreeMapLeft.forEach((key, value) -> degreeMap
        .merge(key, value, (v1, v2) -> v1 + v2));

    return degreeMap;
  }
}
