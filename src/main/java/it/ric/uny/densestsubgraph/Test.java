package it.ric.uny.densestsubgraph;

import com.google.common.graph.MutableGraph;
import it.ric.uny.densestsubgraph.utils.GraphParser;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class Test {

    public static void main(String[] args) {

        MutableGraph<Integer> graphGuava = null;
        //String filename = "data/dummy_graph.txt";
        //String filename = "data/facebook_combined.txt";
        //String filename = "data/ca-CondMat.txt";
        //String filename = "data/ca-AstroPh.txt"; // 84424
        //String filename = "data/roadNet-CA.txt"; // 0, nEdge = 2766607, nNodes = 1965206
        //String filename = "data/as-skitter.txt"; // nEdges = 11095298, nNodes = 1696415
        //String filename = "data/cit-Patents.txt"; // 3858266, nEdges = 16518948, nNodes = 3774768
        String filename = "data/wiki-topcats.txt"; // 0, nEdges = 28511807, nNodes = 1791489
        //String filename = "data/soc-LiveJournal1.txt"; // 0, nEdge = 68993773, nNodes = 4847571

        int node = 0;
        int nEdges = 28511807;
        int nNodes = 1791489;

        // Guava
        /*try {
            graphGuava = GraphParser.parseGuava(filename);
        } catch (IOException e) {
            e.printStackTrace();
        }

        long startTimeG = System.currentTimeMillis();
        int deg = graphGuava.degree(node);
        long endTimeG = System.currentTimeMillis();
        double guavaTime = endTimeG - startTimeG;
        System.out.println("Guava Degree: " + deg);
        System.out.println("Guava Time: " + guavaTime + "ms");

        System.out.println("");*/

        /*UndirectedGraphSeq myGraph = new UndirectedGraphSeq(filename, nEdges, nNodes);
        //Seq
        double startTime = System.nanoTime();
        myGraph.degreeSeq();
        double endTime = System.nanoTime();
        double time = (endTime - startTime) / 1000000.0;
        System.out.println("Sequential Time: " + time + "ms");

        int degSeq = myGraph.degree(node);
        System.out.println("Degree Sequential: " + degSeq);

        System.out.println("");*/


        UndirectedGraphArrays graphArrays = new UndirectedGraphArrays(filename, nEdges, nNodes);
        for (int i = 0; i < 3; i++) {
            double startTimeA = System.nanoTime();

            graphArrays.degreeConc();

            double endTimeA = System.nanoTime();
            double timeA = (endTimeA - startTimeA) / 1000000.0;
            System.out.println("Array Time: " + timeA + "ms");

            if (i < 2) {
                ConcurrentHashMap<Integer, Integer> oldMap = graphArrays.getDegreeMap();
                graphArrays.setDegreeMap(resetMap(nNodes, oldMap));
            }

        }

        int degPar = graphArrays.degree(node);
        System.out.println("Degree Array: " + degPar);

        // Induced Edge Set

/*      HashSet<Integer> s = new HashSet<>();
        s.add(0);
        s.add(1);
        s.add(2);

        System.out.println(myGraph.inducedEdge(s));*/

    }

    private static ConcurrentHashMap<Integer, Integer> resetMap(int nNodes,
        ConcurrentHashMap<Integer, Integer> oldMap) {
        ConcurrentHashMap<Integer, Integer> newMap = new ConcurrentHashMap<>(nNodes,
            0.75f);

        for (int x : oldMap.keySet()) {
            newMap.put(x, 0);
        }

        return newMap;
    }
}
