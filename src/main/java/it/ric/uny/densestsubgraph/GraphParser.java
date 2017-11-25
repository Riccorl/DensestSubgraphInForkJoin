package it.ric.uny.densestsubgraph;

import com.google.common.graph.ElementOrder;
//import com.google.common.graph.Graph;
import com.google.common.graph.GraphBuilder;
import com.google.common.graph.MutableGraph;
import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.Pseudograph;
import org.jgrapht.graph.SimpleGraph;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.RecursiveTask;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GraphParser {

    public static TreeMap<Integer, Integer> toMap(String filename) {

        List<String> rows = parse(filename);
        TreeMap<Integer, Integer> map = new TreeMap<>();

        for (String x : rows) {
            String[] row = x.split("[\t ]");
            int n1 = Integer.parseInt(row[0]);
            int n2 = Integer.parseInt(row[1]);
            map.put(n1, n2);
        }

        return map;
    }

    /*@Override
    protected Graph<Integer, DefaultEdge> compute() {

        Graph<Integer, DefaultEdge> g = new Pseudograph<>(DefaultEdge.class);

        if (high - low < cutoff) {
            for (Integer x : rows.keySet()){
                int n1 = x;
                int n2 = rows.get(x);
                g.addVertex(n1);
                g.addVertex(n2);
                g.addEdge(n1,n2);
            }

            return g;
        }

        SortedMap<Integer, Integer> lowList = rows.subMap(low,(high+low)/2);
        SortedMap<Integer, Integer> highList = rows.subMap((high+low)/2, high);
        //List<String> highList = rows.stream().skip((high+low)/2).collect(Collectors.toList());

        GraphParser left = new GraphParser(lowList, lowList.keySet().size(), this.cutoff);
        GraphParser right = new GraphParser(lowList, highList.keySet().size(), this.cutoff);

        left.fork();
        g = right.compute();
        Graph<Integer, DefaultEdge> gLeft = left.join();

        Graphs.addGraph(g, gLeft);

        return g;
    }*/

    private static List<String> parse(String filename) {

        List<String> rows = new ArrayList<>();

        try (Stream<String> stream = Files.lines(Paths.get(filename))) {
            stream.forEach(rows::add);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return rows;
    }

    public static MutableGraph<Integer> parseGuava(SortedMap<Integer, Integer> rows) {
        MutableGraph<Integer> graph = GraphBuilder.undirected()
                    .allowsSelfLoops(true)
                    .build();
        /*try (Stream<String> stream = Files.lines(Paths.get(filename))) {
            stream.forEach(x -> {
                String[] row = x.split("[\t ]");
                graph.putEdge(Integer.parseInt(row[0]), Integer.parseInt(row[1]));
            });*/

        for (Integer x : rows.keySet()){
            int n1 = x;
            int n2 = rows.get(x);
            graph.putEdge(n1, n2);
        }

        return graph;

    }
}
