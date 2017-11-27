package it.ric.uny.densestsubgraph;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.RecursiveTask;
import java.util.stream.Collectors;

public class ParallelDegree extends RecursiveTask<HashMap<Integer, Integer>> {

  private HashMap<Integer, HashSet<Integer>> graph;
  private Set<Integer> degreeSet;
  private int low;
  private int high;
  private int cutoff;

  public ParallelDegree(Set<Integer> degreeSet,
      HashMap<Integer, HashSet<Integer>> graph) {

    this.graph = graph;
    this.degreeSet = degreeSet;
    this.high = this.degreeSet.size();
    this.cutoff = this.high / 4;
  }

  public ParallelDegree(
      HashMap<Integer, HashSet<Integer>> graph, Set<Integer> degreeSet, int high,
      int cutoff) {
    this.graph = graph;
    this.degreeSet = degreeSet;
    this.high = high;
    this.cutoff = cutoff;
  }

  @Override
  protected HashMap<Integer, Integer> compute() {

    HashMap<Integer, Integer> degreeMap = new HashMap<>();

    if (high - low < cutoff) {
      for (int x : degreeSet) {
        //final int[] value = {degreeMap.containsValue(x) ? degreeMap.get(x) : 0};

        final int[] value = {0};
        graph.forEach((node, edges) -> value[0] += edges.stream()
            .filter(nodeToFilter -> nodeToFilter.equals(x)).count());
        value[0] += graph.get(x).size();
        degreeMap.put(x, value[0]);
      }

      return degreeMap;
    }

    Set<Integer> degreeLeft = degreeSet.stream().limit((high+low)/2).collect(Collectors.toSet());
    Set<Integer> degreeRight = degreeSet.stream().skip((high+low)/2).collect(Collectors.toSet());

    ParallelDegree left = new ParallelDegree(graph, degreeLeft, degreeLeft.size(), cutoff);
    ParallelDegree right = new ParallelDegree(graph, degreeRight, degreeRight.size(), cutoff);

    left.fork();
    HashMap<Integer, Integer> degreeMapRight = right.compute();
    HashMap<Integer, Integer> degreeMapLeft = left.join();

    degreeMap.putAll(degreeMapRight);
    degreeMap.putAll(degreeMapLeft);

    return degreeMap;
  }
}
