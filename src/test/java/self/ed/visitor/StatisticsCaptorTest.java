package self.ed.visitor;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.Test;
import self.ed.util.Utils;
import self.ed.solver.CleverSolver;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.Arrays.stream;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static self.ed.util.Utils.*;
import static self.ed.visitor.StatisticsCaptor.COMPLEXITY_COMPARATOR;

public class StatisticsCaptorTest {
    private static final Path ROOT_DIR = Paths.get("C:\\Users\\pc\\Desktop\\projects\\sudoku");

    @Test
    public void testStatistics() {
        Path baseDir = ROOT_DIR.resolve("data");
        Path inDir = baseDir.resolve("ready");
        Path outFile = baseDir.resolve("statistics-" + getCurrentTime() + ".txt");

        List<Integer[][]> tables = streamFiles(inDir.toFile())
                .map(Utils::readFile)
                .flatMap(file -> stream(file.split("\n")))
                .map(String::trim)
                .map(Utils::parseSimpleString)
                .collect(toList());

        AtomicInteger progress = new AtomicInteger();
        String out = tables.stream()
                .map(table -> {
                    System.out.println(progress.incrementAndGet() + "/" + tables.size());
                    StatisticsCaptor statistics = new StatisticsCaptor();
                    new CleverSolver(table, statistics).solve();
                    return Pair.of(asSimpleString(table), statistics);
                })
                .sorted(comparing(Pair::getValue, COMPLEXITY_COMPARATOR))
                .map(pair -> pair.getKey() + " | " + pair.getValue())
                .collect(joining("\n"));
        writeFile(outFile.toFile(), out);
    }
}