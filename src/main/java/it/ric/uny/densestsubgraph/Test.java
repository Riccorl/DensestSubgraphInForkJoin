package it.ric.uny.densestsubgraph;

import com.google.common.graph.MutableGraph;
import it.ric.uny.densestsubgraph.utils.GraphParser;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Test {

    public static void main(String[] args) {

        MutableGraph<Integer> graphGuava = null;
        //String filename = "data/dummy_graph.txt";
        //String filename = "data/facebook_combined.txt";
        //String filename = "data/ca-CondMat.txt";
        //String filename = "data/ca-AstroPh.txt"; // 84424
        //String filename = "data/roadNet-CA.txt"; // 0, nEdge = 2766607
        //String filename = "data/as-skitter.txt"; // nEdges = 11095298
        //String filename = "data/cit-Patents.txt"; // 3858266, nEdges = 16518948
        //String filename = "data/wiki-topcats.txt"; // 0, nEdges = 28511807
        //String filename = "data/soc-LiveJournal1.txt"; // 0, nEdge = 68993773
        //String filename = "data/gplus_combined.txt"; // 106558284273162270526

        int node = 2;
        int nEdges = 117185083;

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
        System.out.println("Guava Time: " + guavaTime + "ms");*/

        System.out.println("");

        /*UndirectedGraphSeq myGraph = new UndirectedGraphSeq(filename);
        //Seq
        double startTime = System.nanoTime();
        myGraph.degreePrepare();
        double endTime = System.nanoTime();
        double time = (endTime - startTime) / 1000000.0;
        System.out.println("Sequential Time: " + time + "ms");

        int degSeq = myGraph.degree(node);
        System.out.println("Degree Sequential: " + degSeq);

        System.out.println("");*/

        /*UndirectedGraph myGraphParallel = new UndirectedGraph(filename);
        // Parallel
        double startTimeP = System.nanoTime();
        myGraphParallel.degreePrepareParallel();
        double endTimeP = System.nanoTime();
        double parTime = (endTimeP - startTimeP) / 1000000.0;
        System.out.println("Parallel Time: " + parTime + "ms");

        int degPar = myGraphParallel.degree(node);
        System.out.println("Degree Parallel: " + degPar);*/

        UndirectedGraphArrays graphArrays = new UndirectedGraphArrays(filename, nEdges);
        for (int i = 0; i < 1; i++) {
            double startTimeA = System.nanoTime();

            //graphArrays.degreePrepareParallel();

            graphArrays.degreePrepare();

            double endTimeA = System.nanoTime();
            double timeA = (endTimeA - startTimeA) / 1000000.0;
            System.out.println("Array Time: " + timeA + "ms");
        }


        System.out.println("Edge number: " + graphArrays.getEdges().size());
        int degPar = graphArrays.degree(node);
        System.out.println("Degree Array: " + degPar);

        // Nodes size
        /*Set<Integer> nodeSet = myGraph.getConnections().keySet();
        System.out.println("Number of nodes: " + nodeSet.size());
        System.out.println("Max value: " + Collections.max(nodeSet));*/
        //Speedup
        //System.out.println("Speedup: " + time/parTime);

        // Induced Edge Set

/*        HashSet<Integer> s = new HashSet<>();
        s.add(0);
        s.add(1);
        s.add(2);

        System.out.println(myGraph.inducedEdge(s));*/

    }
}
