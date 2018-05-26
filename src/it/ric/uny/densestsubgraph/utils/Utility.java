package it.ric.uny.densestsubgraph.utils;

import static java.nio.file.Files.newBufferedReader;

import com.google.common.graph.GraphBuilder;
import com.google.common.graph.MutableGraph;
import it.ric.uny.densestsubgraph.model.Edge;
import java.io.BufferedReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class Utility {

    private static final String COMMENT_CHAR = "#";

    public static void filter(List<Edge> list, Map<Integer, Set<Integer>> degreeS,
        double threshold) {
        int inputSize = list.size();
        int outputSize = 0;

        for (int i = 0; i < inputSize; ++i) {
            Edge e = list.get(i);
            int u = e.getU();
            int v = e.getV();

            if (degreeS.get(u).size() > threshold && degreeS.get(v).size() > threshold) {
                list.set(outputSize++, e);
            }
        }
        list.subList(outputSize, inputSize).clear();
    }

    public static double round(double x, int numberofDecimals) {
        if (x > 0) {
            return new BigDecimal(String.valueOf(x))
                .setScale(numberofDecimals, BigDecimal.ROUND_FLOOR).doubleValue();
        } else {
            return new BigDecimal(String.valueOf(x))
                .setScale(numberofDecimals, BigDecimal.ROUND_CEILING).doubleValue();
        }
    }

    /**
     * Reads from file and generates data structure for the graph
     *
     * @param filename file to read
     */
    public static List<Edge> fileToGraph(String filename) {
        List<Edge> edges = new ArrayList<>();
        try (BufferedReader br = newBufferedReader(Paths.get(filename),
            StandardCharsets.UTF_8)) {
            for (String line = null; (line = br.readLine()) != null; ) {

                if (line.startsWith(COMMENT_CHAR)) {
                    continue;
                }

                String[] row = line.split("\\s+");

                int u = Integer.parseInt(row[0]);
                int v = Integer.parseInt(row[1]);

                edges.add(new Edge(u, v));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Lettura ok");
        return edges;
    }

    /**
     * Reads from file and generates data structure for the graph
     *
     * @param filename file to read
     */
    public static List<Edge> fileToEdge(String filename) {

        Pattern pattern = Pattern.compile("^([\\d]*)\\s([\\d]*)");

        Set<Edge> edgeSet = new HashSet<>();
        try (BufferedReader br = newBufferedReader(Paths.get(filename), StandardCharsets.UTF_8)) {
            for (String line = null; (line = br.readLine()) != null; ) {

                Matcher matcher = pattern.matcher(line);
                if (matcher.matches()) {
                    int u = Integer.parseInt(matcher.group(1));
                    int v = Integer.parseInt(matcher.group(2));

                    Edge e = new Edge(u, v);
                    edgeSet.add(e);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ArrayList<>(edgeSet);
    }

    public static MutableGraph<Integer> parseGuava(String filename) throws IOException {
        MutableGraph<Integer> graph = GraphBuilder.undirected().allowsSelfLoops(true).build();

        try (Stream<String> stream = Files.lines(Paths.get(filename))) {
            stream.forEach(x -> {
                if (x.startsWith(COMMENT_CHAR)) {
                    return;
                }
                String[] row = x.split("\\s+");
                graph.putEdge(Integer.parseInt(row[0]), Integer.parseInt(row[1]));
            });
            return graph;
        }
    }
}
