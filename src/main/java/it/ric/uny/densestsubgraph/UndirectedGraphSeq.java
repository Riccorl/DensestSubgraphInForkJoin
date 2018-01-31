package it.ric.uny.densestsubgraph;

import static java.nio.file.Files.newBufferedReader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;
import java.util.StringTokenizer;

public class UndirectedGraphSeq implements Graph {

    private static final String COMMENT_CHAR = "#";

    // Grafo.
    private HashMap<Integer, HashSet<Integer>> graph;

    // Grafo con archi duplicati per ogni nodo, utile per calcolo del grado.
    private HashMap<Integer, HashSet<Integer>> connections;

    // Grado associato ad ogni nodo (u, deg(u)).
    private HashMap<Integer, Integer> degreeMap;

    // Densità del grafo
    private int graphDensity;

    public UndirectedGraphSeq(String filename) {

        this.graph = new HashMap<>();
        this.connections = new HashMap<>();

        this.degreeMap = new HashMap<>();

        this.fileToGraph(filename);

        //graphDensity = calcDensity(graph);
    }

    private int calcDensity(HashMap<Integer, HashSet<Integer>> graph) {

        int nEdge = graph.keySet().stream().mapToInt(x -> x).map(x -> graph.get(x).size()).sum();
        int nNode = graph.keySet().size() * (graph.keySet().size() - 1);

        return nEdge / nNode;
    }

    public HashMap<Integer, HashSet<Integer>> inducedEdge(HashSet<Integer> nodes) {
        HashMap<Integer, HashSet<Integer>> square = new HashMap<>();

        for (Integer n : nodes) {
            HashSet<Integer> intersect = new HashSet<>(connections.get(n));
            intersect.retainAll(nodes);
            square.put(n, intersect);
        }

        return square;
    }

    @Override
    public int degree(int n) {
        return degreeMap.get(n);
    }

    /**
     * Wrapper for prepare method
     */
    public void degreePrepare() {
        this.degreeMap = prepare(degreeMap);
    }

    /**
     * Precalculation of all nodes' degree.
     *
     * @param degreeMap Empty map of nodes and relative degree
     * @return degreeMap with degrees
     */
    private HashMap<Integer, Integer> prepare(HashMap<Integer, Integer> degreeMap) {

        for (Integer x : degreeMap.keySet()) {
            int value = connections.get(x).size();
            degreeMap.put(x, value);
        }

        return degreeMap;
    }

    /**
     * Reads from file and generates data structure for the graph
     *
     * @param filename file to read
     */
    private void fileToGraph(String filename) {

        try (BufferedReader br = newBufferedReader(Paths.get(filename),
            StandardCharsets.UTF_8)) {
            for (String line = null; (line = br.readLine()) != null;) {

                if (line.startsWith(COMMENT_CHAR)) {
                    continue;
                }

                String[] row = line.split("[\t ]");

                int n1 = Integer.parseInt(row[0]);
                int n2 = Integer.parseInt(row[1]);

                addEdge(n1, n2);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Lettura Seq ok");
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

        degreeMap.put(u, 0);
        degreeMap.put(v, 0);

    }

    //-------------------------------------------- GETTER --------------------------------------------

    public HashMap<Integer, Integer> getDegreeMap() {
        return degreeMap;
    }

    public HashMap<Integer, HashSet<Integer>> getGraph() {
        return graph;
    }

    public HashMap<Integer, HashSet<Integer>> getConnections() {
        return connections;
    }

    public int getGraphDensity() {
        return graphDensity;
    }

}
