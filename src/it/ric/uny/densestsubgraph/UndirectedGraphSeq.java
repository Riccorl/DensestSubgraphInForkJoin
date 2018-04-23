package it.ric.uny.densestsubgraph;

import it.ric.uny.densestsubgraph.Model.Edge;

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
    private float nEdges;
    // Numero di nodi
    private float nNodes;
    // ArrayList di archi
    private List<Edge> edges;
    // Densita del sottografo più denso
    private float density;
    // Nodi sottografo più denso
    private Set<Integer> sTilde;
    // Grado associato ad ogni nodo (u, deg(u)).
    private HashMap<Integer, Set<Integer>> degreesMap;

    public UndirectedGraphSeq(List<Edge> edges) {
        this.edges = new ArrayList<>(edges);
        this.degreesMap = new HashMap<>();

        this.nEdges = edges.size();
    }

    public float densestSubgraph(float e) {
        Map<Integer, Set<Integer>> degreeS = this.degreeSeq(edges);
        Set<Integer> sTilde = new HashSet<>(degreeS.keySet());

        return densestSubgraph(edges, degreeS, sTilde, e);
    }

    private float densestSubgraph(List<Edge> edges, Map<Integer, Set<Integer>> degreeS,
        Set<Integer> sTilde, float e) {

        float densityS = calcDensity(edges.size() / 2, degreeS.keySet().size());
        float dSTilde = densityS;

        // Itera sugli archi alla ricerca di nodi con grado inferiore a 2*(1 + e) * d(S)
        while (!degreeS.isEmpty()) {

            float threshold = 2 * (1 + e) * densityS;
            // Rimuove archi con grado dei nodi <= 2*(1 + e) * d(S)
            //Utility.filter(edges, degreeS, threshold);
            edges = this.removeEdges(edges, degreeS, threshold);
            // Aggiorna il grado di ogni nodo
            degreeS = this.degreeSeq(edges);
            densityS = calcDensity(edges.size() / 2, degreeS.keySet().size());

            if (densityS > dSTilde) {
                sTilde = new HashSet<>(degreeS.keySet());
                dSTilde = densityS;
            }
        }

        this.density = dSTilde;
        this.sTilde = sTilde;
        return density;
    }

    public List<Edge> removeEdges(List<Edge> edges, Map<Integer, Set<Integer>> degreeS,
        float threshold) {
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
    private float calcDensity(float nEdges, float nNodes) {
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

    public HashMap<Integer, Set<Integer>> degreeSeq(List<Edge> edges) {
        HashMap<Integer, Set<Integer>> degreesMap = new HashMap<>();
        for (Edge e : edges) {
            degreesMap.putIfAbsent(e.getU(), new HashSet<>());
            degreesMap.putIfAbsent(e.getV(), new HashSet<>());

            degreesMap.get(e.getU()).add(e.getV());
            degreesMap.get(e.getV()).add(e.getU());
        }

        return degreesMap;
    }
}
