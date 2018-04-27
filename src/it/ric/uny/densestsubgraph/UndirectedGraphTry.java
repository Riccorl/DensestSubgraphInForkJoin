package it.ric.uny.densestsubgraph;

import com.google.common.graph.MutableGraph;
import it.ric.uny.densestsubgraph.model.Edge;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.Data;

@Data
public class UndirectedGraphTry implements Graph {

    private MutableGraph<Integer> mutableGraph;

    public UndirectedGraphTry(MutableGraph<Integer> mutableGraph) {
        this.mutableGraph = mutableGraph;
    }

    public double densestSubgraph(double e) {

        double nEdges = mutableGraph.edges().size();
        double nNodes = mutableGraph.nodes().size();
        double dS = nEdges / nNodes;
        double dSTilde = dS;
        // Itera sugli archi alla ricerca di nodi con grado inferiore a 2*(1 + e) * d(S)
        while (!mutableGraph.nodes().isEmpty()) {

            double threshold = (2.0 + 2.0 * e) * dS;
            // Rimuove archi con grado dei nodi <= 2 * (1 + e) * d(S)
            Set<Integer> aS = new HashSet<>();
            for (int i : mutableGraph.nodes()) {
                if (mutableGraph.degree(i) <= threshold) {
                    aS.add(i);
                }
            }

            for (int i : aS) {
                mutableGraph.removeNode(i);
            }

            // Ricalcola la densità
            nEdges = mutableGraph.edges().size();
            nNodes = mutableGraph.nodes().size();
            dS = calcDensity(nEdges, nNodes);

            if (dS > dSTilde) {
                dSTilde = dS;
            }
        }
        return dSTilde;
    }

    public List<Edge> removeEdges(List<Edge> edges, Map<Integer, Set<Integer>> degreeS,
        Set<Integer> s, double threshold) {
        List<Edge> newEdge = new ArrayList<>(edges);
        for (Edge edge : newEdge) {
            int u = edge.getU();
            int v = edge.getV();

            if (degreeS.get(u).size() <= threshold || degreeS.get(v).size() <= threshold) {
                if (degreeS.get(u).size() <= threshold) {
                    s.remove(u);
                }
                if (degreeS.get(v).size() <= threshold) {
                    s.remove(v);
                }
                edges.remove(edge);
            }
        }
        return edges;
    }

    /**
     * For undirected simple graphs G = (V,E), and S a subset of G,
     * the graph density is defined as d = |E(S)| / |S|
     *
     * @return d
     */
    private double calcDensity(double nEdges, double nNodes) {
        return nEdges / nNodes;
    }

    /**
     * @param n node in input
     * @return degree of node n
     */
    @Override
    public int degree(int n) {
        return 0;
    }

    public Map<Integer, Set<Integer>> degreeSeq(List<Edge> edges) {
        Map<Integer, Set<Integer>> degreesMap = new HashMap<>();
        for (Edge e : edges) {
            degreesMap.putIfAbsent(e.getU(), new HashSet<>());
            degreesMap.putIfAbsent(e.getV(), new HashSet<>());

            degreesMap.get(e.getU()).add(e.getV());
            degreesMap.get(e.getV()).add(e.getU());
        }

        return degreesMap;
    }
}
