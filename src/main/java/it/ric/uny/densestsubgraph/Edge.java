package it.ric.uny.densestsubgraph;

import java.util.Objects;

public class Edge {

    private int u;
    private int v;

    public Edge(int u, int v) {
        this.u = u;
        this.v = v;
    }

    public int getU() {
        return u;
    }

    public int getV() {
        return v;
    }

    public void setU(int u)
    {
        this.u = u;
    }

    public void setV(int v)
    {
        this.v = v;
    }

    @Override
    public String toString() {
        return
            "(" + u +
            "," + v +
            ")";
    }

    @Override
    public boolean equals(Object o) {
        Edge edge = (Edge) o;
        return u == edge.getU() &&
            v == edge.getV();
    }

    @Override
    public int hashCode() {
        return Objects.hash(u, v);
    }
}
