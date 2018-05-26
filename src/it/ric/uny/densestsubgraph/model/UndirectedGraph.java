package it.ric.uny.densestsubgraph.model;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data
public class UndirectedGraph {

    // Number of edges
    private double nEdges;
    // Number of nodes
    private double nNodes;
    // List of edges
    private List<Edge> edges;

    public UndirectedGraph(List<Edge> edges) {
        this.edges = new ArrayList<>(edges);
        this.nEdges = edges.size();
    }
}
