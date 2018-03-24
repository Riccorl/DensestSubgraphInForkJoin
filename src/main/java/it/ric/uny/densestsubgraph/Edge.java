package it.ric.uny.densestsubgraph;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }

        Edge edge = (Edge) o;

        if (u != edge.u) {
            return false;
        }
        return v == edge.v;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + u;
        result = 31 * result + v;
        return result;
    }



}
