package it.ric.uny.densestsubgraph;

import java.util.ArrayList;
import java.util.concurrent.RecursiveTask;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ParallelArrays extends RecursiveTask<int[]>{

    private final static Logger LOGGER = Logger.getLogger(ParallelArrays.class.getName());

    private static final int CUTOFF = 3000000;

    private ArrayList<Edge> edges;

    private int nodeValue;
    private int nEdge;

    public ParallelArrays(ArrayList<Edge> edges, int nodeValue, int nEdge) {
        this.edges = edges;
        this.nodeValue = nodeValue;
        this.nEdge = nEdge;
    }

    @Override
    protected int[] compute() {

        // Sequential
        if (edges.size() < CUTOFF) {
            int[] degrees = new int[nodeValue+1];

            for (Edge x : edges) {
                degrees[x.getU()] += 1;
            }

            return degrees;
        }

        // Parallel
        double startTimeD = System.nanoTime();

        int nEdgeHalf = nEdge/2;
        ArrayList<Edge> degreeLeft = new ArrayList<>(edges.subList(0, nEdgeHalf));
        ArrayList<Edge> degreeRight = new ArrayList<>(edges.subList(nEdgeHalf, nEdge));

        double endTimeD = System.nanoTime();
        double timeD = (endTimeD - startTimeD)/1000000.0;
        //System.out.println("Arraylist split: " + timeD + "ms");

        ParallelArrays left = new ParallelArrays(degreeLeft, nodeValue, nEdgeHalf);
        ParallelArrays right = new ParallelArrays(degreeRight, nodeValue, nEdgeHalf);

        left.fork();
        int[] resLeft = right.compute();
        int[] resRight = left.join();

        // Merge risultati
        //double startTimeM = System.nanoTime();

        int[] degrees = new int[nodeValue+1];
        for (int i = 0; i <= nodeValue; i++) {
            degrees[i] = resLeft[i] + resRight[i];
        }

        //double endTimeM = System.nanoTime();
        //double timeM = (endTimeM - startTimeM)/1000000.0;

        //LOGGER.log( Level.FINE, "Arraylist Merge: " + timeM + "ms");

        return degrees;
    }

    //-------------------------------------------- GETTER --------------------------------------------


}
