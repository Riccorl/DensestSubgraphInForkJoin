package it.ric.uny.densestsubgraph;

import com.google.common.graph.MutableGraph;
import it.ric.uny.densestsubgraph.utils.GraphParser;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.OptionalInt;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class Test {

    public static void main(String[] args) {

        MutableGraph<Integer> graphGuava = null;
        //String filename = "data/dummy_graph.txt";
        //String filename = "data/dummy_graph2.txt";            float nEdge = 11;         float nNode = 8;
        //String filename = "data/ca-GrQc.txt";                 float nEdges = 14496;     float nNodes = 18772;
        //String filename = "data/facebook_combined.txt";       float nEdges = 88234;     float nNodes = 4039;
        //String filename = "data/ca-CondMat.txt";              float nEdges = 93497;     float nNodes = 23133;
        String filename = "data/ca-AstroPh.txt";              float nEdges = 198110;    float nNodes = 18772;
        //String filename = "data/roadNet-CA.txt";              float nEdge = 2766607;    float nNodes = 1965206;
        //String filename = "data/as-skitter.txt";              float nEdges = 11095298;  float nNodes = 1696415;
        //String filename = "data/cit-Patents.txt";             float nEdges = 16518948;  float nNodes = 3774768;
        //String filename = "data/wiki-topcats.txt";            float nEdges = 28511807;  float nNodes = 1791489;
        //String filename = "data/soc-LiveJournal1.txt";        float nEdge = 68993773;   float nNodes = 4847571;

        int node = 0;

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

        // Sequenziale
        /*long startTime = System.nanoTime();
        UndirectedGraphSeq myGraph = new UndirectedGraphSeq(filename, nEdges, nNodes);
        float densest = myGraph.densestSubgraph((float) 1);
        //float densest = myGraph.densestSubgraphRic((float) 1);
        long endTime = System.nanoTime();
        long time = TimeUnit.NANOSECONDS.toMillis(endTime - startTime);
        System.out.println("Sequential Densest Time: " + time + "ms");
        //System.out.println("Densest subgraph nodes: " + densest.size());
        System.out.println("Densest subgraph d: " + myGraph.getDensity());
        //System.out.println("Densest subgraph d: " + densest);*/

//        int degSeq = myGraph.degree(node);
//        System.out.println("Degree Sequential: " + degSeq);
//        System.out.println("dG = " + myGraph.calcDensity(nEdges, nNodes));

        System.out.println();

        long startTimeP = System.nanoTime();
        UndirectedGraphArrays graphArrays = new UndirectedGraphArrays(filename, nEdges, nNodes);
        float densestP = graphArrays.densestSubgraph((float) 1);
        long endTimeP = System.nanoTime();
        long timeP = TimeUnit.NANOSECONDS.toMillis(endTimeP - startTimeP);
        System.out.println("Parallel Densest Time: " + timeP + " ms");
        //System.out.println("Densest subgraph nodes: " + densest.size());
        //System.out.println("Densest subgraph d: " + graphArrays.getDensity());
        System.out.println("Densest subgraph d: " + densestP);

        /*ArrayList<Integer> valTempi = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            long startTimeA = System.nanoTime();

            graphArrays.degreeConc();

            long endTimeA = System.nanoTime();
            long timeA = TimeUnit.NANOSECONDS.toMillis(endTimeA - startTimeA);
            System.out.println("Array Time: " + timeA + " ms");

            valTempi.add((int) timeA);

            if (i < 9) {
                graphArrays.setDegreesMap(new ConcurrentHashMap<>(nNodes,
                    0.99f));
            }
        }*/

        //Integer max = valTempi.stream().mapToInt(Integer::intValue).max().getAsInt();
        //valTempi.remove(max);
        //int media = valTempi.stream().mapToInt(Integer::intValue).sum() / valTempi.size();
        //System.out.println("Media tempi: " + media + " ms");
        //int degPar = graphArrays.degree(node);
        //System.out.println("Degree Array: " + degPar);

        // Induced Edge Set

/*      HashSet<Integer> s = new HashSet<>();
        s.add(0);
        s.add(1);
        s.add(2);

        System.out.println(myGraph.inducedEdge(s));*/

    }
}
