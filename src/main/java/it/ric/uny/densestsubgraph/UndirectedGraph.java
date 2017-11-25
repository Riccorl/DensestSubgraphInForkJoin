package it.ric.uny.densestsubgraph;


import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.Pseudograph;

import java.util.SortedMap;
import java.util.concurrent.RecursiveTask;

public class UndirectedGraph extends RecursiveTask<Graph<Integer, DefaultEdge>>{

    private SortedMap<Integer, Integer> rows;
    private int low;
    private int high;
    private int cutoff;

    public UndirectedGraph(SortedMap<Integer, Integer> rows) {
        this.rows = rows;
        this.high = this.rows.keySet().size();
        this.cutoff = this.high/16;
    }

    public UndirectedGraph(SortedMap<Integer, Integer> rows, int high, int cutoff) {
        this.rows = rows;
        this.high = high;
        this.cutoff = cutoff;
    }

    @Override
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

        UndirectedGraph left = new UndirectedGraph(lowList, lowList.keySet().size(), this.cutoff);
        UndirectedGraph right = new UndirectedGraph(lowList, highList.keySet().size(), this.cutoff);

        left.fork();
        g = right.compute();
        Graph<Integer, DefaultEdge> gLeft = left.join();

        Graphs.addGraph(g, gLeft);

        return g;
    }
}
