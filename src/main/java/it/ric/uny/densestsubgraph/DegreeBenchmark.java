package it.ric.uny.densestsubgraph;

import it.ric.uny.densestsubgraph.model.Edge;
import it.ric.uny.densestsubgraph.utils.Utility;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@State(Scope.Benchmark)
@Fork(1)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@BenchmarkMode(Mode.SampleTime)
public class DegreeBenchmark {

    private static final Logger logger = LoggerFactory.getLogger(DegreeBenchmark.class);
    private List<Edge> edges;

    private static float epsilon;
    private static int cutoffDegree;
    private static int cutoffRemove;

    public static void main(String[] args) throws RunnerException {

        try {
            epsilon = Float.parseFloat(args[1]);
            cutoffDegree = Integer.parseInt(args[2]);
            cutoffRemove = Integer.parseInt(args[3]);
        } catch (ArrayIndexOutOfBoundsException e) {
            epsilon = (float) 1.1;
            cutoffDegree = 5000;
            cutoffRemove = 5000;
        }

        //String filename = "data/dummy_graph.txt";
        //String filename = "data/dummy_graph2.txt";            float nEdge = 11;         float nNode = 8;
        //String filename = "data/ca-GrQc.txt";                 float nEdges = 14496;     float nNodes = 5242;
        //String filename = "data/facebook_combined.txt";       float nEdges = 88234;     float nNodes = 4039;
        //String filename = "data/ca-CondMat.txt";              float nEdges = 93497;     float nNodes = 23133;
        //String filename = "data/ca-AstroPh.txt";                float nEdges = 198110;          float nNodes = 18772;
        //String filename = "data/roadNet-CA.txt";              float nEdge = 2766607;    float nNodes = 1965206;
        //String filename = "data/as-skitter.txt";              float nEdges = 11095298;  float nNodes = 1696415;
        String filename = "data/cit-Patents.txt";             float nEdges = 16518948;  float nNodes = 3774768;
        //String filename = "data/wiki-topcats.txt";            float nEdges = 28511807;  float nNodes = 1791489;
        //String filename = "data/soc-LiveJournal1.txt";        float nEdge = 68993773;   float nNodes = 4847571;


        // --------------------------------- Reading ----------------------------------------------
        System.out.println();
        System.out.println("Filename: " + filename);
        //System.out.println("Numero di nodi: " + (int) nNodes);
        //System.out.println("Numero di archi: " + (int) nEdges);
        System.out.println("Fattore di approssimazione epsilon: " + epsilon);
        System.out.println();
        System.out.println("Cutoff Grado: " + cutoffDegree);
        System.out.println("Cutoff Rimozione: " + cutoffRemove);
        System.out.println();
        System.out.println("Reading...");
        List<Edge> edges = Utility.fileToEdge(filename);
        System.out.println("Read ok");
        System.out.println();

        // --------------------------------- Sequential ---------------------------------------------
        UndirectedGraphSeq seq = new UndirectedGraphSeq(edges);
        long startTime = System.nanoTime();
        float dS = seq.densestSubgraph(epsilon);
        long endTime = System.nanoTime();
        long timeS = TimeUnit.NANOSECONDS.toMillis(endTime - startTime);
        System.out.println("Sequential Degree Time: " + timeS + " ms");
        System.out.println("Sequential Density: " + dS);

        // --------------------------------- Parallel ---------------------------------------------
        UndirectedGraph parallel = new UndirectedGraph(edges);
        long startTimeP = System.nanoTime();
        float dP = parallel.densestSubgraph(epsilon);
        long endTimeP = System.nanoTime();
        long time = TimeUnit.NANOSECONDS.toMillis(endTimeP - startTimeP);
        System.out.println("Parallel Degree Time: " + time + " ms");
        System.out.println("Parallel Density: " + dP);

        System.out.println("Speedup: " + (timeS / time));

//        Options opts = new OptionsBuilder()
//            .include(DegreeBenchmark.class.getSimpleName())
//            .warmupIterations(5)
//            .measurementIterations(3)
//            .forks(1)
//            .mode(Mode.AverageTime)
//            .timeUnit(TimeUnit.MILLISECONDS)
//            .build();
//
//        new Runner(opts).run();
    }

    @Benchmark
    public float parallelDensity() {
        UndirectedGraph parallel = new UndirectedGraph(edges);
        float d = parallel
            .densestSubgraph(epsilon);

        // logger.debug("Parallel density: {}", d);
        return d;
    }

    @Benchmark
    public float sequentialDensity() {
        UndirectedGraphSeq seq = new UndirectedGraphSeq(edges);
        float d = seq
            .densestSubgraph(epsilon);

        // logger.debug("Sequential density: {}", d);
        return d;
    }

//    @Benchmark
//    public Map<Integer, Set<Integer>> parallelDegree() {
//        UndirectedGraph parallel = new UndirectedGraph(edges);
//        parallel.setCutoffDegree(1000);
//        return parallel
//            .degreeConc(parallel.getEdges(), (int) parallel.getNEdges() / 2);
//    }
//
//    @Benchmark
//    public Map<Integer, Set<Integer>> sequentialDegree() {
//        UndirectedGraphSeq seq = new UndirectedGraphSeq(edges);
//        return seq.degreeSeq(seq.getEdges());
//    }


    @Setup
    public void setup() {
        //    Local
        //String filename = "data/dummy_graph.txt";
        //String filename = "data/dummy_graph2.txt";            float nEdge = 11;         float nNode = 8;
        //String filename = "data/ca-GrQc.txt";                 float nEdges = 14496;     float nNodes = 5242;
        //String filename = "data/facebook_combined.txt";       float nEdges = 88234;     float nNodes = 4039;
        //String filename = "data/ca-CondMat.txt";              float nEdges = 93497;     float nNodes = 23133;
        //String filename = "data/ca-AstroPh.txt";                float nEdges = 198110;          float nNodes = 18772;
        //String filename = "data/roadNet-CA.txt";              float nEdge = 2766607;    float nNodes = 1965206;
        //String filename = "data/as-skitter.txt";              float nEdges = 11095298;  float nNodes = 1696415;
        String filename = "data/cit-Patents.txt";             float nEdges = 16518948;  float nNodes = 3774768;
        //String filename = "data/wiki-topcats.txt";            float nEdges = 28511807;  float nNodes = 1791489;
        //String filename = "data/soc-LiveJournal1.txt";        float nEdge = 68993773;   float nNodes = 4847571;

        logger.debug("Filename: " + filename);
        edges = Utility.fileToEdge(filename);
        epsilon = (float) 1.1;
    }
}
