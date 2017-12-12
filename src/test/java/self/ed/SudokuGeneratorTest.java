package self.ed;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.junit.Test;
import self.ed.exception.CountLimitException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.nio.file.Files.createDirectories;
import static java.util.Arrays.stream;
import static java.util.concurrent.TimeUnit.SECONDS;
import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;
import static org.apache.commons.io.FileUtils.readFileToString;
import static org.apache.commons.io.FileUtils.writeStringToFile;
import static org.junit.Assert.assertEquals;
import static self.ed.SudokuUtils.*;

public class SudokuGeneratorTest {
    private static final Path ROOT_DIR = Paths.get("C:\\Users\\pc\\Desktop\\projects\\sudoku");

//    @Test
//    public void testGenerateSimple() {
//        Integer[][] input = parseFile(readFile("input-simple.txt"));
//        Integer[][] output = parseFile(readFile("input-simple.txt"));
//        SudokuGenerator sudoku = new SudokuGenerator();
//        assertEquals(asString(output), asString(sudoku.generate(input)));
//    }

    @Test
    public void testMinimizeAlreadyMinimal() {
        Integer[][] input = parseFile(readFile("input-21.txt"));
        Integer[][] output = new SudokuGenerator(9).minimize(input);
        assertEquals(asString(input), asString(output));
    }

    @Test
    public void testMinimizeMinusOne() {
        Integer[][] input = parseFile(readFile("input-22.txt"));
        Integer[][] output = new SudokuGenerator(9).minimize(input);
        assertEquals(countOpen(input) - 1, countOpen(output));
    }

    @Test
    public void testMinimizeMinusTwo() {
        Integer[][] input = parseFile(readFile("input-24.txt"));
        Integer[][] output = new SudokuGenerator(9).minimize(input);
        assertEquals(countOpen(input) - 2, countOpen(output));
    }

    @Test
    public void testMinimizeBulk() throws IOException {
        SudokuGenerator sudoku = new SudokuGenerator(9);
        Path inDir = ROOT_DIR.resolve("data-failed").resolve("failed");
        Path outDir = ROOT_DIR.resolve("data-failed").resolve("ok");
        createDirectories(outDir);
        AtomicLong minimizedCount = new AtomicLong();
        File[] files = inDir.toFile().listFiles();
        for (File file : files) {
            System.out.println(file.getName());
            Integer[][] input = parseFile(readFileToString(file));
            Integer[][] output = sudoku.minimize(input);
            long inputCount = countOpen(input);
            long outputCount = countOpen(output);
            String outFile = outputCount + "-" + file.getName().split("-", 2)[1];
            writeStringToFile(outDir.resolve(outFile).toFile(), asString(output));
            if (outputCount != inputCount) {
                minimizedCount.incrementAndGet();
                System.out.println("Minimized " + file.getName() + ":" + inputCount + " => " + outputCount);
            }
        }
        System.out.println("Minimized " + minimizedCount.get() + " of " + files.length);
    }

    @Test
    public void testGenerateComplex() {
        Map<Long, List<Long>> counts = Stream.generate(() -> {
            Integer[][] result = new SudokuGenerator(9).generate();
            System.out.println(asString(result));
            return stream(result).mapToLong(line -> stream(line).filter(Objects::nonNull).count()).sum();
        }).limit(10).collect(groupingBy(Function.identity()));


        System.out.println(counts);
        System.out.println(counts.keySet().stream().mapToLong(Long::longValue).min().getAsLong());
//        System.out.println(toString(result));
    }

    @Test
    public void testGenerateComplex2() throws IOException {
        Path basedDir = ROOT_DIR.resolve("data-" + getCurrentTime());
        Path okDir = basedDir.resolve("ok");
        Path failedDir = basedDir.resolve("failed");
        createDirectories(okDir);
        createDirectories(failedDir);
        ExecutorService executor = Executors.newSingleThreadExecutor();
        SudokuGenerator generator = new SudokuGenerator(9);

        AtomicLong sudokuNumber = new AtomicLong();
        AtomicLong openMin = new AtomicLong(Long.MAX_VALUE);
        Map<Long, Long> counts = Stream.generate(() -> {
            System.out.println("Sudoku " + sudokuNumber.incrementAndGet());
            Future<Integer[][]> generateFuture = executor.submit(() -> generator.generate());
            try {
                Integer[][] result = generateFuture.get(2, SECONDS);
                Long openCount = countOpen(result);
//                if (openCount <= OPEN_LIMIT) { // TODO: It's always true since the generator itself limits it!!!
//                Integer[][] res = result;
//                Future<Integer[][]> minimizeFuture = executor.submit(() -> generator.minimize(res));
//                try {
//                    result = minimizeFuture.get(10, SECONDS);
//                    Long newOpenCount = countOpen(result);
//                    if (!newOpenCount.equals(openCount)) {
//                        System.out.println("Minimized: " + openCount + " => " + newOpenCount);
//                        openCount = newOpenCount;
//                    }
//                } catch (Exception e) {
//                    minimizeFuture.cancel(true);
//                    System.out.println("Cannot minimize: " + openCount);
//                    Path file = failedDir.resolve(openCount + "-" + getCurrentTime() + "-" + sudokuNumber.get() + ".txt");
//                    writeStringToFile(file.toFile(), asString(result));
//                    return 300L;
//                }
//                }
                Long newMin = openCount;
                openMin.getAndUpdate(oldMin -> Math.min(oldMin, newMin));
                System.out.println("Open: " + openCount + "/" + openMin.get());
                if (openCount < 24) {
                    System.out.println(asString(result));
                    Path file = okDir.resolve(openCount + "-" + getCurrentTime() + "-" + sudokuNumber.get() + ".txt");
                    writeStringToFile(file.toFile(), asString(result));
                }
                System.out.println("------------------");
                return openCount;
            } catch (Exception e) {
                generateFuture.cancel(true);
                return ExceptionUtils.indexOfType(e, CountLimitException.class) > -1 ? 200L : 100L;
            }
        }).limit(100).collect(groupingBy(Function.identity(), TreeMap::new, counting()));


        System.out.println(counts);
//        System.out.println(counts.keySet().stream().mapToLong(Long::longValue).min().getAsLong());
//        System.out.println(toString(result));
    }

    private String getCurrentTime() {
        return new SimpleDateFormat("yyyyMMdd-HHmmss").format(new Date());
    }

    /*

        try {
            System.out.println("Started..");
            System.out.println(future.get(3, TimeUnit.SECONDS));
            System.out.println("Finished!");
        } catch (TimeoutException e) {
            future.cancel(true);
            System.out.println("Terminated!");
        }

        executor.shutdownNow();
    }
}

class Task implements Callable<String> {
    @Override
    public String call() throws Exception {
        Thread.sleep(4000); // Just to demo a long running task of 4 seconds.
        return "Ready!";
    }
}
     */

}