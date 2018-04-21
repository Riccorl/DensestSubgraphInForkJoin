package it.ric.uny.densestsubgraph;

import it.ric.uny.densestsubgraph.utils.GraphParser;
import java.util.List;

public class Test {

    public static void main(String[] args) {

        // Local
        //String filename = "data/dummy_graph.txt";
        //String filename = "data/dummy_graph2.txt";            float nEdge = 11;         float nNode = 8;
        //String filename = "data/ca-GrQc.txt";                 float nEdges = 14496;     float nNodes = 5242;
        //String filename = "data/facebook_combined.txt";       float nEdges = 88234;     float nNodes = 4039;
        //String filename = "data/ca-CondMat.txt";              float nEdges = 93497;     float nNodes = 23133;
        //String filename = "ca-AstroPh.txt";              float nEdges = 198110;    float nNodes = 18772;
        //String filename = "data/roadNet-CA.txt";              float nEdge = 2766607;    float nNodes = 1965206;
        //String filename = "data/as-skitter.txt";              float nEdges = 11095298;  float nNodes = 1696415;
        //String filename = "data/cit-Patents.txt";             float nEdges = 16518948;  float nNodes = 3774768;
        //String filename = "data/wiki-topcats.txt";            float nEdges = 28511807;  float nNodes = 1791489;
        //String filename = "data/soc-LiveJournal1.txt";        float nEdge = 68993773;   float nNodes = 4847571;

        // Remote
        //String filename = "ca-GrQc.txt";                 float nEdges = 14496;     float nNodes = 5242;
        //String filename = "facebook_combined.txt";       float nEdges = 88234;     float nNodes = 4039;
        //String filename = "ca-CondMat.txt";              float nEdges = 93497;     float nNodes = 23133;
        //String filename = "ca-AstroPh.txt";              float nEdges = 198110;    float nNodes = 18772;
        //String filename = "roadNet-CA.txt";              float nEdge = 2766607;    float nNodes = 1965206;
        //String filename = "as-skitter.txt";              float nEdges = 11095298;  float nNodes = 1696415;
        //String filename = "cit-Patents.txt";             float nEdges = 16518948;  float nNodes = 3774768;
        //String filename = "wiki-topcats.txt";            float nEdges = 28511807;  float nNodes = 1791489;
        //String filename = "com-lj.ungraph.txt";            float nEdges = 34681189;  float nNodes = 3997962;
        //String filename = "soc-LiveJournal1.txt";        float nEdge = 68993773;   float nNodes = 4847571;

        String filename = args[0];
        float epsilon = Float.parseFloat(args[1]);
        //float epsilon = (float) 1;

        // CUTOFF
        int cutoffDegree;
        int cutoffRemove;

        try {
            cutoffDegree = Integer.parseInt(args[2]);
            cutoffRemove = Integer.parseInt(args[3]);
        } catch (ArrayIndexOutOfBoundsException e) {
            cutoffDegree = 5000;
            cutoffRemove = 5000;
        }

        System.out.println();
        System.out.println("Filename: " + filename);
        //System.out.println("Numero di nodi: " + (int) nNodes);
        //System.out.println("Numero di archi: " + (int) nEdges);
        System.out.println("Fattore di approssimazione epsilon: " + epsilon);
        System.out.println();
        System.out.println("Cutoff Grado: " + cutoffDegree);
        System.out.println("Cutoff Rimozione: " + cutoffRemove);
        System.out.println();
        System.out.println("Reading...");
        List<it.ric.uny.densestsubgraph.model.Edge> edges = GraphParser.fileToEdge(filename);
        System.out.println("Read ok");
        System.out.println();



//        // Sequenziale
//        long startTime = System.nanoTime();
//        UndirectedGraphSeq myGraph = new UndirectedGraphSeq(new ArrayList<>(edges));
//        float densest = myGraph.densestSubgraph(epsilon);
//        //float densest = myGraph.densestSubgraphRic((float) 1);
//        long endTime = System.nanoTime();
//        long time = TimeUnit.NANOSECONDS.toMillis(endTime - startTime);
//        System.out.println("Sequential Densest Time: " + time + " ms");
//        //System.out.println("Densest subgraph nodes: " + densest.size());
//        System.out.println("Densest subgraph d: " + densest);
//
////        int degSeq = myGraph.degree(node);
////        System.out.println("Degree Sequential: " + degSeq);
////        System.out.println("dG = " + myGraph.calcDensity(nEdges, nNodes));
//
//        System.out.println();
//
//        long startTimeP = System.nanoTime();
//        UndirectedGraphArrays graphArrays = new UndirectedGraphArrays(new ArrayList<>(edges));
//        graphArrays.setCutoffDegree(cutoffDegree);
//        graphArrays.setCutoffRemove(cutoffRemove);
//        float densestP = graphArrays.densestSubgraph(epsilon);
//        long endTimeP = System.nanoTime();
//        long timeP = TimeUnit.NANOSECONDS.toMillis(endTimeP - startTimeP);
//        System.out.println("Parallel Densest Time: " + timeP + " ms");
//        //System.out.println("Densest subgraph nodes: " + densest.size());
//        //System.out.println("Densest subgraph d: " + graphArrays.getDensity());
//        System.out.println("Densest subgraph d: " + densestP);
//
//        System.out.println();
//        System.out.println("Speedup: " + (float) time/timeP);
//
//        /*ArrayList<Integer> valTempi = new ArrayList<>();
//        for (int i = 0; i < 10; i++) {
//            long startTimeA = System.nanoTime();
//
//            graphArrays.degreeConc();
//
//            long endTimeA = System.nanoTime();
//            long timeA = TimeUnit.NANOSECONDS.toMillis(endTimeA - startTimeA);
//            System.out.println("Array Time: " + timeA + " ms");
//
//            valTempi.add((int) timeA);
//
//            if (i < 9) {
//                graphArrays.setDegreesMap(new ConcurrentHashMap<>(nNodes,
//                    0.99f));
//            }
//        }*/
//
//        //Integer max = valTempi.stream().mapToInt(Integer::intValue).max().getAsInt();
//        //valTempi.remove(max);
//        //int media = valTempi.stream().mapToInt(Integer::intValue).sum() / valTempi.size();
//        //System.out.println("Media tempi: " + media + " ms");
//        //int degPar = graphArrays.degree(node);
//        //System.out.println("Degree Array: " + degPar);
    }
}
