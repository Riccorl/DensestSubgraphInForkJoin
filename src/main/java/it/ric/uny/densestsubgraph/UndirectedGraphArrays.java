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
import java.util.concurrent.ForkJoinPool;

public class UndirectedGraphArrays {

    private static final String COMMENT_CHAR = "#";
    private static ForkJoinPool fjPool = new ForkJoinPool();


    // Grafo con archi duplicati per ogni nodo, utile per calcolo del grado.
    private HashMap<Integer, HashSet<Integer>> connections;

    // ArrayList di archi
    private ArrayList<Edge> edges;
    private HashSet<Integer> nodes;

    // Grado associato ad ogni nodo (u, deg(u)).
    private int[] degrees;
    private int maxNodeValue;

    public UndirectedGraphArrays(String filename) {

        this.connections = new HashMap<>();
        this.nodes = new HashSet<>();
        this.edges = new ArrayList<>();

        this.fileToGraph(filename);

        this.maxNodeValue = Collections.max(nodes);

        this.degrees = new int[maxNodeValue];
    }

    public int degree(int n) {
        return degrees[n];
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

        return fjPool.invoke(new ParallelArrays(edges, maxNodeValue, 28511807));
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

                nodes.add(u);
                nodes.add(v);
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

    public int[] getDegrees() {
        return degrees;
    }

    public int getMaxNodeValue() {
        return maxNodeValue;
    }
}
