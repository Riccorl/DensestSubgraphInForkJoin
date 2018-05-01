package it.ric.uny.densestsubgraph;

import it.ric.uny.densestsubgraph.model.Edge;
import it.ric.uny.densestsubgraph.parallel.ParallelDegree;
import it.ric.uny.densestsubgraph.parallel.ParallelDegreeTwo;
import it.ric.uny.densestsubgraph.parallel.ParallelRemove;
import it.ric.uny.densestsubgraph.utils.Utility;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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

    // Numero di archi
    private double nEdges;
    // Numero di nodi
    private double nNodes;
    // ArrayList di archi
    private List<Edge> edges;
    //private Edge[] edges;
    // Nodi sottografo più denso
    private Set<Integer> sTilde;
    // Densita del grafo più denso
    private double density;
    // Mappa concorrente dei gradi
    //private ConcurrentHashMap<Integer, Set<Integer>> degreesMap;
    private Map<Integer, Integer> degreesMap;

    public UndirectedGraph(List<Edge> edges, int nNodes) {
        this.edges = new ArrayList<>(edges);
        this.nEdges = edges.size();
        this.degreesMap = new ConcurrentHashMap<>(nNodes, 0.75f, 64);
    }

    public UndirectedGraph(List<Edge> edges) {
        this(edges, 1000000);
    }

    /**
     * Wrapper per il metodo del calcolo del sottografo più denso
     * @param e     epsilon
     * @return      la densità del sottografo più denso
     */
    public double densestSubgraph(double e) {
        degreesMap = this.nodesDegreeTwo(edges);
        return densestSubgraph(edges, degreesMap, e);
    }


    private double densestSubgraph(List<Edge> edges,
        Map<Integer, Integer> degreeS, double e) {
        // Densità attuale
        double dS = calcDensity(edges.size(), degreeS.keySet().size());
        double dSTilde = dS;
        // Itera sugli archi alla ricerca di nodi con grado inferiore a 2*(1 + e) * d(S)
        while (!edges.isEmpty()) {

            double threshold = 2.0 * (1.0 + e) * dS;
            // Rimuove gli archi tra nodi che hanno grado <= 2*(1 + e) * d(S)
            edges = this.removeEdges(edges, degreeS, threshold);
            // Ricalcola grado di ogni nodo, fork/join
            //degreeS = this.nodesDegree(edges);
            degreeS = this.nodesDegreeTwo(edges);
            // Ricalcola densità attuale
            dS = calcDensity(edges.size(), degreeS.keySet().size());
            // Se la nuova densità è maggiore della massima fino ad ora ->
            //      aggiorna la densità massima
            if (dS > dSTilde) {
                dSTilde = dS;
            }
        }

        this.density = dSTilde;
        return density;
    }

    private List<Edge> removeEdges(List<Edge> edges,
        Map<Integer, Integer> degreeS, double threshold) {
        return ForkJoinPool.commonPool().invoke(new ParallelRemove(edges, degreeS, threshold));
    }

    private ConcurrentHashMap<Integer, Set<Integer>> nodesDegree(List<Edge> edges) {

        // Degrees map
        ConcurrentHashMap<Integer, Set<Integer>> degreesMap =
            new ConcurrentHashMap<>((int) nNodes, 0.75f, 64);
        // Do parallel
        ForkJoinPool.commonPool()
            .invoke(new ParallelDegree(edges, degreesMap));
        // Return results
        return degreesMap;
    }

    private Map<Integer, Integer> nodesDegreeTwo(List<Edge> edges) {
        // Do parallel
        return ForkJoinPool.commonPool()
            .invoke(new ParallelDegreeTwo(edges));
    }

    public ConcurrentHashMap<Integer, Set<Integer>> degreeSeq(List<Edge> edges) {
        ConcurrentHashMap<Integer, Set<Integer>> degreesMap = new ConcurrentHashMap<>();
        for (Edge e : edges) {
            degreesMap.putIfAbsent(e.getU(), new HashSet<>());
            degreesMap.putIfAbsent(e.getV(), new HashSet<>());

            degreesMap.get(e.getU()).add(e.getV());
            degreesMap.get(e.getV()).add(e.getU());
        }

        return degreesMap;
    }

    public int degree(int n) {
        //return degrees[n];
        return degreesMap.get(n);
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
}
