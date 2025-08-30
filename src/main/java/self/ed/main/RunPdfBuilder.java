package self.ed.main;

import static java.util.Arrays.asList;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.joining;

import static self.ed.main.DataDirs.DATA_DIR;
import static self.ed.util.FileUtils.readFile;
import static self.ed.util.FileUtils.writeFile;
import static self.ed.util.SudokuUtils.fromString1D;
import static self.ed.util.SudokuUtils.getCurrentTime;

import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;

import self.ed.pdf.PdfBuilder;
import self.ed.solver.CleverSolver;
import self.ed.visitor.StatisticsCaptor;

public class RunPdfBuilder {

  public static void main(String[] args) {
    buildSudokuPdfToPrint();
  }

  private static void buildSudokuPdfToPrint() {
    Path inFile = DATA_DIR.resolve("statistics-to-print.txt");
    Path outTaskFile = DATA_DIR.resolve(getCurrentTime() + "-task.pdf");
    Path outSolutionFile = DATA_DIR.resolve(getCurrentTime() + "-solution.pdf");

    AtomicLong counter = new AtomicLong();
    List<Triple<int[][], List<String>, int[][]>> tables = stream(readFile(inFile).split("\n"))
        .map(line -> fromString1D(line.split("\\|")[0].trim()))
        .map(table -> buildTableData(counter.incrementAndGet(), table))
        .toList();

    List<Pair<int[][], List<String>>> inputTables = tables.stream()
        .map(triple -> Pair.of(triple.getLeft(), triple.getMiddle()))
        .toList();

    List<Pair<int[][], List<String>>> outputTables = tables.stream()
        .map(triple -> Pair.of(triple.getRight(), triple.getMiddle()))
        .toList();

    writeFile(outTaskFile, new PdfBuilder(2).build(inputTables));
    writeFile(outSolutionFile, new PdfBuilder(4).build(outputTables));
  }

  private static Triple<int[][], List<String>, int[][]> buildTableData(long id, int[][] input) {
    StatisticsCaptor statistics = new StatisticsCaptor();
    int[][] output = new CleverSolver(input, statistics).solve();
    String complexity = Stream.of(
        input.length * input.length - statistics.getInitial(),
        statistics.getMinGuesses(),
        statistics.getMaxGuesses(),
        statistics.getValueOpenings()
    ).map(Object::toString).collect(joining(" / "));
    return Triple.of(input, asList("# " + id, complexity), output);
  }
}
