package it.ric.uny.densestsubgraph;

import static java.nio.file.Files.newBufferedReader;

import it.ric.uny.densestsubgraph.parallel.ParallelDegree;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
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
    private int nEdges;
    // Numero di nodi
    private int nNodes;
    // ArrayList di archi
    private ArrayList<Edge> edges;
    //private Edge[] edges;

    // Mappa concorrente dei gradi
    // Grado associato ad ogni nodo (u, deg(u)).
    private ConcurrentHashMap<Integer, Integer> degreeMap;

    public UndirectedGraphArrays(String filename, int nEdges, int nNodes) {

        this.connections = new HashMap<>();
        this.edges = new ArrayList<>();//new Edge[nEdges];//new ArrayList<>();
        this.degreeMap = new ConcurrentHashMap<>(nNodes);

        //long startTime = System.nanoTime();
        this.fileToGraph(filename);

        //long endTime = System.nanoTime();
        //long time = TimeUnit.NANOSECONDS.toMillis(endTime - startTime);

        //System.out.println("Fill Time: " + time + "ms");

        this.nEdges = nEdges;
        this.nNodes = nNodes;
    }

    public void degreeConc() {
        fjPool.invoke(new ParallelDegree(edges, degreeMap, nEdges));
    }

    public int degree(int n) {
        //return degrees[n];
        return degreeMap.get(n);
    }

    /**
     * For undirected simple graphs G = (V,E), the graph density is defined as
     * d = 2|E|/(|V|*(|V| - 1))
     *
     * @return      d
     */
    public int calcDensity() {
        int e = edges.size();
        int v = degreeMap.keySet().size();

        return (2 * e) / (v * (v - 1));
    }

    private void addEdge(int u, int v) {

        connections.putIfAbsent(u, new HashSet<>());
        connections.putIfAbsent(v, new HashSet<>());

        connections.get(u).add(v);
        connections.get(v).add(u);
    }

    private void fillMaps() {

        for (Edge e : edges) {
            //addEdge(e.getU(), e.getV());
        }
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

    public ConcurrentHashMap<Integer, Integer> getDegreeMap() {
        return degreeMap;
    }

    //-------------------------------------------- SETTER ------------------------------------------

    public void setDegreeMap(
        ConcurrentHashMap<Integer, Integer> degreeMap) {
        this.degreeMap = degreeMap;
    }
}
