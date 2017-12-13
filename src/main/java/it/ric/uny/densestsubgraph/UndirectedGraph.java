package it.ric.uny.densestsubgraph;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Stream;
import javax.sound.midi.Soundbank;

public class UndirectedGraph implements Graph {

  private static final String COMMENT_CHAR = "#";
  private static ForkJoinPool fjPool = new ForkJoinPool();

  private HashMap<Integer, HashSet<Integer>> graph;
  private HashMap<Integer, Integer> degreeMap;
  private HashMap<Integer, Integer> degreeMapPar;
  private HashMap<Integer, HashSet<Integer>> connections;

  public UndirectedGraph(String filename) {

    this.graph = new HashMap<>();
    this.degreeMap = new HashMap<>();
    this.degreeMapPar = new HashMap<>();
    this.connections = new HashMap<>();

    this.fileToGraph(filename);
  }

  @Override
  public int degree(int n) {
    return degreeMap.get(n);
  }

  @Override
  public Graph inducedSubgraph(Set<Integer> nodes) {

    HashMap<Integer, HashSet<Integer>> subGraph = new HashMap<>();
    connections.forEach((x, y) -> {
      if (nodes.contains(x)) subGraph.put(x, new HashSet<>());
    });
    return null;
  }

  /**
   * Wrapper for prepareParallel method
   */
  public void degreePrepareParallel() {
    this.degreeMapPar = prepareParallel(degreeMap.keySet());
  }

  /**
   * Precalculation of all nodes' degree in parallel
   *
   * @param degreeSet Set of nodes
   * @return degreeMap with degrees
   */
  private HashMap<Integer, Integer> prepareParallel(Set<Integer> degreeSet) {

    int parallelism = fjPool.getParallelism();
    degreeMapPar = fjPool.invoke(new ParallelDegree(degreeSet, graph, connections, parallelism));

    return degreeMapPar;
  }

  /**
   * Wrapper for prepare method
   */
  public void degreePrepare() {
    this.degreeMap = prepare(degreeMap);
  }

  /**
   * Precalculation of all nodes' degree.
   *
   * @param degreeMap Empty map of nodes and relative degree
   * @return degreeMap with degrees
   */
  private HashMap<Integer, Integer> prepare(HashMap<Integer, Integer> degreeMap) {

    for (Integer x : degreeMap.keySet()) {
      int value = connections.get(x).size();
      degreeMap.put(x, value);
    }

    return degreeMap;
  }

  /**
   * Reads from file and generates data structure for the graph
   *
   * @param filename file to read
   */
  private void fileToGraph(String filename) {

    try (Stream<String> stream = Files.lines(Paths.get(filename))) {
      stream.forEach(x -> {

        if (x.startsWith(COMMENT_CHAR)) {
          return;
        }

        String[] row = x.split("[\t ]");
        int n1 = Integer.parseInt(row[0]);
        int n2 = Integer.parseInt(row[1]);

        degreeMap.put(n1, 0);
        degreeMap.put(n2, 0);

        degreeMapPar.put(n1, 0);
        degreeMapPar.put(n2, 0);

        if (!graph.containsKey(n1)) {
          graph.put(n1, new HashSet<>());
        }
        if (!graph.containsKey(n2)) {
          graph.put(n2, new HashSet<>());
        }

        if (!connections.containsKey(n1)) {
          connections.put(n1, new HashSet<>());
        }
        if (!connections.containsKey(n2)) {
          connections.put(n2, new HashSet<>());
        }

        // fill graph
        HashSet<Integer> map = graph.get(n1);
        map.add(n2);
        graph.put(n1, map);

        // fill connections
        map = connections.get(n1);
        map.add(n2);
        connections.put(n1, map);

        map = connections.get(n2);
        map.add(n1);
        connections.put(n2, map);

      });

    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  //-------------------------------------------- GETTER --------------------------------------------

  public HashMap<Integer, Integer> getDegreeMap() {
    return degreeMap;
  }

  public HashMap<Integer, HashSet<Integer>> getGraph() {
    return graph;
  }

  public HashMap<Integer, HashSet<Integer>> getConnections() {
    return connections;
  }
}
