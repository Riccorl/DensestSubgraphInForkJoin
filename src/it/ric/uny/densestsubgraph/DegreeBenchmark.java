package it.ric.uny.densestsubgraph;

import it.ric.uny.densestsubgraph.model.Edge;
import it.ric.uny.densestsubgraph.model.UndirectedGraph;
import it.ric.uny.densestsubgraph.utils.Utility;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;

@State(Scope.Benchmark)
@Fork(1)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@BenchmarkMode(Mode.All)
public class DegreeBenchmark {

    private List<Edge> edges;
    @Param({"data/ca-AstroPh.txt"})
    private String filename;
    private double epsilon;

    public void doBenchmark(String filename, Double epsilon) throws RunnerException {
        this.filename = filename;
        this.epsilon = epsilon;

        Options opts = new OptionsBuilder()
            .include(DegreeBenchmark.class.getSimpleName())
            .warmupIterations(2)
            .measurementIterations(10)
            .forks(1)
            .param("filename", "data/ca-AstroPh.txt")
            .resultFormat(ResultFormatType.CSV)
            .result("res_" + filename + "_e_1" + ".csv")
            .mode(Mode.SingleShotTime)
            .measurementBatchSize(1)
            .timeUnit(TimeUnit.MILLISECONDS)
            .timeout(TimeValue.minutes(200))
            .build();

        new Runner(opts).run();

    }

    @Benchmark
    public double parallelDensity() {
        Densest densest = new Densest();
        UndirectedGraph graph = new UndirectedGraph(edges);
        double d = densest
            .densestSubgraphParallel(graph, epsilon);

        System.out.println("Parallel density: " + d);
        return d;
    }

    @Benchmark
    public double sequentialDensity() {
        Densest densest = new Densest();
        UndirectedGraph graph = new UndirectedGraph(edges);
        double d = densest
            .densestSubgraphSequential(graph, epsilon);

        System.out.println("Sequential density: " + d);
        return d;
    }

    @Setup(Level.Trial)
    public void setup() {
        System.out.println(epsilon);
        edges = Utility.fileToEdge(filename);
    }
}
