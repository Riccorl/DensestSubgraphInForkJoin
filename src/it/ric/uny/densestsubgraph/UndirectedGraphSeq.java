package it.ric.uny.densestsubgraph;

import it.ric.uny.densestsubgraph.model.Edge;
import it.ric.uny.densestsubgraph.utils.Utility;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.Data;

@Data
public class UndirectedGraphSeq implements Graph {

    //private static final Logger logger = LoggerFactory.getLogger(UndirectedGraphSeq.class);


    // Numero di archi
    private double nEdges;
    // Numero di nodi
    private double nNodes;
    // ArrayList di archi
    private List<Edge> edges;
    // Densita del sottografo più denso
    private double density;
    // Nodi sottografo più denso
    private Set<Integer> sTilde;
    // Grado associato ad ogni nodo (u, deg(u)).
    private HashMap<Integer, Set<Integer>> degreesMap;

    public UndirectedGraphSeq(List<Edge> edges) {
        this.edges = new ArrayList<>(edges);
        this.degreesMap = new HashMap<>();

        this.nEdges = edges.size();
    }

    public double densestSubgraphRic(double e) {
        Map<Integer, Set<Integer>> degreeS = this.degreeSeq(edges);
        double dS = Utility.round(calcDensity(edges.size() / 2 , degreeS.size()), 2);
        return densestSubgraphRic(edges, degreeS, dS, dS, e);
    }

    private double densestSubgraphRic(List<Edge> edges,
        Map<Integer, Set<Integer>> degreeS,
        double dS, double dSTilde, double e) {

        if (degreeS.isEmpty()) {
            return dSTilde;
        }

        degreeS = this.degreeSeq(edges);
        if (edges.isEmpty()) return dSTilde;
        double threshold = Utility.round(2 * (1 + e) * dS, 2);
        Utility.filter(edges, degreeS, threshold);
        dS = Utility.round(calcDensity(edges.size() / 2 , degreeS.size()), 2);

        if (dS > dSTilde) {
            dSTilde = dS;
        }

        return densestSubgraphRic(edges, degreeS, dS, dSTilde, e);
    }

    public double densestSubgraph(double e) {
        Map<Integer, Set<Integer>> degreeS = this.degreeSeq(edges);
        Set<Integer> sTilde = new HashSet<>(degreeS.keySet());

        return densestSubgraph(edges, degreeS, sTilde, e);
    }

    private double densestSubgraph(List<Edge> edges, Map<Integer, Set<Integer>> degreeS,
        Set<Integer> sTilde, double e) {

        double dS = calcDensity(edges.size() / 2, degreeS.keySet().size());
        double dSTilde = dS;

        // Itera sugli archi alla ricerca di nodi con grado inferiore a 2*(1 + e) * d(S)
        while (!degreeS.keySet().isEmpty()) {

            double threshold = 2 * (1 + e) * dS;
            // Rimuove archi con grado dei nodi <= 2 * (1 + e) * d(S)
            //filter(edges, degreeS, threshold);
            //edges = this.removeEdgesSlower(edges, degreeS, threshold);
            edges = this.removeEdges(edges, degreeS, threshold);
            // Aggiorna il grado di ogni nodo
            degreeS = this.degreeSeq(edges);
            dS = calcDensity(edges.size() / 2, degreeS.keySet().size());

            if (dS > dSTilde) {
                sTilde = new HashSet<>(degreeS.keySet());
                dSTilde = dS;
            }
        }

        this.density = dSTilde;
        this.sTilde = sTilde;
        return density;
    }

    public List<Edge> removeEdgesSlower(List<Edge> edges, Map<Integer, Set<Integer>> degreeS,
        double threshold) {
        List<Edge> newEdge = new ArrayList<>(edges);
        for (Edge edge : edges) {
            int u = edge.getU();
            int v = edge.getV();

            if (degreeS.get(u).size() <= threshold || degreeS.get(v).size() <= threshold) {
                newEdge.remove(edge);
            }
        }
        return newEdge;
    }

    public List<Edge> removeEdges(List<Edge> edges, Map<Integer, Set<Integer>> degreeS,
        double threshold) {
        List<Edge> newEdge = new ArrayList<>();
        for (Edge edge : edges) {
            int u = edge.getU();
            int v = edge.getV();

            if (degreeS.get(u).size() > threshold && degreeS.get(v).size() > threshold) {
                newEdge.add(edge);
            }
        }
        return newEdge;
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
        return degreesMap.get(n).size();
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
