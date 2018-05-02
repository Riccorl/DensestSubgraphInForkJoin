package it.ric.uny.densestsubgraph;

import it.ric.uny.densestsubgraph.model.Edge;
import it.ric.uny.densestsubgraph.parallel.ParallelDegree;
import it.ric.uny.densestsubgraph.parallel.ParallelRemove;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ForkJoinPool;
import lombok.Data;

@Data
public class UndirectedGraph {

    // Numero di archi
    private double nEdges;
    // Numero di nodi
    private double nNodes;
    // ArrayList di archi
    private List<Edge> edges;
    // Mappa dei gradi dei nodi
    private Map<Integer, Integer> degreesMap;
    // Nodi sottografo più denso
    private Set<Integer> sTilde;
    // Densita del grafo più denso
    private double density;

    public UndirectedGraph(List<Edge> edges) {
        this.edges = new ArrayList<>(edges);
        this.nEdges = edges.size();
    }

    /**
     * Wrapper per il metodo del calcolo del sottografo più denso
     *
     * @param e epsilon
     * @return la densità del sottografo più denso
     */
    public double densestSubgraph(double e) {
        degreesMap = this.nodesDegree(edges);
        return densestSubgraph(edges, degreesMap, e);
    }


    /**
     * Calcola la densità del sottografo più denso
     * @param edges     lista di arcchi
     * @param degreeS   mappa del grado dei nodi
     * @param e         fattore di approssimazione
     * @return          densità del sottografo più denso
     */
    private double densestSubgraph(List<Edge> edges,
        Map<Integer, Integer> degreeS, double e) {
        // Densità attuale
        double dS = calcDensity(edges.size(), degreeS.keySet().size());
        double dSTilde = dS;
        // Itera sugli archi alla ricerca di nodi con grado inferiore a 2 * (1 + e) * d(S)
        while (!edges.isEmpty()) {

            double threshold = 2.0 * (1.0 + e) * dS;
            // Rimuove gli archi tra nodi che hanno grado <= 2*(1 + e) * d(S)
            edges = this.removeEdges(edges, degreeS, threshold);
            // Ricalcola grado di ogni nodo, fork/join
            degreeS = this.nodesDegree(edges);
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

    /**
     * Rimuove gli archi che hanno almeno un nodo con grado inferiore a threshold
     *
     * @param edges         lista di archi
     * @param degreeS       mappa del grado dei nodi
     * @param threshold     soglia minima del grado dei nodi
     * @return              la lista degli archi aggiornata
     */
    private List<Edge> removeEdges(List<Edge> edges,
        Map<Integer, Integer> degreeS, double threshold) {
        return ForkJoinPool.commonPool().invoke(new ParallelRemove(edges, degreeS, threshold));
    }

    /**
     * Calcola il grado dei nodi a partire dagli archi
     * @param edges     lista di archi
     * @return          Mappa del grado dei nodi, nella forma (u, deg(u))
     */
    private Map<Integer, Integer> nodesDegree(List<Edge> edges) {
        return ForkJoinPool.commonPool().invoke(new ParallelDegree(edges));
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
