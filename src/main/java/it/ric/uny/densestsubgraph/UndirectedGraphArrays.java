package it.ric.uny.densestsubgraph;

import static java.nio.file.Files.newBufferedReader;

import it.ric.uny.densestsubgraph.parallel.ParallelDegree;
import it.ric.uny.densestsubgraph.parallel.ParallelRemove;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ForkJoinPool;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.Data;

// Modifica con ConcurrentHashMap
// Source Code: https://goo.gl/tZqrkB
// Link utili:
// https://howtodoinjava.com/core-java/multi-threading/best-practices-for-using-concurrenthashmap/

@Data
public class UndirectedGraphArrays {

    private static final String COMMENT_CHAR = "#";
    private static ForkJoinPool fjPool = new ForkJoinPool();

    // Grafo con archi duplicati per ogni nodo, utile per calcolo del grado.
    private HashMap<Integer, HashSet<Integer>> connections;

    // Numero di archi
    private float nEdges;
    // Numero di nodi
    private float nNodes;
    // ArrayList di archi
    private ArrayList<Edge> edges;
    //private Edge[] edges;
    // Insieme dei nodi
    private Set<Integer> nodes;
    // Nodi sottografo più denso
    private Set<Integer> sTilde;
    // Densita del grafo più denso
    private float density;
    // Mappa concorrente dei gradi
    // Grado associato ad ogni nodo (u, deg(u)).
    private ConcurrentHashMap<Integer, Set<Integer>> degreesMap;

    public UndirectedGraphArrays(String filename, float nEdges, float nNodes) {

        this.connections = new HashMap<>();
        this.edges = new ArrayList<>();//new Edge[nEdges];//new ArrayList<>();
        this.nodes = new HashSet<>();
        this.degreesMap = new ConcurrentHashMap<>((int) nNodes, 0.55f);

        this.fileToGraph(filename);

        this.nEdges = nEdges;
        this.nNodes = nNodes;
    }

    public float densestSubgraph(float e) {
        this.degreesMap = this.degreeConc(edges, edges.size());
        Set<Integer> sTilde = new HashSet<>(nodes);
        float densityS = calcDensity(nEdges, nNodes);
        return densestSubgraph(edges, degreesMap, sTilde, densityS, densityS, e);
    }

    private float densestSubgraph(ArrayList<Edge> edges, Map<Integer, Set<Integer>> degreeS,
        Set<Integer> sTilde, float densityS, float densitySTilde, float e) {

        // Itera sugli archi alla ricerca di nodi con grado inferiore a 2*(1 + e) * d(S)
        while (!degreeS.isEmpty()) {

            float threshold = 2 * (1 + e) * densityS;
            //filter(edges, degreeS, threshold);
            edges = fjPool.invoke(new ParallelRemove(edges, degreeS, threshold));

            densityS = calcDensity(edges.size() / 2, degreeS.keySet().size());
            if (densityS > densitySTilde) {
                densitySTilde = densityS;
            }

            degreeS = this.degreeConc(edges, edges.size());
        }

        this.density = densitySTilde;
        this.sTilde = sTilde;
        return density;

       /* float densityS = calcDensity(nEdges, nNodes);
        float dSTilde = densityS;

        // Itera sugli archi alla ricerca di nodi con grado inferiore a
        // 2*(1 + e) * d(S)
        while (!s.isEmpty()) {

            ArrayList<Edge> edgesRemoved = new ArrayList<>(edges);
            Map<Integer, Integer> degreeS = this.degreeConc(edges, edges.size());
            s.retainAll(degreeS.keySet());

            float threshold = 2 * (1 + e) * densityS;

            //

            densityS = calcDensity(edges.size() / 2, s.size());

            if (densityS > dSTilde) {
                sTilde = new HashSet<>(s);
                dSTilde = densityS;
            }
        }

        density = dSTilde;
        return density;*/
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

    public ConcurrentHashMap<Integer, Set<Integer>> degreeConc(ArrayList<Edge> edges, int nEdges) {
        ConcurrentHashMap<Integer, Set<Integer>> degreesMap = new ConcurrentHashMap<>();
        fjPool.invoke(new ParallelDegree(edges, degreesMap, nEdges));
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

    private void addEdge(int u, int v) {

        connections.putIfAbsent(u, new HashSet<>());
        connections.putIfAbsent(v, new HashSet<>());

        connections.get(u).add(v);
        connections.get(v).add(u);
    }

    /**
     * Reads from file and generates data structure for the graph
     *
     * @param filename file to read
     */
    private void fileToGraph(String filename) {

        Pattern pattern = Pattern.compile("^([\\d]*)\\s([\\d]*)");
        int i = 0;

        try (BufferedReader br = newBufferedReader(Paths.get(filename), StandardCharsets.UTF_8)) {
            for (String line = null; (line = br.readLine()) != null; ) {

                Matcher matcher = pattern.matcher(line);
                if (matcher.matches()) {
                    int u = Integer.parseInt(matcher.group(1));
                    int v = Integer.parseInt(matcher.group(2));
                    nodes.add(u);
                    nodes.add(v);
                    edges.add(new Edge(u, v));
                    //edges[i] = new Edge(u, v);
                    //i++;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
