package it.ric.uny.densestsubgraph;

import it.ric.uny.densestsubgraph.model.Edge;
import it.ric.uny.densestsubgraph.parallel.ParallelDegree;
import it.ric.uny.densestsubgraph.parallel.ParallelRemove;
import it.ric.uny.densestsubgraph.utils.Utility;
import java.util.ArrayList;
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

    private static ForkJoinPool fjPool = ForkJoinPool.commonPool();
    // CUTOFF
    int cutoffDegree = 5000;
    int cutoffRemove = 1000000;
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
    // Grado associato ad ogni nodo (u, deg(u)).
    private ConcurrentHashMap<Integer, Set<Integer>> degreesMap;

    public UndirectedGraph(List<Edge> edges) {
        this.edges = new ArrayList<>(edges);
        this.nEdges = edges.size();
        this.degreesMap = new ConcurrentHashMap<>((int) nNodes, 0.75f, 64);
    }

    public double densestSubgraph(double e) {
        degreesMap = this.degreeConc(edges, edges.size());
        Set<Integer> sTilde = new HashSet<>(degreesMap.keySet());
        double densityS = Utility.round(calcDensity(edges.size() / 2, degreesMap.keySet().size()), 2);

        return densestSubgraph(edges, degreesMap, sTilde, densityS, densityS, e);
    }


    private double densestSubgraph(List<Edge> edges,
        ConcurrentHashMap<Integer, Set<Integer>> degreeS,
        Set<Integer> sTilde, double densityS, double densitySTilde, double e) {
        int counter = 0;
        // Itera sugli archi alla ricerca di nodi con grado inferiore a 2*(1 + e) * d(S)
        while (!degreeS.isEmpty()) {

            double threshold = Utility.round(2 * (1 + e) * densityS, 2);
            // Rimuove gli archi tra nodi che hanno grado <= 2*(1 + e) * d(S)
//            if (edges.size() - counter  < 10000) {
//                //edges = this.removeEdges(edges, degreeS, threshold);
//                Utility.filter(edges, degreeS, threshold);
//                degreeS = this.degreeConc(edges, edges.size());
//                densityS = calcDensity(edges.size() / 2, degreeS.keySet().size());
////                degreeS = this.degreeSeq(edges);
//            } else {
//                counter += fjPool.invoke(new ParallelRemove(edges, degreeS, threshold));
//                degreeS = this.degreeConc(edges, edges.size());
//                densityS = calcDensity((edges.size() - counter) / 2, degreeS.keySet().size());
////                edges = fjPool.invoke(new ParallelRemove(edges, degreeS, threshold));
////                degreeS = this.degreeConc(edges, edges.size());
//            }
            counter += fjPool.invoke(new ParallelRemove(edges, degreeS, threshold));
            // Ricalcola grado di ogni nodo, fork/join
            degreeS = this.degreeConc(edges, edges.size());

            // Controllo su grandezza della lista di archi
            if (edges.size() - counter <= 0) {
                return densitySTilde;
            }
            // Ricalcola densità attuale
            densityS = Utility
                .round(calcDensity((edges.size() - counter) / 2, degreeS.keySet().size()), 2);
//            densityS = calcDensity(edges.size() / 2, degreeS.keySet().size());
            // Se la nuova densità è maggiore della massima fino ad ora ->
            //      aggiorna la densità massima
            if (densityS > densitySTilde) {
                densitySTilde = densityS;
            }
        }

        this.density = densitySTilde;
        this.sTilde = sTilde;
        return density;
    }

    public ConcurrentHashMap<Integer, Set<Integer>> degreeConc(List<Edge> edges, int nEdges) {
        ConcurrentHashMap<Integer, Set<Integer>> degreesMap =
            new ConcurrentHashMap<>((int) nNodes, 0.75f, 64);
        fjPool.invoke(new ParallelDegree(edges, degreesMap, nEdges, cutoffDegree));
        return degreesMap;
    }

    private ConcurrentHashMap<Integer, Set<Integer>> degreeSeq(List<Edge> edges) {
        ConcurrentHashMap<Integer, Set<Integer>> degreesMap = new ConcurrentHashMap<>();
        for (Edge e : edges) {
            if (e == null) {
                continue;
            }
            degreesMap.putIfAbsent(e.getU(), new HashSet<>());
            degreesMap.putIfAbsent(e.getV(), new HashSet<>());

            degreesMap.get(e.getU()).add(e.getV());
            degreesMap.get(e.getV()).add(e.getU());
        }

        return degreesMap;
    }

    public List<Edge> removeEdges(List<Edge> edges, Map<Integer, Set<Integer>> degreeS,
        double threshold) {
        List<Edge> newEdge = new ArrayList<>(edges.size());
        for (Edge edge : edges) {
            int u = edge.getU();
            int v = edge.getV();

            if (degreeS.get(u).size() > threshold && degreeS.get(v).size() > threshold) {
                newEdge.add(edge);
            }
        }
        return newEdge;
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
    private double calcDensity(double nEdges, double nNodes) {
        return nEdges / nNodes;
    }
}
