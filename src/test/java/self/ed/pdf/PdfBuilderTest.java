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
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.io.FileUtils.writeStringToFile;
import static org.apache.commons.lang3.StringUtils.repeat;
import static self.ed.SudokuUtils.*;

public class PdfBuilderTest {
    private static final Path ROOT_DIR = Paths.get("/Users/user/Work/projects/sudoku");

    @Test
    public void testBuild() throws Exception {
        Path baseDir = ROOT_DIR.resolve("data-pdf");
        Path inDir = baseDir.resolve("in");
        Path outDir = baseDir.resolve("out");
        Path outFile = outDir.resolve("out.pdf");
        createDirectories(outDir);

        List<Integer[][]> tables = stream(inDir.toFile().listFiles())
                .map(file -> parseFile(readFile(file)))
                .collect(toList());

//        Files.write(outFile, new PdfBuilder().build(tables));
    }

    @Test
    public void testBuildPrepared() throws Exception {
        int limit = 21;
        Path baseDir = ROOT_DIR.resolve("data");
        Path inFile = baseDir.resolve("statistics.txt");
        Path outFile = baseDir.resolve("statistics-" + limit + ".pdf");

        AtomicLong counter = new AtomicLong();
        List<Triple<Integer[][], Map<String, String>, Integer[][]>> tables = stream(readFile(inFile.toFile()).split("\n")).limit(limit)
                .map(line -> parseFlatString(line.split(":")[0].trim()))
                .map(table -> buildMetaData(counter.incrementAndGet(), table))
                .collect(toList());

        List<Pair<Integer[][], Map<String, String>>> inputTables = tables.stream()
                .map(triple -> Pair.of(triple.getLeft(), triple.getMiddle()))
                .collect(toList());

        List<Pair<Integer[][], Map<String, String>>> outputTables = tables.stream()
                .map(triple -> Pair.of(triple.getRight(), triple.getMiddle()))
                .collect(toList());
        //                    Statistics statistics = new Statistics();
//                    new SudokuSolver(table, statistics).solve();
        // TODO: implement map: table => header lines
        Files.write(outFile, new PdfBuilder(2).build(inputTables));
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

        String merged = stream(inDir.toFile().listFiles())
                .map(SudokuUtils::readFile)
                .collect(joining(delimiter));

        writeStringToFile(outFile.toFile(), merged);
    }
}