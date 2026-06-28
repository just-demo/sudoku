package just.demo.main;

import static java.lang.Long.MAX_VALUE;
import static java.lang.Runtime.getRuntime;
import static java.lang.System.currentTimeMillis;
import static java.util.concurrent.Executors.newFixedThreadPool;
import static java.util.concurrent.TimeUnit.SECONDS;
import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;

import static just.demo.main.DataDirs.READY_DIR;
import static just.demo.main.DataDirs.REDUCER_FAILED_DIR;
import static just.demo.util.FileUtils.appendFile;
import static just.demo.util.FileUtils.writeFile;
import static just.demo.util.SudokuUtils.countOpen;
import static just.demo.util.SudokuUtils.getCurrentTime;
import static just.demo.util.SudokuUtils.toString1D;
import static just.demo.util.SudokuUtils.toString2D;

import java.nio.file.Path;
import java.time.Duration;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;
import java.util.stream.Stream;

import org.apache.commons.lang3.exception.ExceptionUtils;

import just.demo.exception.ComplexityLimitException;
import just.demo.generator.Generator;
import just.demo.generator.Reducer;

public class RunGenerator {

  private static final Duration GENERATOR_TIMEOUT = Duration.ofSeconds(5);
  private static final Duration REDUCER_TIMEOUT = Duration.ofMinutes(10);

  public static void main(String[] args) {
    generateMany();
  }

  private static void generateMany() {
    long start = currentTimeMillis();
    Path reducerFailedDir = REDUCER_FAILED_DIR.resolve(getCurrentTime());
    Map<Long, Long> counts;
//    try (ExecutorService executor = newSingleThreadExecutor()) {
//    try (ExecutorService executor = newFixedThreadPool(getRuntime().availableProcessors())) {
    try (ExecutorService executor = newFixedThreadPool(4)) {
      Generator generator = new Generator(9, 31);
      Reducer reducer = new Reducer();

      AtomicLong totalCounter = new AtomicLong();
      AtomicLong successCounter = new AtomicLong();
      AtomicLong openMin = new AtomicLong(MAX_VALUE);
      counts = Stream.generate(() -> {
            System.out.println("Generating " + totalCounter.incrementAndGet());
            Future<int[][]> generateFuture = executor.submit(generator::generate);
            try {
              int[][] result = generateFuture.get(GENERATOR_TIMEOUT.toSeconds(), SECONDS);
              Long openCount = countOpen(result);
              int[][] res = result;
              System.out.println("Minimizing " + totalCounter.get());
              Future<int[][]> minimizeFuture = executor.submit(() -> reducer.reduce(res));
              try {
                result = minimizeFuture.get(REDUCER_TIMEOUT.toSeconds(), SECONDS);
                Long newOpenCount = countOpen(result);
                if (!newOpenCount.equals(openCount)) {
                  System.out.println("Minimized: " + openCount + " => " + newOpenCount);
                  openCount = newOpenCount;
                }
              } catch (Exception e) {
                minimizeFuture.cancel(true);
                System.out.println("Failed to reduce: " + openCount);
                Path failedFile = reducerFailedDir.resolve(
                    openCount + "-" + getCurrentTime() + "-" + totalCounter.get() + ".txt");
                writeFile(failedFile.toFile(), toString2D(result));
                return 300L;
              }
              Long newMin = openCount;
              openMin.getAndUpdate(oldMin -> Math.min(oldMin, newMin));
              successCounter.incrementAndGet();
              long successPercentage = 100 * successCounter.get() / totalCounter.get();
              System.out.println("Generated: " + openCount + "/" + openMin.get() + " - " + successCounter.get() + "/" +
                  successPercentage + "%");
              System.out.println(toString2D(result));
              Path readyFile = READY_DIR.resolve(openCount + ".txt");
              synchronized (RunGenerator.class) {
                appendFile(readyFile.toFile(), toString1D(result) + "\n");
              }
              System.out.println("------------------");
              return openCount;
            } catch (Exception e) {
              System.out.println("Failed to generate");
              generateFuture.cancel(true);
              return ExceptionUtils.indexOfType(e, ComplexityLimitException.class) > -1 ? 200L : 100L;
            }
          })
          .parallel()
          .filter(openCount -> openCount < 100)
//          .limit(100_000)
          .collect(groupingBy(Function.identity(), TreeMap::new, counting()));
    }

    System.out.println(counts);
    System.out.println("Time: " + (currentTimeMillis() - start) / 1000d);
  }
}
