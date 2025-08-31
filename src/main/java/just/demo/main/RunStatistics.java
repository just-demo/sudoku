package just.demo.main;

import static java.lang.System.currentTimeMillis;
import static java.util.Arrays.stream;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toMap;

import static just.demo.main.DataDirs.DATA_DIR;
import static just.demo.main.DataDirs.READY_DIR;
import static just.demo.util.FileUtils.readFile;
import static just.demo.util.FileUtils.streamFiles;
import static just.demo.util.FileUtils.writeFile;
import static just.demo.util.SudokuUtils.join;
import static just.demo.util.SudokuUtils.toString1D;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import lombok.Value;
import just.demo.solver.ComplexSolver;
import just.demo.util.CompressionUtils;
import just.demo.util.FileUtils;
import just.demo.util.SudokuUtils;
import just.demo.visitor.StatisticsCaptor;

public class RunStatistics {

  private static final Comparator<StatisticsCaptor> COMPLEXITY_COMPARATOR = comparing(StatisticsCaptor::getOpenings)
      .thenComparing(StatisticsCaptor::getMaxGuesses)
      .thenComparing(StatisticsCaptor::getMinGuesses)
      .thenComparing(StatisticsCaptor::getInitial);

  public static void main(String[] args) {
    printSudokuCountPerType();
    printSudokuTotalCount();
//    findDuplicates
//    buildExtendedStatisticsFile();
//    compressReadySudoku();
  }

  private static void printSudokuCountPerType() {
    Map<Long, Long> counts = streamFiles(READY_DIR).collect(toMap(
        file -> Long.valueOf(file.getName().split("\\.")[0]),
        file -> stream(readFile(file).split("\n"))
            .map(String::trim)
            .filter(StringUtils::isNotEmpty)
            .count()));

    Map<Long, Double> countsLog = counts.entrySet().stream().collect(toMap(
        Map.Entry::getKey,
        entry -> Math.log10(entry.getValue())));

    Map<Long, Double> countsLn = counts.entrySet().stream().collect(toMap(
        Map.Entry::getKey,
        entry -> Math.log(entry.getValue())));

    print(counts);
    System.out.println();
    print(countsLog);
    System.out.println();
    print(countsLn);
  }

  private static void printSudokuTotalCount() {
    Path baseDir = Paths.get("data");
    Path inDir = baseDir.resolve("ready");

    System.out.println(streamFiles(inDir)
        .map(FileUtils::readFile)
        .flatMap(file -> stream(file.split("\n")))
        .map(String::trim)
        .filter(StringUtils::isNotEmpty)
        .count());

  }

  private static void findDuplicates() {
    List<String> tables = streamFiles(READY_DIR)
        .map(FileUtils::readFile)
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

  private static int getDistance(String s1, String s2) {
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

  private static void buildExtendedStatisticsFile() {
    Path outFile = DATA_DIR.resolve("statistics-extended.txt");

    List<int[][]> tables = streamFiles(READY_DIR)
        .map(FileUtils::readFile)
        .flatMap(file -> stream(file.split("\n")))
        .map(String::trim)
        .filter(StringUtils::isNotEmpty)
        .map(SudokuUtils::fromString1D)
        .toList();

    AtomicInteger progress = new AtomicInteger();
    AtomicLong maxTime = new AtomicLong(Long.MIN_VALUE);
    String out = tables.stream()
        .map(table -> {
          System.out.println(progress.incrementAndGet() + "/" + tables.size());
          StatisticsCaptor statistics = new StatisticsCaptor();
          long startTime = currentTimeMillis();
          int[][] solution = new ComplexSolver(table, statistics).solve();
          long time = currentTimeMillis() - startTime;
          maxTime.getAndAccumulate(time, Math::max);
          return new StatisticsWrapper(toString1D(table), toString1D(solution), time, statistics);
        })
        .sorted(comparing(StatisticsWrapper::getStatistics, COMPLEXITY_COMPARATOR)
            .thenComparing(StatisticsWrapper::getInput))
        .map(StatisticsWrapper::toString)
        .collect(joining("\n"));
    System.out.println("Max time = " + maxTime.get());
    writeFile(outFile, out + "\n");
  }

  private static void compressReadySudoku() {
    Path outFile = DATA_DIR.resolve("compressed.txt");

    String out = streamFiles(READY_DIR)
        .map(FileUtils::readFile)
        .flatMap(file -> stream(file.split("\n")))
        .map(String::trim)
        .filter(StringUtils::isNotEmpty)
        .map(CompressionUtils::compress)
        // .map(CompressionUtils::decompress)
        .collect(joining("\n"));

    writeFile(outFile, out);
  }

  private static void print(Map<?, ?> map) {
    map.forEach((key, val) -> System.out.println(key + "\t" + val));
  }

  @Value
  private static class StatisticsWrapper {

    String input;
    String output;
    long time;
    StatisticsCaptor statistics;

    @Override
    public String toString() {
      return join(" | ", input, output, time, statistics.getOpenings(), statistics.getCellOpenings(),
          statistics.getValueOpenings(), statistics.getMaxGuesses(), statistics.getMinGuesses(),
          statistics.getInitial());
    }
  }
}