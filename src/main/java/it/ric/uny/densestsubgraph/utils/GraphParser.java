package it.ric.uny.densestsubgraph.utils;

import static java.nio.file.Files.newBufferedReader;

import com.google.common.graph.GraphBuilder;
import com.google.common.graph.MutableGraph;
import it.ric.uny.densestsubgraph.Edge;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class GraphParser {

    private static final String COMMENT_CHAR = "#";

    /**
     * Reads from file and generates data structure for the graph
     *
     * @param filename file to read
     */
    public static ArrayList<Edge> fileToEdge(String filename) {

        Pattern pattern = Pattern.compile("^([\\d]*)\\s([\\d]*)");

        ArrayList<Edge> edges = new ArrayList<>();
        try (BufferedReader br = newBufferedReader(Paths.get(filename), StandardCharsets.UTF_8)) {
            for (String line = null; (line = br.readLine()) != null; ) {

                Matcher matcher = pattern.matcher(line);
                if (matcher.matches()) {
                    int u = Integer.parseInt(matcher.group(1));
                    int v = Integer.parseInt(matcher.group(2));

                    edges.add(new Edge(u, v));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return edges;
    }

    public static MutableGraph<Integer> parseGuava(String filename) throws IOException {
        MutableGraph<Integer> graph = GraphBuilder.undirected().allowsSelfLoops(true).build();

        try (Stream<String> stream = Files.lines(Paths.get(filename))) {
            stream.forEach(x -> {
                if (x.startsWith(COMMENT_CHAR)) {
                    return;
                }
                String[] row = x.split("[\t ]");
                graph.putEdge(Integer.parseInt(row[0]), Integer.parseInt(row[1]));
            });
            return graph;
        }
    }
}
