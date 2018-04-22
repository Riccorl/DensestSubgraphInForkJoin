package it.ric.uny.densestsubgraph;

import it.ric.uny.densestsubgraph.model.Edge;
import it.ric.uny.densestsubgraph.parallel.ParallelDegree;
import it.ric.uny.densestsubgraph.parallel.ParallelRemove;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ForkJoinPool;
import lombok.Data;

// Modifica con ConcurrentHashMap
// Source Code: https://goo.gl/tZqrkB
// Link utili:
// https://howtodoinjava.com/core-java/multi-threading/best-practices-for-using-concurrenthashmap/

@Data
public class UndirectedGraph {

    private static final String COMMENT_CHAR = "#";
    private static ForkJoinPool fjPool = new ForkJoinPool();

    // Numero di archi
    private float nEdges;
    // Numero di nodi
    private float nNodes;
    // ArrayList di archi
    private List<Edge> edges;
    //private Edge[] edges;
    // Nodi sottografo più denso
    private Set<Integer> sTilde;
    // Densita del grafo più denso
    private float density;
    // Mappa concorrente dei gradi
    // Grado associato ad ogni nodo (u, deg(u)).
    private ConcurrentHashMap<Integer, Set<Integer>> degreesMap;

    // CUTOFF
    int cutoffDegree = 5000;
    int cutoffRemove = 5000;

    public UndirectedGraph(List<Edge> edges) {
        this.edges = new ArrayList<>(edges);
        this.nEdges = edges.size();
        this.degreesMap = new ConcurrentHashMap<>((int) nNodes);
    }

    public float densestSubgraph(float e) {
        degreesMap = this.degreeConc(edges, edges.size());
        //degreesMap = this.degreeSeq(edges);
        Set<Integer> sTilde = new HashSet<>(degreesMap.keySet());
        float densityS = calcDensity(edges.size() / 2, degreesMap.keySet().size());

        return densestSubgraph(edges, degreesMap, sTilde, densityS, densityS, e);
    }

    private float densestSubgraph(List<Edge> edges,
        ConcurrentHashMap<Integer, Set<Integer>> degreeS,
        Set<Integer> sTilde, float densityS, float densitySTilde, float e) {

        // Itera sugli archi alla ricerca di nodi con grado inferiore a 2*(1 + e) * d(S)
        while (!degreeS.isEmpty()) {

            float threshold = 2 * (1 + e) * densityS;
            //filter(edges, degreeS, threshold);
            int edgesSize = edges.size();
            int clear = fjPool.invoke(new ParallelRemove(edges, degreeS, threshold, cutoffRemove));
            edges.subList(clear, edgesSize).clear();
            degreeS = this.degreeConc(edges, edges.size());
            //degreeS = this.degreeSeq(edges);

            densityS = calcDensity(edges.size() / 2, degreeS.keySet().size());
            if (densityS > densitySTilde) {
                densitySTilde = densityS;
            }
        }

        this.density = densitySTilde;
        this.sTilde = sTilde;
        return density;
    }

    private ConcurrentHashMap<Integer, Set<Integer>> degreeSeq(List<Edge> edges) {
        ConcurrentHashMap<Integer, Set<Integer>> degreesMap = new ConcurrentHashMap<>();
        for (Edge e : edges) {
            degreesMap.putIfAbsent(e.getU(), new HashSet<>());
            degreesMap.putIfAbsent(e.getV(), new HashSet<>());

            degreesMap.get(e.getU()).add(e.getV());
            degreesMap.get(e.getV()).add(e.getU());
        }

        return degreesMap;
    }

    public ConcurrentHashMap<Integer, Set<Integer>> degreeConc(List<Edge> edges, int nEdges) {
        ConcurrentHashMap<Integer, Set<Integer>> degreesMap = new ConcurrentHashMap<>();
        fjPool.invoke(new ParallelDegree(edges, degreesMap, nEdges, cutoffDegree));
        return degreesMap;
    }

    public int degree(int n) {
        //return degrees[n];
        return degreesMap.get(n).size();
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
}
