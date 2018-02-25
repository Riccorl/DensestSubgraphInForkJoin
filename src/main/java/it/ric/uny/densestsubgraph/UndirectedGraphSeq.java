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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;
import java.util.StringTokenizer;

public class UndirectedGraphSeq implements Graph {

    private static final String COMMENT_CHAR = "#";

    // Numero di archi
    private int nEdges;
    // Numero di nodi
    private int nNodes;
    // ArrayList di archi
    private ArrayList<Edge> edges;

    // Grado associato ad ogni nodo (u, deg(u)).
    private HashMap<Integer, Integer> degreeMap;

    public UndirectedGraphSeq(String filename, int nEdges, int nNodes) {

        this.edges = new ArrayList<>();
        this.degreeMap = new HashMap<>();

        this.fileToGraph(filename);

        this.nEdges = nEdges;
        this.nNodes = nNodes;
    }

    private int calcDensity(HashMap<Integer, HashSet<Integer>> graph) {

        int nEdge = graph.keySet().stream().mapToInt(x -> x).map(x -> graph.get(x).size()).sum();
        int nNode = graph.keySet().size() * (graph.keySet().size() - 1);

        return nEdge / nNode;
    }

    @Override
    public int degree(int n) {
        return degreeMap.get(n);
    }

    public void degreeSeq() {
        for (Edge e : edges) {
            int k = e.getU();
            degreeMap.put(k, degreeMap.get(k) + 1);
        }
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

/*    private void addEdge(int u, int v) {
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

    }*/

    /*    public HashMap<Integer, HashSet<Integer>> inducedEdge(HashSet<Integer> nodes) {
        HashMap<Integer, HashSet<Integer>> square = new HashMap<>();

        for (Integer n : nodes) {
            HashSet<Integer> intersect = new HashSet<>(connections.get(n));
            intersect.retainAll(nodes);
            square.put(n, intersect);
        }

        return square;
    }*/

    //-------------------------------------------- GETTER --------------------------------------------

    public HashMap<Integer, Integer> getDegreeMap() {
        return degreeMap;
    }

}
