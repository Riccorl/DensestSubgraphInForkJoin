package it.ric.uny.densestsubgraph;

import com.google.common.graph.MutableGraph;
import it.ric.uny.densestsubgraph.utils.GraphParser;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.TreeMap;

public class Test {

  public static void main(String[] args) {

    MutableGraph<Integer> graphGuava = null;
    String filename = "data/dummy_graph.txt";
    //String filename = "data/facebook_combined.txt";
    //String filename = "data/ca-CondMat.txt";
    //String filename = "data/ca-AstroPh.txt";
    //String filename = "data/roadNet-CA.txt";

    int node = 0; //84424; //1; //73647

    try {
      graphGuava = GraphParser.parseGuava(filename);
    } catch (IOException e) {
      e.printStackTrace();
    }

    long startTime = System.currentTimeMillis();
    int deg = graphGuava.degree(node);
    long endTime = System.currentTimeMillis();
    double guavaTime = endTime - startTime;
    System.out.println("Guava Degree: " + deg);
    System.out.println("Guava Time: " + guavaTime + "ms");


    UndirectedGraph myGraph = new UndirectedGraph(filename);
    //UndirectedGraph myGraphParallel = new UndirectedGraph(filename);

    startTime = System.currentTimeMillis();
    myGraph.degreePrepare();
    endTime = System.currentTimeMillis();
    double time = endTime - startTime;
    System.out.println("Sequential Time: " + time + "ms");

    startTime = System.currentTimeMillis();
    //myGraph.degreePrepareParallel();
    endTime = System.currentTimeMillis();
    double parTime = endTime - startTime;
    System.out.println("Parallel Time: " + parTime + "ms");

    //Seq
    int degSeq = myGraph.degree(node);
    System.out.println("Degree Sequential: " + degSeq);

    // Induced Edge Set

    HashSet<Integer> s = new HashSet<>();
    s.add(0);
    s.add(1);
    s.add(2);

    System.out.println(myGraph.inducedEdge(s));
    //Speedup
    //System.out.println("Speedup: " + time/parTime);
  }
}
