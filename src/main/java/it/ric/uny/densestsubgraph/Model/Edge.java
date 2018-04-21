package it.ric.uny.densestsubgraph.model;


import lombok.Data;

@Data
public class Edge {

    private int u;
    private int v;

    public Edge(int u, int v) {
        this.u = u;
        this.v = v;
    }

    @Override
    public String toString() {
        return
            "(" + u +
            "," + v +
            ")";
    }

}
