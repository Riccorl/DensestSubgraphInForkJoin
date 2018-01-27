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
    //String filename = "data/dummy_graph.txt";
    //String filename = "data/facebook_combined.txt";
    //String filename = "data/ca-CondMat.txt";
    //String filename = "data/ca-AstroPh.txt";
    //String filename = "data/roadNet-CA.txt";
    String filename = "data/as-skitter.txt";

    int node = 84424; //84424; //1; //73647

    /*// Guava
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
    System.out.println("Guava Time: " + guavaTime + "ms");*/


    UndirectedGraphSeq myGraph = new UndirectedGraphSeq(filename);
    //Seq
    double startTime = System.currentTimeMillis();
    myGraph.degreePrepare();
    double endTime = System.currentTimeMillis();
    double time = endTime - startTime;
    System.out.println("Sequential Time: " + time + "ms");

    int degSeq = myGraph.degree(node);
    System.out.println("Degree Sequential: " + degSeq);

    /*UndirectedGraph myGraphParallel = new UndirectedGraph(filename);
    // Parallel
    double startTime = System.currentTimeMillis();
    myGraphParallel.degreePrepareParallel();
    double endTime = System.currentTimeMillis();
    double parTime = endTime - startTime;
    System.out.println("Parallel Time: " + parTime + "ms");

    int degPar = myGraphParallel.degree(node);
    System.out.println("Degree Parallel: " + degPar);*/

    //Speedup
    //System.out.println("Speedup: " + time/parTime);

    // Induced Edge Set

    /*HashSet<Integer> s = new HashSet<>();
    s.add(0);
    s.add(1);
    s.add(2);

    System.out.println(myGraph.inducedEdge(s));*/


  }
}
