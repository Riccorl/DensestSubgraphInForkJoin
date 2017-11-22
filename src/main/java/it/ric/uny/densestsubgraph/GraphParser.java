package it.ric.uny.densestsubgraph;

import com.google.common.graph.ElementOrder;
//import com.google.common.graph.Graph;
import com.google.common.graph.GraphBuilder;
import com.google.common.graph.MutableGraph;
import org.jgrapht.Graph;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class GraphParser {

    public static Graph<Integer, DefaultEdge> parse(String filename) throws IOException {

        Graph<Integer, DefaultEdge> g = new SimpleGraph<>(DefaultEdge.class);

        try (Stream<String> stream = Files.lines(Paths.get(filename))) {
            stream.forEach(x -> {
                String[] row = x.split(" ");
                int n1 = Integer.parseInt(row[0]);
                int n2 = Integer.parseInt(row[1]);
                g.addVertex(n1);
                g.addVertex(n2);
                g.addEdge(n1,n2);
            });
        }

        return g;
    }

    public static MutableGraph<Integer> parseGuava(String filename) throws IOException {
        MutableGraph<Integer> graph = GraphBuilder.undirected().build();
        try (Stream<String> stream = Files.lines(Paths.get(filename))) {
            stream.forEach(x -> {
                String[] row = x.split(" ");
                graph.putEdge(Integer.parseInt(row[0]), Integer.parseInt(row[1]));
            });
            return graph;
        }
    }
}
