package it.ric.uny.densestsubgraph;

import static java.nio.file.Files.newBufferedReader;
import static jdk.nashorn.internal.runtime.regexp.joni.Config.log;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
public class UndirectedGraphSeq implements Graph {

    private static final String COMMENT_CHAR = "#";

    // Numero di archi
    private float nEdges;
    // Numero di nodi
    private float nNodes;
    // ArrayList di archi
    private ArrayList<Edge> edges;

    // Grado associato ad ogni nodo (u, deg(u)).
    private HashMap<Integer, Set<Integer>> degreesMap;

    public UndirectedGraphSeq(String filename, float nEdges, float nNodes) {

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

    public Set<Integer> densestSubgraph(float e) {
        Set<Integer> s = degreesMap.keySet();
        Set<Integer> sTilde = degreesMap.keySet();
        return densestSubgraph(edges, s, sTilde, e);
    }

    private Set<Integer> densestSubgraph(ArrayList<Edge> edges, Set<Integer> s,
        Set<Integer> sTilde, float e) {

        float densityS = calcDensity(edges.size(), s.size());
        float dSTilde = densityS;

        // Itera sugli archi alla ricerca di nodi con grado inferiore a
        // 2*(1 + e) * d(S)
        while (!s.isEmpty()) {
//            log.print("s size: "+s.size()+ "\n");

            Map<Integer, Set<Integer>> degreeS = this.degreeSeq(edges);
            Set<Integer> aS = new HashSet<>();
            ArrayList<Edge> edgesRemoved = new ArrayList<>();

            float threshold = 2 * (1 + e) * densityS;
            for (Edge edge : edges) {
                int degU = degreeS.get(edge.getU()).size();
                int degV = degreeS.get(edge.getV()).size();

                if (degU <= threshold || degV <= threshold) {

                    if (degU <= threshold) aS.add(edge.getU());
                    if (degV <= threshold) aS.add(edge.getV());

                    edgesRemoved.add(edge);
                }
            }

//            filter(edges, aS, threshold);

            s.removeAll(aS);
            edges.removeAll(edgesRemoved);

            densityS = calcDensity(edges.size(), s.size());
            if (densityS > dSTilde) {
                sTilde = new HashSet<>(s);
                dSTilde = densityS;
            }
        }

        return sTilde;
    }

    public void filter(ArrayList<Edge> list, Set<Integer> set, double threshold) {
        int inputSize = list.size();
        int outputSize = 0;

        for (int i = 0; i < inputSize; ++i) {
            Edge e = list.get(i);
            if (this.degree(e.getU()) <= threshold
                || this.degree(e.getV()) <= threshold) {

                if (this.degree(e.getU()) <= threshold) {
                    set.add(e.getU());
                }

                if (this.degree(e.getV()) <= threshold) {
                    set.add(e.getV());
                }
            } else {
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
    private float calcDensity(float nEdges, float nNodes) {
        return nEdges / nNodes;
    }

    /**
     *
     * @param       n node in input
     * @return      degree of node n
     */
    @Override
    public int degree(int n) {
        return degreesMap.get(n).size();
    }

    public HashMap<Integer, Set<Integer>> degreeSeq(ArrayList<Edge> edges) {
        HashMap<Integer, Set<Integer>> degreesMap = new HashMap<>();
        for (Edge e : edges) {
            if (!degreesMap.containsKey(e.getU())) {
                degreesMap.put(e.getU(), new HashSet<>());
            }

            if (!degreesMap.containsKey(e.getV())) {
                degreesMap.put(e.getV(), new HashSet<>());
            }

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

                    degreesMap.putIfAbsent(u, new HashSet<>());
                    degreesMap.putIfAbsent(v, new HashSet<>());

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
