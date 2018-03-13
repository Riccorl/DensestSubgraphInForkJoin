package it.ric.uny.densestsubgraph;

import it.ric.uny.densestsubgraph.utils.GraphParser;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import lombok.Data;

@Data
public class UndirectedGraphSeq implements Graph {

    private static final String COMMENT_CHAR = "#";

    // Numero di archi
    private float nEdges;
    // Numero di nodi
    private float nNodes;
    // ArrayList di archi
    private ArrayList<Edge> edges;
    // Densita del sottografo più denso
    private float density;
    // Nodi sottografo più denso
    private Set<Integer> sTilde;
    // Grado associato ad ogni nodo (u, deg(u)).
    private HashMap<Integer, Set<Integer>> degreesMap;

    public UndirectedGraphSeq(String filename, float nEdges, float nNodes) {

        this.edges = GraphParser.fileToEdge(filename);
        this.degreesMap = new HashMap<>();
        this.sTilde = new HashSet<>();

        this.nEdges = nEdges;
        this.nNodes = nNodes;
    }

    public UndirectedGraphSeq(int nEdges, int nNodes) {
        this.edges = new ArrayList<>();
        this.degreesMap = new HashMap<>();

        this.nEdges = nEdges;
        this.nNodes = nNodes;
    }

    public float densestSubgraphRic(float e) {
        float densityS = calcDensity(nEdges, nNodes);
        HashMap<Integer, Set<Integer>> degreeS = this.degreeSeq(edges);
        Set<Integer> s = new HashSet<>(degreeS.keySet());
        return densestSubgraphRic(edges, s, degreeS, densityS, densityS, e);
    }

    private float densestSubgraphRic(ArrayList<Edge> edges, Set<Integer> s,
        HashMap<Integer, Set<Integer>> degreeS,
        float densityS, float dSTilde, float e) {

        if (degreeS.isEmpty()) {
            return dSTilde;
        }

        degreeS = this.degreeSeq(edges);
        float threshold = 2 * (1 + e) * densityS;
        filter(edges, degreeS, threshold);
        densityS = calcDensity(edges.size() / 2, degreeS.keySet().size());

        if (densityS > dSTilde) {
            dSTilde = densityS;
        }

        return densestSubgraphRic(edges, s, degreeS, densityS, dSTilde, e);
    }

    public float densestSubgraph(float e) {
        Map<Integer, Set<Integer>> degreeS = this.degreeSeq(edges);
        Set<Integer> sTilde = new HashSet<>(degreeS.keySet());

        return densestSubgraph(edges, degreeS, sTilde, e);
    }

    private float densestSubgraph(ArrayList<Edge> edges, Map<Integer, Set<Integer>> degreeS,
        Set<Integer> sTilde, float e) {

        float densityS = calcDensity(nEdges, nNodes);
        float dSTilde = densityS;

        // Itera sugli archi alla ricerca di nodi con grado inferiore a 2*(1 + e) * d(S)
        while (!degreeS.isEmpty()) {

            float threshold = 2 * (1 + e) * densityS;
            // Rimuove archi con grado dei nodi <= threshold
            filter(edges, degreeS, threshold);
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

    private void filter(ArrayList<Edge> list, Map<Integer, Set<Integer>> degreeS,
        double threshold) {
        int inputSize = list.size();
        int outputSize = 0;

        for (int i = 0; i < inputSize; ++i) {
            Edge e = list.get(i);
            int u = e.getU();
            int v = e.getV();

            if (degreeS.get(u).size() > threshold && degreeS.get(v).size() > threshold) {
                list.set(outputSize++, e);
            }
        }
        list.subList(outputSize, inputSize).clear();
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

    private HashMap<Integer, Set<Integer>> degreeSeq(ArrayList<Edge> edges) {
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
