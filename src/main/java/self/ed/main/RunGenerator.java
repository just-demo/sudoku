package self.ed.main;

import static java.lang.Long.MAX_VALUE;
import static java.lang.System.currentTimeMillis;
import static java.util.concurrent.Executors.newSingleThreadExecutor;
import static java.util.concurrent.TimeUnit.SECONDS;
import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;

import static self.ed.main.DataDirs.READY_DIR;
import static self.ed.main.DataDirs.REDUCER_FAILED_DIR;
import static self.ed.util.FileUtils.appendFile;
import static self.ed.util.FileUtils.writeFile;
import static self.ed.util.SudokuUtils.countOpen;
import static self.ed.util.SudokuUtils.getCurrentTime;
import static self.ed.util.SudokuUtils.toString1D;
import static self.ed.util.SudokuUtils.toString2D;

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

import self.ed.exception.ComplexityLimitException;
import self.ed.generator.Generator;
import self.ed.generator.Reducer;

public class RunGenerator {

  private static final Duration GENERATOR_TIMEOUT = Duration.ofSeconds(3);
  private static final Duration REDUCER_TIMEOUT = Duration.ofSeconds(10);

  public static void main(String[] args) {
    generateMany();
  }

  private static void generateMany() {
    long start = currentTimeMillis();
    int complexityGenerateLimit = 31;
    Path reducerFailedDir = REDUCER_FAILED_DIR.resolve(getCurrentTime());
    Map<Long, Long> counts;
    try (ExecutorService executor = newSingleThreadExecutor()) {
      Generator generator = new Generator(9);
      Reducer reducer = new Reducer();

      AtomicLong totalCounter = new AtomicLong();
      AtomicLong successCounter = new AtomicLong();
      AtomicLong openMin = new AtomicLong(MAX_VALUE);
      counts = Stream.generate(() -> {
            System.out.println("Generating " + totalCounter.incrementAndGet());
            Future<Integer[][]> generateFuture = executor.submit(() -> generator.generate(complexityGenerateLimit));
            try {
              Integer[][] result = generateFuture.get(GENERATOR_TIMEOUT.toSeconds(), SECONDS);
              Long openCount = countOpen(result);
              Integer[][] res = result;
              System.out.println("Minimizing " + totalCounter.get());
              Future<Integer[][]> minimizeFuture = executor.submit(() -> reducer.reduce(res));
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
              System.out.println(
                  "Generated: " + openCount + "/" + openMin.get() + " - " + successCounter.get() + "/" + successPercentage
                      + "%");
              System.out.println(toString2D(result));
              Path readyFile = READY_DIR.resolve(openCount + ".txt");
              appendFile(readyFile.toFile(), toString1D(result) + "\n");
              System.out.println("------------------");
              return openCount;
            } catch (Exception e) {
              generateFuture.cancel(true);
              return ExceptionUtils.indexOfType(e, ComplexityLimitException.class) > -1 ? 200L : 100L;
            }
          })
          //.filter(openCount -> openCount < 100)
          .limit(10)
          .collect(groupingBy(Function.identity(), TreeMap::new, counting()));
    }

    System.out.println(counts);
    System.out.println("Time: " + (currentTimeMillis() - start) / 1000d);
  }
}
