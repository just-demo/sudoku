package self.ed.generator;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.junit.Test;
import self.ed.exception.ComplexityLimitException;
import self.ed.util.Utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.lang.System.currentTimeMillis;
import static java.nio.file.Files.createDirectories;
import static java.util.concurrent.TimeUnit.SECONDS;
import static java.util.stream.Collectors.*;
import static org.apache.commons.io.FileUtils.readFileToString;
import static org.junit.Assert.assertEquals;
import static self.ed.util.Utils.*;

public class GeneratorTest {

    @Test
    public void testReduce_AlreadyMinimal() {
        Integer[][] input = parseFile(readFile("input-21.txt"));
        Integer[][] output = new Generator(9).reduce(input);
        assertEquals(asString(input), asString(output));
    }

    @Test
    public void testReduce_MinusOne() {
        Integer[][] input = parseFile(readFile("input-22.txt"));
        Integer[][] output = new Generator(9).reduce(input);
        assertEquals(countOpen(input) - 1, countOpen(output));
    }

    @Test
    public void testReduce_MinusTwo() {
        Integer[][] input = parseFile(readFile("input-24.txt"));
        Integer[][] output = new Generator(9).reduce(input);
        assertEquals(countOpen(input) - 2, countOpen(output));
    }

    @Test
    public void testReduce_Bulk() throws IOException {
        Generator generator = new Generator(9);
        Path baseDir = Paths.get("data-failed");
        Path inDir = baseDir.resolve("failed");
        Path outDir = baseDir.resolve("ok-fixed");
        createDirectories(outDir);
        AtomicLong minimizedCount = new AtomicLong();
        File[] files = inDir.toFile().listFiles();
        for (File file : files) {
            System.out.println(file.getName());
            Integer[][] input = parseFile(readFileToString(file));
            long startTime = currentTimeMillis();
            Integer[][] output = generator.reduce(input);
            System.out.println("Time: " + (currentTimeMillis() - startTime) / 1000d + "s");
            long inputCount = countOpen(input);
            long outputCount = countOpen(output);
            String outFile = outputCount + "-" + file.getName().split("-", 2)[1];
            writeFile(outDir.resolve(outFile).toFile(), asString(output));
            if (outputCount != inputCount) {
                minimizedCount.incrementAndGet();
                System.out.println("Minimized " + file.getName() + ":" + inputCount + " => " + outputCount);
            }
            file.delete();
        }
        System.out.println("Minimized " + minimizedCount.get() + " of " + files.length);
    }

    @Test
    public void testGenerate_Complex() throws IOException {
        int complexityGenerateLimit = 31;
        int complexitySaveLimit = 81;
        Path basedDir = Paths.get("data").resolve("generated").resolve(getCurrentTime());
        Path okDir = basedDir.resolve("ok");
        Path failedDir = basedDir.resolve("failed");
        createDirectories(okDir);
        createDirectories(failedDir);
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Generator generator = new Generator(9);

        AtomicLong sudokuNumber = new AtomicLong();
        AtomicLong openMin = new AtomicLong(Long.MAX_VALUE);
        Map<Long, Long> counts = Stream.generate(() -> {
            System.out.println("Sudoku " + sudokuNumber.incrementAndGet());
            Future<Integer[][]> generateFuture = executor.submit(() -> generator.generate(complexityGenerateLimit));
            try {
                Integer[][] result = generateFuture.get(2, SECONDS);
                Long openCount = countOpen(result);
                Integer[][] res = result;
                Future<Integer[][]> minimizeFuture = executor.submit(() -> generator.reduce(res));
                try {
                    result = minimizeFuture.get(60, SECONDS);
                    Long newOpenCount = countOpen(result);
                    if (!newOpenCount.equals(openCount)) {
                        System.out.println("Minimized: " + openCount + " => " + newOpenCount);
                        openCount = newOpenCount;
                    }
                } catch (Exception e) {
                    minimizeFuture.cancel(true);
                    System.out.println("Failed to reduce: " + openCount);
                    Path file = failedDir.resolve(openCount + "-" + getCurrentTime() + "-" + sudokuNumber.get() + ".txt");
                    writeFile(file.toFile(), asString(result));
                    return 300L;
                }
                Long newMin = openCount;
                openMin.getAndUpdate(oldMin -> Math.min(oldMin, newMin));
                System.out.println("Open: " + openCount + "/" + openMin.get());
                if (openCount <= complexitySaveLimit) {
                    System.out.println(asString(result));
                    Path file = okDir.resolve(openCount + "-" + getCurrentTime() + "-" + sudokuNumber.get() + ".txt");
                    writeFile(file.toFile(), asString(result));
                }
                System.out.println("------------------");
                return openCount;
            } catch (Exception e) {
                generateFuture.cancel(true);
                return ExceptionUtils.indexOfType(e, ComplexityLimitException.class) > -1 ? 200L : 100L;
            }
        }).limit(100).collect(groupingBy(Function.identity(), TreeMap::new, counting()));

        System.out.println(counts);
    }

    @Test
    public void testMergeFiles() throws Exception {
        Path inDir = Paths.get("data").resolve("generated");
        Path outDir = Paths.get("data").resolve("ready");
        createDirectories(outDir);

        streamFiles(inDir.toFile())
                .collect(groupingBy(file -> file.getName().split("-")[0]))
                .forEach((group, files) -> {
                    Path outFile = outDir.resolve(group + ".txt");
                    String out = files.stream()
                            .map(Utils::readFile)
                            .map(content -> content.replaceAll("\\s", ""))
                            .collect(joining("\n"));
                    appendFile(outFile.toFile(), out + "\n");
                });
    }
}