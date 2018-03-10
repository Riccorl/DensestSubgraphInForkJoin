package it.ric.uny.densestsubgraph;

import static java.nio.file.Files.newBufferedReader;

import it.ric.uny.densestsubgraph.parallel.ParallelDegree;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ForkJoinPool;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// Modifica con ConcurrentHashMap
// Source Code: https://goo.gl/tZqrkB
// Link utili:
// https://howtodoinjava.com/core-java/multi-threading/best-practices-for-using-concurrenthashmap/

public class UndirectedGraphArrays {

    private static final String COMMENT_CHAR = "#";
    private static ForkJoinPool fjPool = new ForkJoinPool();

    // Grafo con archi duplicati per ogni nodo, utile per calcolo del grado.
    private HashMap<Integer, HashSet<Integer>> connections;

    // Numero di archi
    private double nEdges;
    // Numero di nodi
    private double nNodes;
    // ArrayList di archi
    private ArrayList<Edge> edges;
    //private Edge[] edges;

    // Mappa concorrente dei gradi
    // Grado associato ad ogni nodo (u, deg(u)).
    private ConcurrentHashMap<Integer, Integer> degreesMap;

    public UndirectedGraphArrays(String filename, double nEdges, double nNodes) {

        this.connections = new HashMap<>();
        this.edges = new ArrayList<>();//new Edge[nEdges];//new ArrayList<>();
        this.degreesMap = new ConcurrentHashMap<>((int) nNodes, 0.55f);

        //long startTime = System.nanoTime();
        this.fileToGraph(filename);

        //long endTime = System.nanoTime();
        //long time = TimeUnit.NANOSECONDS.toMillis(endTime - startTime);

        //System.out.println("Fill Time: " + time + "ms");

        this.nEdges = nEdges;
        this.nNodes = nNodes;
    }

    public Set<Integer> densestSubgraph(double e) {
        this.degreesMap = this.degreeConc(edges, new ConcurrentHashMap<>(), edges.size());

        Set<Integer> s = degreesMap.keySet();
        Set<Integer> sTilde = degreesMap.keySet();
        double d = calcDensity(nEdges, nNodes);

        return densestSubgraph(edges, s, sTilde, d, e);
    }

    private Set<Integer> densestSubgraph(ArrayList<Edge> edges, Set<Integer> s,
        Set<Integer> sTilde, double dSTilde, double e) {

        double threshold = 2 * (1 + e) * dSTilde;
        Set<Integer> aS = new HashSet<>();
        ArrayList<Edge> edgesRemoved = new ArrayList<>();

        // Itera sugli archi alla ricerca di nodi con grado inferiore a
        // 2*(1 + e) * d(S)
        while (!s.isEmpty()) {
            this.degreesMap = this.degreeConc(edges, new ConcurrentHashMap<>(), edges.size());
            for (Edge edge : edges) {
                if (this.degree(edge.getU()) <= threshold
                    || this.degree(edge.getV()) <= threshold) {

                    if (this.degree(edge.getU()) <= threshold) {
                        aS.add(edge.getU());
                    }

                    if (this.degree(edge.getV()) <= threshold) {
                        aS.add(edge.getV());
                    }

                    edgesRemoved.add(edge);
                }
            }

            s.removeAll(aS);
            edges.removeAll(edgesRemoved);

            double densityS = calcDensity(edges.size(), s.size());

            if (densityS > dSTilde) {
                sTilde = new HashSet<>(s);
                dSTilde = densityS;
            }

            aS.clear();
            edgesRemoved.clear();
        }

        return sTilde;
    }

    public ConcurrentHashMap<Integer, Integer> degreeConc(ArrayList<Edge> edges,
        ConcurrentHashMap<Integer, Integer> degreesMap,
        int nEdges) {
        fjPool.invoke(new ParallelDegree(edges, degreesMap, nEdges));
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
                    edges.add(new Edge(u, v));
                    //edges[i] = new Edge(u, v);
                    //i++;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //-------------------------------------------- GETTER --------------------------------------------


    public HashMap<Integer, HashSet<Integer>> getConnections() {
        return connections;
    }

    public ArrayList<Edge> getEdges() {
        return null;
    }

    public HashSet<Integer> getNodes() {
        return null;
    }

    public ConcurrentHashMap<Integer, Integer> getDegreesMap() {
        return degreesMap;
    }

    //-------------------------------------------- SETTER ------------------------------------------

    public void setDegreesMap(
        ConcurrentHashMap<Integer, Integer> degreesMap) {
        this.degreesMap = degreesMap;
    }
}
