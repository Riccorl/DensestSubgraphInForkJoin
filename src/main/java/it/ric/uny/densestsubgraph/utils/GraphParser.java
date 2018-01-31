package it.ric.uny.densestsubgraph.utils;

import com.google.common.graph.GraphBuilder;
import com.google.common.graph.MutableGraph;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Stream;

public class GraphParser {

    private static final String COMMENT_CHAR = "#";


    public static HashMap<Integer, HashSet<Integer>> toGraph(String filename) {

        HashMap<Integer, HashSet<Integer>> graph = new HashMap<>();
        List<String> rows = parse(filename);

        rows.forEach(x -> {
            if (x.startsWith(COMMENT_CHAR)) {
                return;
            }

            String[] row = x.split("[\t ]");
            int n1 = Integer.parseInt(row[0]);
            int n2 = Integer.parseInt(row[1]);
            if (!graph.containsKey(n1)) {
                HashSet<Integer> set = new HashSet<>();
                set.add(n2);
                graph.put(n1, set);
            } else {
                HashSet<Integer> set = graph.get(n1);
                set.add(n2);
                graph.put(n1, set);
            }
        });

        return graph;
    }

    private static List<String> parse(String filename) {

        List<String> rows = new ArrayList<>();

        try (Stream<String> stream = Files.lines(Paths.get(filename))) {
            stream.forEach(rows::add);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return rows;
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
