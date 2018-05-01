package it.ric.uny.densestsubgraph.model;

import com.google.common.base.Objects;
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

        Edge edge = (Edge) o;
        return ((u == edge.getU()) && (v == edge.getV()))
            || ((v == edge.getU()) && (u == edge.getV()));
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + (u*v);
        return result;
    }
}