package it.ric.uny.densestsubgraph;

import it.ric.uny.densestsubgraph.model.Edge;
import it.ric.uny.densestsubgraph.model.UndirectedGraph;
import it.ric.uny.densestsubgraph.utils.Utility;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ComputeDensest {

    public static void main(String[] args) {

        double epsilon = (double) 1;

//        String filename = "data/dummy_graph.txt";
//        String filename = "data/dummy_graph2.txt";            float nEdge = 11;         float nNode = 8;
//        String filename = "data/ca-GrQc.txt";                 float nEdges = 14496;     float nNodes = 5242;
        String filename = "data/facebook_combined.txt";       float nEdges = 88234;     float nNodes = 4039;
//        String filename = "data/ca-CondMat.txt";              float nEdges = 93497;     float nNodes = 23133;
//        String filename = "data/CA-HepTh.txt";                float nEdges = 25998;     float nNodes = 9877;
//        String filename = "data/ca-HepPh.txt";                float nEdges = 118521;    float nNodes = 12008;
//        String filename = "data/email-Enron.txt";             float nEdges = 183831;    float nNodes = 36692;
//        String filename = "data/ca-AstroPh.txt";              float nEdges = 198110;    float nNodes = 18772;
//        String filename = "data/roadNet-CA.txt";              float nEdge = 2766607;    float nNodes = 1965206;
//        String filename = "data/as-skitter.txt";              float nEdges = 11095298;  float nNodes = 1696415;
//        String filename = "data/cit-Patents.txt";             float nEdges = 16518948;  float nNodes = 3774768;
//        String filename = "data/wiki-topcats.txt";            float nEdges = 28511807;  float nNodes = 1791489;
//        String filename = "data/soc-LiveJournal1.txt";        float nEdge = 68993773;   float nNodes = 4847571;
//
        // ------------------------------------- Setup --------------------------------------------
        System.out.println();
        System.out.println("Filename: " + filename);
        System.out.println("Approximation factor epsilon: " + epsilon);
        System.out.println();
        System.out.println("Reading...");
        var edges = Utility.fileToEdge(filename);
        System.out.println("Read ok");
        System.out.println("Number of nodes: " + (int) nNodes);
        System.out.println("Number of edges: " + edges.size());
        System.out.println();

        var densest = new Densest();
        // --------------------------------- Sequential -------------------------------------------
        var graph = new UndirectedGraph(edges);
        long startTime = System.nanoTime();
        double dS = densest.densestSubgraphSequential(graph, epsilon);
        long endTime = System.nanoTime();
        long timeS = TimeUnit.NANOSECONDS.toMillis(endTime - startTime);
        System.out.println("Sequential Degree Time: " + timeS + " ms");
        System.out.println("Sequential density: " + dS);

        // --------------------------------- Parallel ---------------------------------------------
        long startTimeP = System.nanoTime();
        double dP = densest.densestSubgraphParallel(graph, epsilon);
        long endTimeP = System.nanoTime();
        long time = TimeUnit.NANOSECONDS.toMillis(endTimeP - startTimeP);
        System.out.println("Parallel Degree Time: " + time + " ms");
        System.out.println("Parallel Density: " + dP);

        System.out.println("Speedup: " + (timeS / time));

    }

}
