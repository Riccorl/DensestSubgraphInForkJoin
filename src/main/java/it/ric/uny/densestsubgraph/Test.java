package it.ric.uny.densestsubgraph;

import com.google.common.graph.MutableGraph;

import java.io.IOException;

public class Test {

    public static void main(String[] args) {

        MutableGraph<Integer> graph = null;
        //String filename = "data/dummy_graph.txt";
        String filename = "data/facebook_combined.txt";

        try {
            graph = GraphParser.parseGuava(filename);
        } catch (IOException e) {
            e.printStackTrace();
        }

        long startTime = System.currentTimeMillis();
        int degree = graph.degree(0);
        long endTime = System.currentTimeMillis();
        double time = endTime - startTime;
        System.out.println("Guava Time: " + time + " ms");
    }
}
