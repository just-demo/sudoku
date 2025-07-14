package self.ed.solver;

import static java.lang.System.currentTimeMillis;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;
import static self.ed.util.Utils.readFile;
import static self.ed.util.Utils.streamFiles;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import self.ed.util.Utils;

public class TestSpeed {

  public static void main(String[] args) {
    String sudokuStr = ".......9......8.2.7.3.54.....52.................6....88....3..7.9....6..6...8...4";
    Integer[][] sudoku = Utils.parseSimpleString(sudokuStr);
    long start = currentTimeMillis();
    Integer[][] solution = new CleverSolver(sudoku).solve();
    System.out.println(Utils.asString(solution));
    System.out.println("Time: " + (currentTimeMillis() - start));
  }

  public static void main_(String[] args) {
    StringUtils.strip("asd", "\"");
    Path readyDir = Paths.get("data").resolve("ready");
    List<File> files = streamFiles(readyDir.toFile()).sorted().collect(toList());
    Map<String, Long> counts = files.stream()
        .collect(Collectors.toMap(File::getName, TestSpeed::countLines));
    long count = counts.values().stream().mapToLong(i -> i).sum();
    System.out.println(counts);
    System.out.println("Total count: " + count); // 1006520
    AtomicLong counter = new AtomicLong();
    long start = currentTimeMillis();
    Map<String, Pair<Long, Long>> statistics = new LinkedHashMap<>();
    for (File file : files) {
      long fileStart = currentTimeMillis();
      long fileCount = streamLines(file).map(Utils::parseSimpleString).map(sudoku -> {
        long progress = counter.incrementAndGet();
        if (progress % 1000 == 0) {
          System.out.println("progress: " + progress + " / " + ((currentTimeMillis() - start) / 1000) + "s");
        }
        if (progress > 10000) {
          throw new RuntimeException("stopped");
        }
//        return new CleverSolver(sudoku).solve();
        return new SimpleSolver(sudoku).solve();
      }).count();
      long fileEnd = currentTimeMillis();
      statistics.put(file.getName(), Pair.of(fileCount, fileEnd - fileStart));
    }
    long end = currentTimeMillis();
    System.out.println("Total count: " + count); // 1006520
    System.out.println("Total time: " + (end - start) / 1000);
    statistics.forEach((file, stat) -> {
      System.out.println(file + ": " + stat.getRight() + " / " + ((double) stat.getRight() / stat.getLeft()));
    });
  }

  /*
Clever:
progress: 1000 / 4s
progress: 2000 / 8s
progress: 3000 / 12s
progress: 4000 / 16s
progress: 5000 / 19s
progress: 6000 / 22s
progress: 7000 / 26s
progress: 8000 / 29s
progress: 9000 / 33s
progress: 10000 / 36s
Simple:
progress: 1000 / 41s
progress: 2000 / 87s
progress: 3000 / 114s
progress: 4000 / 137s
progress: 5000 / 159s
progress: 6000 / 179s
progress: 7000 / 202s
progress: 8000 / 227s
progress: 9000 / 250s
progress: 10000 / 274s
   */

  private static Long countLines(File file) {
    return streamLines(file).count();
  }

  private static Stream<String> streamLines(File file) {
    return stream(readFile(file).split("\n"))
        .filter(StringUtils::isNotEmpty);
  }

}
