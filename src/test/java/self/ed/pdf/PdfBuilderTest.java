package self.ed.pdf;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.junit.Test;
import self.ed.SudokuUtils;
import self.ed.solver.CleverSolver;
import self.ed.solver.SimpleSolver;
import self.ed.visitor.StatisticsCaptor;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;

import static java.nio.file.Files.createDirectories;
import static java.util.Arrays.asList;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.*;
import static org.junit.Assert.assertEquals;
import static self.ed.SudokuUtils.*;

public class PdfBuilderTest {
    private static final Path ROOT_DIR = Paths.get("C:\\Users\\pc\\Desktop\\projects\\sudoku");

    @Test
    public void testBuild() throws Exception {
//        int limit = 10;
        Path baseDir = ROOT_DIR.resolve("data");
        Path inFile = baseDir.resolve("statistics-to-print.txt");
        Path outTaskFile = baseDir.resolve(getCurrentTime() + "-task.pdf");
        Path outSolutionFile = baseDir.resolve(getCurrentTime() + "-solution.pdf");

        AtomicLong counter = new AtomicLong();
        List<Triple<Integer[][], List<String>, Integer[][]>> tables = stream(readFile(inFile.toFile()).split("\n"))
//                .limit(limit)
                .map(line -> parseSimpleString(line.split("\\|")[0].trim()))
                .map(table -> buildMetaData(counter.incrementAndGet(), table))
                .collect(toList());

        List<Pair<Integer[][], List<String>>> inputTables = tables.stream()
                .map(triple -> Pair.of(triple.getLeft(), triple.getMiddle()))
                .collect(toList());

        List<Pair<Integer[][], List<String>>> outputTables = tables.stream()
                .map(triple -> Pair.of(triple.getRight(), triple.getMiddle()))
                .collect(toList());

        Files.write(outTaskFile, new PdfBuilder(2).build(inputTables));
        Files.write(outSolutionFile, new PdfBuilder(4).build(outputTables));
    }

    private Triple<Integer[][], List<String>, Integer[][]> buildMetaData(long id, Integer[][] input) {
        StatisticsCaptor statistics = new StatisticsCaptor();
        Integer[][] output = new CleverSolver(input, statistics).solve();
        Integer[][] output2 = new SimpleSolver(input).solve();
        assertEquals(asString(output2), asString(output)); //TODO: remove
        String complexity = Stream.of(
                input.length * input.length - statistics.getInitial(),
                statistics.getMinGuesses(),
                statistics.getMaxGuesses(),
                statistics.getValueOpenings()
        ).map(Object::toString).collect(joining(" / "));
        return Triple.of(input, asList("# " + id, complexity), output);
    }

    @Test
    public void testMergeFiles() throws Exception {
        Path baseDir = ROOT_DIR.resolve("data-20171213");
        Path inDir = baseDir.resolve("ok");
        Path outDir = baseDir.resolve("merged");
        createDirectories(outDir);

        streamFiles(inDir.toFile())
                .collect(groupingBy(file -> file.getName().split("-")[0]))
                .forEach((group, files) -> {
                    Path outFile = outDir.resolve(group + ".txt");
                    String out = files.stream()
                            .map(SudokuUtils::readFile)
                            .map(content -> content.replaceAll("\\s", ""))
                            .collect(joining("\n"));
                    writeFile(outFile.toFile(), out);
                });
    }
}