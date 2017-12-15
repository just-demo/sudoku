package self.ed.visitor;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.Test;
import self.ed.SudokuSolver;
import self.ed.SudokuUtils;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static java.util.Arrays.stream;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;
import static self.ed.SudokuUtils.asFlatString;
import static self.ed.SudokuUtils.parseFile;
import static self.ed.SudokuUtils.readFile;
import static self.ed.visitor.Statistics.COMPLEXITY_COMPARATOR;

public class StatisticsTest {

    @Test
    public void testCapture() {
        Path baseDir = Paths.get("C:\\Users\\pc\\Desktop\\projects\\sudoku\\data-pdf");
        Path inDir = baseDir.resolve("in");
        asList(inDir.toFile().listFiles()).forEach(file -> {
            Integer[][] table = parseFile(readFile(file));
            Statistics statistics = new Statistics();
            new SudokuSolver(table, statistics).solve();
            System.out.println(file.getName() + ": " + statistics.toString());
        });
    }

    @Test
    public void testReadAllFiles() throws Exception {
        Path baseDir = Paths.get("C:\\Users\\pc\\Desktop\\projects\\sudoku\\data");
        long totalCount = Stream.of("20.txt", "21.txt", "22.txt", "23.txt")
                .map(baseDir::resolve)
                .map(Path::toFile)
                .map(SudokuUtils::readFile)
                .flatMap(file -> stream(file.split("-+")))
                .map(String::trim)
                .count();
        System.out.println(totalCount);
    }

    @Test
    public void testGetStatistics() {
        Path baseDir = Paths.get("C:\\Users\\pc\\Desktop\\projects\\sudoku\\data");

        List<Integer[][]> tables = Stream.of("20.txt", "21.txt", "22.txt", "23.txt")
                .map(baseDir::resolve)
                .map(Path::toFile)
                .map(SudokuUtils::readFile)
                .flatMap(file -> stream(file.split("-+")))
                .map(String::trim)
                .map(SudokuUtils::parseFile)
                .collect(toList());

        AtomicInteger progress = new AtomicInteger();
        tables.stream()
                .map(table -> {
                    System.out.println(progress.incrementAndGet() + "/" + tables.size());
                    Statistics statistics = new Statistics();
                    new SudokuSolver(table, statistics).solve();
                    return Pair.of(asFlatString(table), statistics);
                })
                .sorted(comparing(Pair::getValue, COMPLEXITY_COMPARATOR))
                .forEach(pair ->
                        System.out.println(pair.getKey() + ": " + pair.getValue())
                );
    }
}