package self.ed.visitor;

import static java.lang.System.currentTimeMillis;
import static java.util.Arrays.stream;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toMap;

import static self.ed.util.Utils.asSimpleString;
import static self.ed.util.Utils.join;
import static self.ed.util.Utils.readFile;
import static self.ed.util.Utils.streamFiles;
import static self.ed.util.Utils.writeFile;
import static self.ed.visitor.StatisticsCaptor.COMPLEXITY_COMPARATOR;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;

import self.ed.solver.CleverSolver;
import self.ed.util.Utils;

public class StatisticsCaptorTest {

  @Test
  public void testCountsPerType() {
    Path baseDir = Paths.get("data");
    Path inDir = baseDir.resolve("ready");
    Map<Long, Long> counts = streamFiles(inDir.toFile()).collect(toMap(
        file -> Long.valueOf(file.getName().split("\\.")[0]),
        file -> stream(readFile(file).split("\n"))
            .map(String::trim)
            .filter(StringUtils::isNotEmpty)
            .count()
    ));

    Map<Long, Double> countsLog = counts.entrySet().stream()
        .collect(toMap(
            Map.Entry::getKey,
            entry -> Math.log10(entry.getValue())
        ));

    Map<Long, Double> countsLn = counts.entrySet().stream()
        .collect(toMap(
            Map.Entry::getKey,
            entry -> Math.log(entry.getValue())
        ));

    print(counts);
    System.out.println();
    print(countsLog);
    System.out.println();
    print(countsLn);
  }

  private void print(Map<?, ?> map) {
    map.forEach((key, val) -> System.out.println(key + "\t" + val));
  }

  @Test
  public void testCount() {
    Path baseDir = Paths.get("data");
    Path inDir = baseDir.resolve("ready");

    System.out.println(streamFiles(inDir.toFile())
        .map(Utils::readFile)
        .flatMap(file -> stream(file.split("\n")))
        .map(String::trim)
        .filter(StringUtils::isNotEmpty)
        .count());

  }

  @Test
  public void testStatistics() {
    Path baseDir = Paths.get("data");
    Path inDir = baseDir.resolve("ready");
    Path outFile = baseDir.resolve("statistics-full.txt");

    List<Integer[][]> tables = streamFiles(inDir.toFile())
        .map(Utils::readFile)
        .flatMap(file -> stream(file.split("\n")))
        .map(String::trim)
        .filter(StringUtils::isNotEmpty)
        .map(Utils::parseSimpleString)
        .toList();

    AtomicInteger progress = new AtomicInteger();
    AtomicLong maxTime = new AtomicLong(Long.MIN_VALUE);
    String out = tables.stream()
        .map(table -> {
          System.out.println(progress.incrementAndGet() + "/" + tables.size());
          StatisticsCaptor statistics = new StatisticsCaptor();
          long startTime = currentTimeMillis();
          Integer[][] solution = new CleverSolver(table, statistics).solve();
          long time = currentTimeMillis() - startTime;
          maxTime.getAndAccumulate(time, Math::max);
          return new StatisticsWrapper(asSimpleString(table), asSimpleString(solution), time, statistics);
        })
        .sorted(comparing(StatisticsWrapper::getStatistics, COMPLEXITY_COMPARATOR)
            .thenComparing(StatisticsWrapper::getInput))
        .map(StatisticsWrapper::toString)
        .collect(joining("\n"));
    System.out.println("Max time = " + maxTime.get());
    writeFile(outFile.toFile(), out + "\n");
  }

  private static class StatisticsWrapper {

    String input;
    String output;
    long time;
    StatisticsCaptor statistics;

    StatisticsWrapper(String input, String output, long time, StatisticsCaptor statistics) {
      this.input = input;
      this.output = output;
      this.time = time;
      this.statistics = statistics;
    }

    String getInput() {
      return input;
    }

    String getOutput() {
      return output;
    }

    long getTime() {
      return time;
    }

    StatisticsCaptor getStatistics() {
      return statistics;
    }

    @Override
    public String toString() {
      return join(" | ", input, output, time, statistics.getOpenings(), statistics.getCellOpenings(),
          statistics.getValueOpenings(), statistics.getMaxGuesses(), statistics.getMinGuesses(),
          statistics.getInitial());
    }
  }
}