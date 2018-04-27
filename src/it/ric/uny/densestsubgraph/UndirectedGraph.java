package it.ric.uny.densestsubgraph;

import it.ric.uny.densestsubgraph.model.Edge;
import it.ric.uny.densestsubgraph.parallel.ParallelDegree;
import it.ric.uny.densestsubgraph.parallel.ParallelRemove;
import java.util.ArrayList;
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

    public UndirectedGraph(List<Edge> edges, int nNodes) {
        this.edges = new ArrayList<>(edges);
        this.nEdges = edges.size();
        this.degreesMap = new ConcurrentHashMap<>(nNodes, 0.75f, 256);
    }

    public UndirectedGraph(List<Edge> edges) {
        this(edges, 1000000);
    }

    public double densestSubgraph(double e) {
        degreesMap = this.degreeConc(edges);
        double densityS = calcDensity(edges.size() / 2.0, degreesMap.keySet().size());

        return densestSubgraph(edges, degreesMap, densityS, densityS, e);
    }


    private double densestSubgraph(List<Edge> edges,
        ConcurrentHashMap<Integer, Set<Integer>> degreeS, double densityS,
        double densitySTilde, double e) {

        // Itera sugli archi alla ricerca di nodi con grado inferiore a 2*(1 + e) * d(S)
        while (!degreeS.isEmpty()) {

            double threshold = 2.0 * (1.0 + e) * densityS;
            // Rimuove gli archi tra nodi che hanno grado <= 2*(1 + e) * d(S)
            edges = ForkJoinPool.commonPool().invoke(new ParallelRemove(edges, degreeS, threshold));
            // Ricalcola grado di ogni nodo, fork/join
            degreeS = this.degreeConc(edges);
            // Ricalcola densità attuale
            densityS = calcDensity(edges.size() / 2.0, degreeS.keySet().size());
            // Se la nuova densità è maggiore della massima fino ad ora ->
            //      aggiorna la densità massima
            if (densityS > densitySTilde) {
                densitySTilde = densityS;
            }
        }

        this.density = densitySTilde;
        return density;
    }

    public ConcurrentHashMap<Integer, Set<Integer>> degreeConc(List<Edge> edges) {

        ConcurrentHashMap<Integer, Set<Integer>> degreesMap =
            new ConcurrentHashMap<>((int) nNodes, 0.75f, 256);

        ForkJoinPool.commonPool()
            .invoke(new ParallelDegree(edges, degreesMap));

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
    private double calcDensity(double nEdges, double nNodes) {
        return nEdges / nNodes;
    }
}
