package it.ric.uny.densestsubgraph.parallel;

import it.ric.uny.densestsubgraph.Edge;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.RecursiveTask;
import lombok.Data;

@Data
public class ParallelRemove extends RecursiveTask<ArrayList<Edge>> {

    private static final int CUTOFF = 5000;

    private ArrayList<Edge> edges;
    private Map<Integer, Set<Integer>> degreeS;
    private float threshold;

    public ParallelRemove(ArrayList<Edge> edges, Map<Integer, Set<Integer>> degreeS,
        float threshold) {

        this.edges = edges;
        this.degreeS = degreeS;
        this.threshold = threshold;
    }

    @Override
    protected ArrayList<Edge> compute() {

        // Sequential
        if (edges.size() < CUTOFF) {
            int inputSize = edges.size();
            int outputSize = 0;

            for (Edge e : edges) {
                if (degreeS.get(e.getU()).size() > threshold && degreeS.get(e.getV()).size() > threshold) {
                    edges.set(outputSize++, e);
                }
            }

            edges.subList(outputSize, inputSize).clear();
            return edges;
        }

        // Parallel

        ParallelRemove left = new ParallelRemove(
            new ArrayList<>(edges.subList(0, edges.size() / 2)), degreeS, threshold);
        ParallelRemove right = new ParallelRemove(
            new ArrayList<>(edges.subList(edges.size() / 2, edges.size())), degreeS, threshold);

        left.fork();
        ArrayList<Edge> edgesRight = right.compute();
        ArrayList<Edge> edgesLeft = left.join();

        edgesLeft.addAll(edgesRight);
        return edgesLeft;
    }
}
