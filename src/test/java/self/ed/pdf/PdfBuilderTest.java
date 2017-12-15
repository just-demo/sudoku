package self.ed.pdf;

import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.junit.Test;
import self.ed.SudokuSolver;
import self.ed.SudokuUtils;
import self.ed.visitor.Statistics;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import static java.nio.file.Files.createDirectories;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.*;
import static org.apache.commons.io.FileUtils.writeStringToFile;
import static org.apache.commons.lang3.StringUtils.repeat;
import static self.ed.SudokuUtils.*;

public class PdfBuilderTest {
    private static final Path ROOT_DIR = Paths.get("/Users/user/Work/projects/sudoku");

    @Test
    public void testBuild() throws Exception {
        int limit = 21;
        Path baseDir = ROOT_DIR.resolve("data");
        Path inFile = baseDir.resolve("statistics.txt");
        Path outTaskFile = baseDir.resolve("task-" + limit + ".pdf");
        Path outSolutionFile = baseDir.resolve("solution-" + limit + ".pdf");

        AtomicLong counter = new AtomicLong();
        List<Triple<Integer[][], Map<String, String>, Integer[][]>> tables = stream(readFile(inFile.toFile()).split("\n")).limit(limit)
                .map(line -> parseSimpleString(line.split(":")[0].trim()))
                .map(table -> buildMetaData(counter.incrementAndGet(), table))
                .collect(toList());

        List<Pair<Integer[][], Map<String, String>>> inputTables = tables.stream()
                .map(triple -> Pair.of(triple.getLeft(), triple.getMiddle()))
                .collect(toList());

        List<Pair<Integer[][], Map<String, String>>> outputTables = tables.stream()
                .map(triple -> Pair.of(triple.getRight(), triple.getMiddle()))
                .collect(toList());

        Files.write(outTaskFile, new PdfBuilder(2).build(inputTables));
        Files.write(outSolutionFile, new PdfBuilder(6).build(outputTables));
    }

    private Triple<Integer[][], Map<String, String>, Integer[][]> buildMetaData(long id, Integer[][] input) {
        Statistics statistics = new Statistics();
        Integer[][] output = new SudokuSolver(input, statistics).solve();
        int hidden = input.length * input.length - statistics.getInitial();
        return Triple.of(
                input,
                ImmutableMap.of(
                        "id", String.valueOf(id),
                        "complexity", hidden + " / " + statistics.getMinGuesses() + " / " + statistics.getMaxGuesses()
                ),
                output
        );
    }


    @Test
    public void testMergeFiles() throws Exception {
        Path baseDir = ROOT_DIR.resolve("data");
        Path inDir = baseDir.resolve("23");
        Path outFile = baseDir.resolve("23.txt");

        String delimiter = "\n" + repeat("-", 17) + "\n";

        String merged = streamFiles(inDir.toFile())
                .map(SudokuUtils::readFile)
                .collect(joining(delimiter));

        writeStringToFile(outFile.toFile(), merged);
    }

    @Test
    public void testMergeFiles2() throws Exception {
        Path baseDir = ROOT_DIR.resolve("data-20171214-173426");
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