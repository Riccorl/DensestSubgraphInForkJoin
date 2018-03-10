package it.ric.uny.densestsubgraph;

import static java.nio.file.Files.newBufferedReader;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UndirectedGraphSeq implements Graph {

    private static final String COMMENT_CHAR = "#";

    // Numero di archi
    private double nEdges;
    // Numero di nodi
    private double nNodes;
    // ArrayList di archi
    private ArrayList<Edge> edges;

    // Grado associato ad ogni nodo (u, deg(u)).
    private HashMap<Integer, Integer> degreesMap;

    public UndirectedGraphSeq(String filename, double nEdges, double nNodes) {

        this.edges = new ArrayList<>();
        this.degreesMap = new HashMap<>();

        this.nEdges = nEdges;
        this.nNodes = nNodes;

        this.fileToGraph(filename);
    }

    public UndirectedGraphSeq(int nEdges, int nNodes) {
        this.edges = new ArrayList<>();
        this.degreesMap = new HashMap<>();

        this.nEdges = nEdges;
        this.nNodes = nNodes;
    }

    public Set<Integer> densestSubgraph(int e) {
        Set<Integer> s = degreesMap.keySet();
        Set<Integer> sTilde = degreesMap.keySet();
        double d = calcDensity(nEdges, nNodes);
        return densestSubgraph(edges, s, sTilde, d, e);
    }

    private Set<Integer> densestSubgraph(ArrayList<Edge> edges, Set<Integer> s,
                                         Set<Integer> sTilde, double density, int e) {

        if (s.size() == 0) {
            return sTilde;
        }

        ArrayList<Edge> edgesTilde = new ArrayList<>(edges);
        this.degreesMap = this.degreeSeq(edges);
        HashMap<Integer, Integer> degreesTilde = new HashMap<>(degreesMap);

        // Itera sugli archi alla ricerca di nodi con grado inferiore a
        // 2*(1 + e) * d(S)
        for (Edge edge : edges) {
            if (this.degree(edge.getU()) < 2*(1+e)*density) {
                //edge.setU(-1);
                degreesTilde.remove(edge.getU());
                s.remove(edge.getU());
                edgesTilde.remove(edge);
            }

            if (this.degree(edge.getV()) < 2*(1+e)*density) {
                //edge.setV(-1);
                degreesTilde.remove(edge.getV());
                s.remove(edge.getV());
                edgesTilde.remove(edge);
            }
        }

        edges = edgesTilde;
        degreesMap = degreesTilde;

        double densityTilde = calcDensity(edgesTilde.size(), s.size());

        if (density > densityTilde) {

            sTilde = s;
        }

        return densestSubgraph(edges, s, sTilde, density, e);
    }

    /**
     * For undirected simple graphs G = (V,E), the graph density is defined as
     * d = 2|E| / (|V| * (|V| - 1))
     *
     * @return      d
     */
    public double calcDensity(double nEdges, double nNodes) {
        return nEdges / nNodes;
    }

    @Override
    public int degree(int n) {
        return degreesMap.get(n);
    }

    public HashMap<Integer, Integer> degreeSeq(ArrayList<Edge> edges) {
        //HashMap<Integer, Integer> degreesMap = new HashMap<>();
        for (Edge e : edges) {
            int k = e.getU();
            degreesMap.put(k, degreesMap.get(k) + 1);
        }

        return degreesMap;
    }

    /**
     * Reads from file and generates data structure for the graph
     *
     * @param filename file to read
     */
    private void fileToGraph(String filename) {

        Pattern pattern = Pattern.compile("^([\\d]*)\\s([\\d]*)");

        try (BufferedReader br = newBufferedReader(Paths.get(filename),
            StandardCharsets.UTF_8)) {
            for (String line = null; (line = br.readLine()) != null; ) {

                Matcher matcher = pattern.matcher(line);
                if (matcher.matches()) {
                    int u = Integer.parseInt(matcher.group(1));
                    int v = Integer.parseInt(matcher.group(2));

                    degreesMap.putIfAbsent(u, 0);
                    degreesMap.putIfAbsent(v, 0);

                    edges.add(new Edge(u, v));
                }
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

        degreesMap.put(u, 0);
        degreesMap.put(v, 0);

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

    public HashMap<Integer, Integer> getDegreesMap() {
        return degreesMap;
    }

    public ArrayList<Edge> getEdges()
    {
        return edges;
    }
}
