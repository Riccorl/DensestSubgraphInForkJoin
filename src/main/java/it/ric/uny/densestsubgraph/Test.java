package it.ric.uny.densestsubgraph;

import com.google.common.graph.MutableGraph;
import com.sun.xml.internal.ws.api.streaming.XMLStreamWriterFactory;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;

import java.io.IOException;
import java.util.SortedMap;
import java.util.concurrent.ForkJoinPool;

public class Test {

    public static void main(String[] args) {

        MutableGraph<Integer> graphGuava = null;
        //String filename = "data/dummy_graph.txt";
        //String filename = "data/facebook_combined.txt";
        //String filename = "data/ca-AstroPh.txt";
        String filename = "data/roadNet-CA.txt";
        SortedMap<Integer, Integer> map = GraphParser.toMap(filename);

        long startTime = System.currentTimeMillis();
        Graph<Integer, DefaultEdge> graph = ForkJoinPool.commonPool().invoke(new UndirectedGraph(map));
        long endTime = System.currentTimeMillis();

        double time = endTime - startTime;
        System.out.println("Parallel Time: " + time + " ms");

        //int degree = graph.degree(0);
        startTime = System.currentTimeMillis();
        GraphParser.parseGuava(map);
        //graph = GraphParser.parseGuava(filename);

        endTime = System.currentTimeMillis();

        time = endTime - startTime;

        System.out.println("Guava Time: " + time + " ms");
    }
}
