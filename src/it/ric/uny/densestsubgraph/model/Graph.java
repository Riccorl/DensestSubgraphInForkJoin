package it.ric.uny.densestsubgraph.model;

public interface Graph {

    /**
     * Get degree of a node
     *
     * @param n node in input
     * @return degree of n
     */
    int degree(int n);
}
