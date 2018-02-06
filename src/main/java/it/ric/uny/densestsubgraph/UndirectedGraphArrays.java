package it.ric.uny.densestsubgraph;

import static java.nio.file.Files.newBufferedReader;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ForkJoinPool;

// Modifica con ConcurrentHashMap
// Source Code: https://goo.gl/tZqrkB

public class UndirectedGraphArrays {

    private static final String COMMENT_CHAR = "#";
    private static ForkJoinPool fjPool = new ForkJoinPool();
    private static int INIT_SIZE = 10000000;


    // Grafo con archi duplicati per ogni nodo, utile per calcolo del grado.
    private HashMap<Integer, HashSet<Integer>> connections;

    // Numero di Archi
    private int nEdges;
    // ArrayList di archi
    private ArrayList<Edge> edges;
    // ArrayList di nodi

    // Mappa concorrente dei gradi
    private ConcurrentHashMap<Integer, Integer> degreeMap;

    // Grado associato ad ogni nodo (u, deg(u)).
    private int[] degrees;
    private int maxNodeValue;


    public UndirectedGraphArrays(String filename, int nEdges) {

        this.connections = new HashMap<>();
        this.edges = new ArrayList<>();
        this.degreeMap = new ConcurrentHashMap<>(INIT_SIZE, 0.75f, 8);

        this.fileToGraph(filename);

        this.nEdges = nEdges;
    }

    public void degreeConc() {
        fjPool.invoke(new ParallelCon(edges, degreeMap, nEdges));
    }

    public int degree(int n) {
        //return degrees[n];
        return degreeMap.get(n);
    }

    public void degreePrepare() {
        degrees = new int[maxNodeValue+1];

        for (Edge x : edges) {
            degrees[x.getU()] += 1;
        }
    }

    /**
     * Wrapper for prepareParallel method
     */
    public void degreePrepareParallel() {
        this.degrees = prepareParallel();
    }

    /**
     * Precalculation of all nodes' degree in parallel
     *
     * @return degreeMap with degrees
     */
    private int[] prepareParallel() {

        return fjPool.invoke(new ParallelArrays(edges, maxNodeValue, nEdges));
    }

    /**
     * Reads from file and generates data structure for the graph
     *
     * @param filename file to read
     */
    private void fileToGraph(String filename) {

        try (BufferedReader br = newBufferedReader(Paths.get(filename),
            StandardCharsets.UTF_8)) {
            for (String line = null; (line = br.readLine()) != null; ) {

                if (line.startsWith(COMMENT_CHAR)) {
                    continue;
                }

                String[] row = line.split("[\t ]");

                int u = Integer.parseInt(row[0]);
                int v = Integer.parseInt(row[1]);

                degreeMap.putIfAbsent(u, 0);
                degreeMap.putIfAbsent(v, 0);
                edges.add(new Edge(u, v));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Lettura ok");
    }

    private void addEdge(int u, int v) {
        if (!connections.containsKey(u)) {
            connections.put(u, new HashSet<>());
        }

        if (!connections.containsKey(v)) {
            connections.put(v, new HashSet<>());
        }

        connections.get(u).add(v);
        connections.get(v).add(u);
    }

    //-------------------------------------------- GETTER --------------------------------------------


    public HashMap<Integer, HashSet<Integer>> getConnections() {
        return connections;
    }

    public ArrayList<Edge> getEdges() {
        return edges;
    }

    public HashSet<Integer> getNodes() {
        return nodes;
    }

    public int[] getDegrees() {
        return degrees;
    }

    public int getMaxNodeValue() {
        return maxNodeValue;
    }

    public ConcurrentHashMap<Integer, Integer> getDegreeMap() {
        return degreeMap;
    }
}
