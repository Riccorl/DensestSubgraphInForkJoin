package it.ric.uny.densestsubgraph;

import com.google.common.graph.MutableGraph;

import java.util.HashMap;
import java.util.HashSet;

public class UndirectedGraph {

    private MutableGraph<Integer> graph;
    private HashMap<Integer, Integer> degreeMap;

    public UndirectedGraph(MutableGraph<Integer> graph) {
        this.graph = graph;
        //this.degreeMap = graph.nodes().stream().forEach(x -> );
    }
}
