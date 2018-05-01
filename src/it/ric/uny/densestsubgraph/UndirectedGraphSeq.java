package it.ric.uny.densestsubgraph;

import it.ric.uny.densestsubgraph.model.Edge;
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

    public double densestSubgraph(double e) {
        Map<Integer, Set<Integer>> degreeS = this.degreeSeq(edges);
        return densestSubgraph(edges, degreeS, e);
    }

    private double densestSubgraph(List<Edge> edges, Map<Integer, Set<Integer>> degreeS, double e) {

        double dS = calcDensity(edges.size(), degreeS.keySet().size());
        double dSTilde = dS;

        // Itera sugli archi alla ricerca di nodi con grado inferiore a 2*(1 + e) * d(S)
        while (!edges.isEmpty()) {

            double threshold = 2d * (1d + e) * dS;
            // Rimuove archi con grado dei nodi <= 2 * (1 + e) * d(S)
            edges = this.removeEdges(edges, degreeS, threshold);
            // Aggiorna il grado di ogni nodo
            degreeS = this.degreeSeq(edges);
            // Ricalcola la densità
            dS = calcDensity(edges.size(), degreeS.keySet().size());
            if (dS > dSTilde) {
                dSTilde = dS;
            }

        }

        this.density = dSTilde;
        return density;
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

    /**
     * For undirected simple graphs G = (V,E), and S a subset of G,
     * the graph density is defined as d = |E(S)| / |S|
     *
     * @return d    density of the graph S
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

}
