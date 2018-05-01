package it.ric.uny.densestsubgraph.parallel;

import it.ric.uny.densestsubgraph.model.Edge;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.RecursiveTask;
import java.util.stream.Collectors;
import java.util.stream.Stream;

// Modifica con ConcurrentHashMap
// Source Code: https://goo.gl/tZqrkB
// Link utili:
// https://howtodoinjava.com/core-java/multi-threading/best-practices-for-using-concurrenthashmap/

public class ParallelDegreeTwo extends RecursiveTask<Map<Integer, Integer>> {

    private static final int CUTOFF = 5000;

    // ArrayList contenente gli archi
    private List<Edge> edges;
    //private Edge[] edges;
    // Mappa (u, deg(u))

    private int start;
    private int end;

    public ParallelDegreeTwo(List<Edge> edges) {
        this.edges = edges;
        this.end = edges.size();
    }

    private ParallelDegreeTwo(List<Edge> edges, int start,
        int end) {
        this.edges = edges;
        this.start = start;
        this.end = end;
    }

    @Override
    protected Map<Integer, Integer> compute() {

        // Sequential
        if (end - start < CUTOFF) {
            Map<Integer, Integer> degreeMap = new HashMap<>();
            for (int i = start; i < end; i++) {
                // Nodo da aggiornare
                int u = edges.get(i).getU();
                int v = edges.get(i).getV();

                // Se i nodi non sono presenti, aggiungili
                // con grado 0
                degreeMap.putIfAbsent(u, 0);
                degreeMap.putIfAbsent(v, 0);

                int degU = degreeMap.get(u);
                degreeMap.put(u, degU + 1);

                int degV = degreeMap.get(v);
                degreeMap.put(v, degV + 1);
            }
            return degreeMap;
        }

        // Parallel
        int mid = (start + end) / 2;

        ParallelDegreeTwo left = new ParallelDegreeTwo(edges, start, mid);
        ParallelDegreeTwo right = new ParallelDegreeTwo(edges, mid, end);

        left.fork();
        Map<Integer, Integer> rightMap = right.compute();
        Map<Integer, Integer> leftMap = left.join();
        rightMap.forEach((k, v) -> leftMap.merge(k, v, Integer::sum));
        return leftMap;


//        return Stream.of(leftMap, rightMap)
//            .map(Map::entrySet)          // converts each map into an entry set
//            .flatMap(Collection::stream) // converts each set into an entry stream, then
//            // "concatenates" it in place of the original set
//            .collect(
//                Collectors.toMap(           // collects into a map
//                    Map.Entry::getKey,      // where each entry is based
//                    Map.Entry::getValue,    // on the entries in the stream
//                    Integer::sum            // such that if a value already exist for
//                                            // a given key, it sums the two values
//                )
//            );
    }
}