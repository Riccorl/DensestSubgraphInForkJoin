package it.ric.uny.densestsubgraph;

import static java.nio.file.Files.newBufferedReader;

import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.Data;

@Data
public class UndirectedGraphSeq implements Graph {

    private static final String COMMENT_CHAR = "#";

    // Numero di archi
    private float nEdges;
    // Numero di nodi
    private float nNodes;
    // ArrayList di archi
    private ArrayList<Edge> edges;
    // Insieme dei nodi
    private Set<Integer> nodes;
    // Densita del sottografo più denso
    private float density;
    // Nodi sottografo più denso
    private Set<Integer> sTilde;

    // Grado associato ad ogni nodo (u, deg(u)).
    private HashMap<Integer, Set<Integer>> degreesMap;

    public UndirectedGraphSeq(String filename, float nEdges, float nNodes) {

        this.edges = new ArrayList<>();
        this.nodes = new HashSet<>();
        this.degreesMap = new HashMap<>();
        this.sTilde = new HashSet<>();

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

    public float densestSubgraphRic(float e) {
        float densityS = calcDensity(nEdges, nNodes);
        Set<Integer> s = new HashSet<>(nodes);
        HashMap<Integer, Set<Integer>> degreeS = this.degreeSeq(edges);
        return densestSubgraphRic(edges, s, degreeS, densityS, densityS, e);
    }

    private float densestSubgraphRic(ArrayList<Edge> edges, Set<Integer> s,
        HashMap<Integer, Set<Integer>> degreeS,
        float densityS, float dSTilde, float e) {

        if (degreeS.isEmpty()) {
            return dSTilde;
        }

        degreeS = this.degreeSeq(edges);
        float threshold = 2 * (1 + e) * densityS;
        filter(edges, degreeS, threshold);
        densityS = calcDensity(edges.size() / 2, degreeS.keySet().size());

        if (densityS > dSTilde) {
            dSTilde = densityS;
        }

        return densestSubgraphRic(edges, s, degreeS, densityS, dSTilde, e);
    }

    public float densestSubgraph(float e) {
        Set<Integer> sTilde = new HashSet<>(nodes);
        Map<Integer, Set<Integer>> degreeS = this.degreeSeq(edges);

        return densestSubgraph(edges, degreeS, sTilde, e);
    }

    private float densestSubgraph(ArrayList<Edge> edges, Map<Integer, Set<Integer>> degreeS,
        Set<Integer> sTilde, float e) {

        float densityS = calcDensity(nEdges, nNodes);
        float dSTilde = densityS;

        // Itera sugli archi alla ricerca di nodi con grado inferiore a 2*(1 + e) * d(S)
        while (!degreeS.isEmpty()) {

            float threshold = 2 * (1 + e) * densityS;
            filter(edges, degreeS, threshold);
            densityS = calcDensity(edges.size() / 2, degreeS.keySet().size());

            if (densityS > dSTilde) {
                sTilde = new HashSet<>(degreeS.keySet());
                dSTilde = densityS;
            }

            degreeS = this.degreeSeq(edges);
        }

        this.density = dSTilde;
        this.sTilde = sTilde;
        return density;
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

    /**
     * For undirected simple graphs G = (V,E), and S a subset of G,
     * the graph density is defined as d = |E(S)| / |S|
     *
     * @return d
     */
    public float calcDensity(float nEdges, float nNodes) {
        return nEdges / nNodes;
    }

    /**
     * @param n node in input
     * @return degree of node n
     */
    @Override
    public int degree(int n) {
        return degreesMap.get(n).size();
    }

    public HashMap<Integer, Set<Integer>> degreeSeq(ArrayList<Edge> edges) {
        HashMap<Integer, Set<Integer>> degreesMap = new HashMap<>();
        for (Edge e : edges) {
            degreesMap.putIfAbsent(e.getU(), new HashSet<>());
            degreesMap.putIfAbsent(e.getV(), new HashSet<>());

            degreesMap.get(e.getU()).add(e.getV());
            degreesMap.get(e.getV()).add(e.getU());
            //degreesMap.put(k, degreesMap.get(k) + 1);
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

                    nodes.add(u);
                    nodes.add(v);
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
}
