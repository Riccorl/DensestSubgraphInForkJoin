package it.ric.uny.densestsubgraph;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Stream;
import javax.sound.midi.Soundbank;

public class UndirectedGraph implements Graph {

  private static final String COMMENT_CHAR = "#";

  private HashMap<Integer, HashSet<Integer>> graph;
  private HashMap<Integer, Integer> degreeMap;

  public UndirectedGraph(String filename) {

    this.graph = new HashMap<>();
    this.degreeMap = new HashMap<>();

    this.fileToGraph(filename);
    //this.degreePrepare();
  }

  @Override
  public int degree(int n) {
    return degreeMap.get(n);
  }

  private void fileToGraph(String filename) {
    List<String> rows = this.parse(filename);

    rows.forEach(x -> {

      if (x.startsWith(COMMENT_CHAR)) return;

      String[] row = x.split("[\t ]");
      int n1 = Integer.parseInt(row[0]);
      int n2 = Integer.parseInt(row[1]);

      degreeMap.put(n1, 0);
      degreeMap.put(n2, 0);

      if (!graph.containsKey(n1)) {
        graph.put(n1, new HashSet<>());
      }
      if (!graph.containsKey(n2)) {
        graph.put(n2, new HashSet<>());
      }

      HashSet<Integer> map = graph.get(n1);
      map.add(n2);
      graph.put(n1, map);

    });
  }

  public void degreePrepareParallel() {
    this.degreeMap = prepareParallel(degreeMap.keySet());
  }

  private HashMap<Integer, Integer> prepareParallel(Set<Integer> degreeSet) {

    HashMap<Integer, Integer> degreeMap = ForkJoinPool.commonPool()
        .invoke(new ParallelDegree(degreeSet, graph));

    return degreeMap;
  }

  public void degreePrepare() {
    this.degreeMap = prepare(degreeMap);
  }

  private HashMap<Integer, Integer> prepare(HashMap<Integer, Integer> degreeMap) {

    for (int x : degreeMap.keySet()) {
      final int[] value = {degreeMap.containsValue(x) ? degreeMap.get(x) : 0};

      graph.forEach((node, edges) -> value[0] += edges.stream()
          .filter(nodeToFilter -> nodeToFilter.equals(x)).count());
      value[0] += graph.get(x).size();
      degreeMap.put(x, value[0]);
    }

    return degreeMap;
  }

  private List<String> parse(String filename) {

    List<String> rows = new ArrayList<>();

    try (Stream<String> stream = Files.lines(Paths.get(filename))) {
      stream.forEach(rows::add);
    } catch (IOException e) {
      e.printStackTrace();
    }

    return rows;
  }

  public HashMap<Integer, Integer> getDegreeMap() {
    return degreeMap;
  }

  public HashMap<Integer, HashSet<Integer>> getGraph() {
    return graph;
  }
}
