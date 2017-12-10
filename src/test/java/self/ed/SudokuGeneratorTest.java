package self.ed;

import org.junit.Test;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.Arrays.stream;
import static java.util.concurrent.TimeUnit.SECONDS;
import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;
import static org.junit.Assert.*;
import static self.ed.SudokuUtils.*;

public class SudokuGeneratorTest {

//    @Test
//    public void testGenerateSimple() {
//        Integer[][] input = parseFile(readFile("input-simple.txt"));
//        Integer[][] output = parseFile(readFile("input-simple.txt"));
//        SudokuGenerator sudoku = new SudokuGenerator();
//        assertEquals(asString(output), asString(sudoku.generate(input)));
//    }

    @Test
    public void testMinimize() {
        Integer[][] input = parseFile(readFile("input-30.txt"));
        Integer[][] output = parseFile(readFile("input-30.txt"));
        SudokuGenerator sudoku = new SudokuGenerator(9);
        assertEquals(asString(output), asString(sudoku.minimize(input)));
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
    public void testGenerateComplex2() {
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
                if (openCount <= 25) {
                    Integer[][] res = result;
                    Future<Integer[][]> minimizeFuture = executor.submit(() -> generator.minimize(res));
                    try {
                        result = minimizeFuture.get(10, SECONDS);
                    } catch (Exception e) {
                        minimizeFuture.cancel(true);
                        System.out.println("Cannot minimize: " + openCount);
                        System.out.println(asString(result));
                        return 200L;
                    }
                }
                openMin.getAndUpdate(old -> Math.min(old, openCount));
                System.out.println("Open: " + openCount + "/" + openMin.get());
                if (openCount < 24) {
                    System.out.println(asString(result));
                }
                System.out.println("------------------");
                return openCount;
            } catch (Exception e) {
                generateFuture.cancel(true);
                return 100L;
            }
        }).limit(500).collect(groupingBy(Function.identity(), TreeMap::new, counting()));


        System.out.println(counts);
//        System.out.println(counts.keySet().stream().mapToLong(Long::longValue).min().getAsLong());
//        System.out.println(toString(result));
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