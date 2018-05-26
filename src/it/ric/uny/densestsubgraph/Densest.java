package it.ric.uny.densestsubgraph;

import it.ric.uny.densestsubgraph.model.Edge;
import it.ric.uny.densestsubgraph.model.UndirectedGraph;
import it.ric.uny.densestsubgraph.parallel.ParallelDegree;
import it.ric.uny.densestsubgraph.parallel.ParallelRemove;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ForkJoinPool;

public class Densest {

    // -------------------------------------- PARALLEL --------------------------------------------

    /**
     * {@link Densest#densestSubgraphParallel(List, Map, double)} wrapper. Search for
     * the densest subgraph
     *
     * @param e epsilon
     * @return density of the densest subgraph
     */
    public double densestSubgraphParallel(UndirectedGraph graph, double e) {
        Map<Integer, Integer> degreesMap = this.nodesDegreeParallel(graph.getEdges());
        return densestSubgraphParallel(graph.getEdges(), degreesMap, e);
    }


    /**
     * Compute the density of the densest subgraph, in parallel
     *
     * @param edges list of edges
     * @param degreeS map (node, deg(node))
     * @param e approximation factor
     * @return density of the densest subgraph
     */
    private double densestSubgraphParallel(List<Edge> edges,
        Map<Integer, Integer> degreeS, double e) {
        // Actual density
        double dS = (double) edges.size() / (double) degreeS.keySet().size();
        double dSTilde = dS;
        // Search for edges with at least one node that have degree <= 2 * (1 + e) * d(S)
        while (!edges.isEmpty()) {

            double threshold = 2.0 * (1.0 + e) * dS;
            // Removes edges with one node wth degree <= 2*(1 + e) * d(S)
            edges = this.removeEdgesParallel(edges, degreeS, threshold);
            // Computes degree of each node
            degreeS = this.nodesDegreeParallel(edges);
            // Compute density
            dS = (double) edges.size() / (double) degreeS.keySet().size();
            // If the new density is greater than max ->
            //      update max density
            if (dS > dSTilde) {
                dSTilde = dS;
            }
        }
        return dSTilde;
    }

    /**
     * Removes edges with at least one node that have degree <= 2 * (1 + e) * d(S), in parallel
     *
     * @param edges list of edges
     * @param degreeS map (node, deg(node))
     * @param threshold 2 * (1 + e) * d(S)
     * @return updated list of edges
     */
    private List<Edge> removeEdgesParallel(List<Edge> edges,
        Map<Integer, Integer> degreeS, double threshold) {
        return ForkJoinPool.commonPool().invoke(new ParallelRemove(edges, degreeS, threshold));
    }

    /**
     * Computes degree of nodes, in parallel
     *
     * @param edges list of edges
     * @return map (node, deg(node))
     */
    private Map<Integer, Integer> nodesDegreeParallel(List<Edge> edges) {
        return ForkJoinPool.commonPool().invoke(new ParallelDegree(edges));
    }

    // -------------------------------------- SEQUENTIAL ------------------------------------------

    /**
     * {@link Densest#densestSubgraphSequential(List, Map, double)} wrapper. Search for
     * the densest subgraph sequantially
     *
     * @param e epsilon
     * @return density of the densest subgraph
     */
    public double densestSubgraphSequential(UndirectedGraph graph, double e) {
        Map<Integer, Integer> degreeS = this.nodesDegreeSequential(graph.getEdges());
        return densestSubgraphSequential(graph.getEdges(), degreeS, e);
    }

    /**
     * Compute the density of the densest subgraph, sequantially
     *
     * @param edges list of edges
     * @param degreeS map (node, deg(node))
     * @param e approximation factor
     * @return density of the densest subgraph
     */
    private double densestSubgraphSequential(List<Edge> edges, Map<Integer, Integer> degreeS,
        double e) {

        // Actual density
        double dS = (double) edges.size() / (double) degreeS.keySet().size();
        double dSTilde = dS;
        // Search for edges with at least one node that have degree <= 2 * (1 + e) * d(S)
        while (!edges.isEmpty()) {

            double threshold = 2.0 * (1.0 + e) * dS;
            // Removes edges with one node wth degree <= 2*(1 + e) * d(S)
            edges = this.removeEdgesSequential(edges, degreeS, threshold);
            // Computes degree of each node
            degreeS = this.nodesDegreeSequential(edges);
            // Compute density
            dS = (double) edges.size() / (double) degreeS.keySet().size();
            // If the new density is greater than max ->
            //      update max density
            if (dS > dSTilde) {
                dSTilde = dS;
            }
        }
        return dSTilde;
    }

    /**
     * Removes edges with at least one node that have degree <= 2 * (1 + e) * d(S), sequantially
     *
     * @param edges list of edges
     * @param degreeS map (node, deg(node))
     * @param threshold 2 * (1 + e) * d(S)
     * @return updated list of edges
     */
    public List<Edge> removeEdgesSequential(List<Edge> edges, Map<Integer, Integer> degreeS,
        double threshold) {
        List<Edge> newEdge = new ArrayList<>();
        for (Edge edge : edges) {
            int u = edge.getU();
            int v = edge.getV();

            if (degreeS.get(u) > threshold && degreeS.get(v) > threshold) {
                newEdge.add(edge);
            }
        }
        return newEdge;
    }

    /**
     * Computes degree of nodes, sequantially
     *
     * @param edges list of edges
     * @return map (node, deg(node))
     */
    public Map<Integer, Integer> nodesDegreeSequential(List<Edge> edges) {
        Map<Integer, Integer> degreesMap = new HashMap<>();
        for (Edge e : edges) {
            degreesMap.putIfAbsent(e.getU(), 0);
            degreesMap.putIfAbsent(e.getV(), 0);

            degreesMap.put(e.getU(), degreesMap.get(e.getU()) + 1);
            degreesMap.put(e.getV(), degreesMap.get(e.getV()) + 1);
        }

        return degreesMap;
    }

}
