package self.ed.generator;

import static java.lang.System.currentTimeMillis;
import static java.nio.file.Files.createDirectories;
import static java.util.Arrays.stream;
import static java.util.concurrent.TimeUnit.MINUTES;
import static java.util.concurrent.TimeUnit.SECONDS;
import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

import static org.junit.jupiter.api.Assertions.assertEquals;

import static self.ed.util.Utils.appendFile;
import static self.ed.util.Utils.asSimpleString;
import static self.ed.util.Utils.asString;
import static self.ed.util.Utils.countOpen;
import static self.ed.util.Utils.getCurrentTime;
import static self.ed.util.Utils.listFiles;
import static self.ed.util.Utils.parseFile;
import static self.ed.util.Utils.readFile;
import static self.ed.util.Utils.streamFiles;
import static self.ed.util.Utils.writeFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Test;

import self.ed.exception.ComplexityLimitException;
import self.ed.util.Utils;

public class GeneratorTest {

  @Test
  public void testReduce_AlreadyMinimal() {
    Integer[][] input = parseFile(readFile("input-21.txt"));
    Integer[][] output = new Generator(9).reduce(input);
    assertEquals(asString(input), asString(output));
  }

  @Test
  public void testReduce_MinusOne() {
    Integer[][] input = parseFile(readFile("input-22.txt"));
    Integer[][] output = new Generator(9).reduce(input);
    assertEquals(countOpen(input) - 1, countOpen(output));
  }

  @Test
  public void testReduce_MinusTwo() {
    Integer[][] input = parseFile(readFile("input-24.txt"));
    Integer[][] output = new Generator(9).reduce(input);
    assertEquals(countOpen(input) - 2, countOpen(output));
  }

  @Test
  public void testReduce_Bulk() throws IOException {
    Generator generator = new Generator(9);
    Path baseDir = Paths.get("data-failed");
    Path inDir = baseDir.resolve("failed");
    Path outDir = baseDir.resolve("ok-fixed");
    createDirectories(outDir);
    AtomicLong minimizedCount = new AtomicLong();
    List<File> files = listFiles(inDir.toFile());
    for (File file : files) {
      System.out.println(file.getName());
      Integer[][] input = parseFile(readFile(file));
      long startTime = currentTimeMillis();
      Integer[][] output = generator.reduce(input);
      System.out.println("Time: " + (currentTimeMillis() - startTime) / 1000d + "s");
      long inputCount = countOpen(input);
      long outputCount = countOpen(output);
      String outFile = outputCount + "-" + file.getName().split("-", 2)[1];
      writeFile(outDir.resolve(outFile).toFile(), asString(output));
      if (outputCount != inputCount) {
        minimizedCount.incrementAndGet();
        System.out.println("Minimized " + file.getName() + ":" + inputCount + " => " + outputCount);
      }
      file.delete();
    }
    System.out.println("Minimized " + minimizedCount.get() + " of " + files.size());
  }

  @Test
  public void testGenerate_Complex() throws IOException {
    int complexityGenerateLimit = 31;
    Path readyDir = Paths.get("data").resolve("ready");
    Path failedDir = Paths.get("data").resolve("failed").resolve(getCurrentTime());
    createDirectories(readyDir);
    ExecutorService executor = Executors.newSingleThreadExecutor();
    Generator generator = new Generator(9);

    AtomicLong totalCounter = new AtomicLong();
    AtomicLong successCounter = new AtomicLong();
    AtomicLong openMin = new AtomicLong(Long.MAX_VALUE);
    Map<Long, Long> counts = Stream.generate(() -> {
      System.out.println("Generating " + totalCounter.incrementAndGet());
      Future<Integer[][]> generateFuture = executor.submit(() -> generator.generate(complexityGenerateLimit));
      try {
        Integer[][] result = generateFuture.get(3, SECONDS);
        Long openCount = countOpen(result);
        Integer[][] res = result;
        Future<Integer[][]> minimizeFuture = executor.submit(() -> generator.reduce(res));
        try {
          result = minimizeFuture.get(10, MINUTES);
          Long newOpenCount = countOpen(result);
          if (!newOpenCount.equals(openCount)) {
            System.out.println("Minimized: " + openCount + " => " + newOpenCount);
            openCount = newOpenCount;
          }
        } catch (Exception e) {
          minimizeFuture.cancel(true);
          System.out.println("Failed to reduce: " + openCount);
          createDirectories(failedDir);
          Path failedFile = failedDir.resolve(openCount + "-" + getCurrentTime() + "-" + totalCounter.get() + ".txt");
          writeFile(failedFile.toFile(), asString(result));
          return 300L;
        }
        Long newMin = openCount;
        openMin.getAndUpdate(oldMin -> Math.min(oldMin, newMin));
        successCounter.incrementAndGet();
        long successPercentage = 100 * successCounter.get() / totalCounter.get();
        System.out.println(
            "Generated: " + openCount + "/" + openMin.get() + " - " + successCounter.get() + "/" + successPercentage
                + "%");
        System.out.println(asString(result));
        Path readyFile = readyDir.resolve(openCount + ".txt");
        appendFile(readyFile.toFile(), asSimpleString(result) + "\n");
        System.out.println("------------------");
        return openCount;
      } catch (Exception e) {
        generateFuture.cancel(true);
        return ExceptionUtils.indexOfType(e, ComplexityLimitException.class) > -1 ? 200L : 100L;
      }
    }).limit(1000000).collect(groupingBy(Function.identity(), TreeMap::new, counting()));

    System.out.println(counts);
  }

  @Test
  public void testMergeFiles() throws Exception {
    Path inDir = Paths.get("data").resolve("generated");
    Path outDir = Paths.get("data").resolve("ready");
    createDirectories(outDir);

    streamFiles(inDir.toFile())
        .collect(groupingBy(file -> file.getName().split("-")[0]))
        .forEach((group, files) -> {
          Path outFile = outDir.resolve(group + ".txt");
          String out = files.stream()
              .map(Utils::readFile)
              .map(content -> content.replaceAll("\\s", ""))
              .collect(joining("\n"));
          appendFile(outFile.toFile(), out + "\n");
        });
  }

  @Test
  public void testDuplicates() throws Exception {
    Path inDir = Paths.get("data").resolve("ready");

    List<String> tables = streamFiles(inDir.toFile())
        .map(Utils::readFile)
        .flatMap(file -> stream(file.split("\n")))
        .map(String::trim)
        .filter(StringUtils::isNotEmpty)
        .toList();

    int n = tables.size();
    Map<Pair<String, String>, Integer> distances = new HashMap<>();
    for (int i = 0; i < n; i++) {
      for (int j = i + 1; j < n; j++) {
        distances.put(Pair.of(tables.get(i), tables.get(j)), getDistance(tables.get(i), tables.get(j)));
      }
    }

    System.out.println(Collections.min(distances.values()));
    System.out.println(Collections.max(distances.values()));
    System.out.println(distances.values().stream().mapToInt(Integer::intValue).average().getAsDouble());
  }

  private int getDistance(String s1, String s2) {
    int min = Math.min(s1.length(), s2.length());
    int max = Math.max(s1.length(), s2.length());
    int distance = max - min;
    for (int i = 0; i < min; i++) {
      if (s1.charAt(i) != s2.charAt(i)) {
        distance++;
      }
    }
    return distance;
  }

  @Test
  public void testCompress() throws IOException {
    Path inDir = Paths.get("data").resolve("ready");
    Path outFile = Paths.get("data").resolve("compressed.txt");

    String out = streamFiles(inDir.toFile())
        .map(Utils::readFile)
        .flatMap(file -> stream(file.split("\n")))
        .map(String::trim)
        .filter(StringUtils::isNotEmpty)
        .map(this::compress)
        // .map(this::decompress)
        .collect(joining("\n"));

    createDirectories(outFile.getParent());
    writeFile(outFile.toFile(), out);
  }

  private String compress(String str) {
    for (char i = 'z'; i >= 'a'; i--) {
      str = str.replaceAll("\\.{" + (i - 'a' + 1) + "}", String.valueOf(i));
    }
    return str;
  }

  private String decompress(String str) {
    for (char i = 'z'; i >= 'a'; i--) {
      str = str.replaceAll(String.valueOf(i), StringUtils.repeat('.', i - 'a' + 1));
    }
    return str;
  }
}