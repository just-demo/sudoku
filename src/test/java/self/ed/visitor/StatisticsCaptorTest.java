package self.ed.visitor;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Test;
import self.ed.solver.CleverSolver;
import self.ed.util.Utils;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

import static java.util.Arrays.stream;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static self.ed.util.Utils.*;
import static self.ed.visitor.StatisticsCaptor.COMPLEXITY_COMPARATOR;

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
    public void testStatistics() {
        Path baseDir = Paths.get("data");
        Path inDir = baseDir.resolve("ready");
        Path outFile = baseDir.resolve("statistics.txt");

        List<Integer[][]> tables = streamFiles(inDir.toFile())
                .map(Utils::readFile)
                .flatMap(file -> stream(file.split("\n")))
                .map(String::trim)
                .filter(StringUtils::isNotEmpty)
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
                .sorted(comparing((Function<Pair<String, StatisticsCaptor>, StatisticsCaptor>) Pair::getValue, COMPLEXITY_COMPARATOR)
                        .thenComparing(Pair::getKey))
                .map(pair -> pair.getKey() + " | " + pair.getValue())
                .collect(joining("\n"));
        writeFile(outFile.toFile(), out + "\n");
    }
}