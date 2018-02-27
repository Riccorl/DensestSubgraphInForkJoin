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

    @Override
    public String toString() {
        return
            "(" + u +
            "," + v +
            ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Edge edge = (Edge) o;
        return u == edge.u &&
            v == edge.v;
    }

    @Override
    public int hashCode() {
        return Objects.hash(u, v);
    }
}
