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
import static self.ed.SudokuUtils.asString;
import static self.ed.SudokuUtils.parseFile;
import static self.ed.SudokuUtils.readFile;

public class SudokuGeneratorTest {

//    @Test
//    public void testGenerateSimple() {
//        Integer[][] input = parseFile(readFile("input-simple.txt"));
//        Integer[][] output = parseFile(readFile("input-simple.txt"));
//        SudokuGenerator sudoku = new SudokuGenerator();
//        assertEquals(asString(output), asString(sudoku.generate(input)));
//    }

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
            Future<Long> future = executor.submit(() -> {
                Integer[][] result = generator.generate();
                Long openCount = stream(result).mapToLong(line -> stream(line).filter(Objects::nonNull).count()).sum();
                openMin.getAndUpdate(old -> Math.min(old, openCount));
                System.out.println("Open: " + openCount + "/" + openMin.get());
                if (openCount < 24) {
                    System.out.println(asString(result));
                }
                System.out.println("------------------");
                return openCount;
            });
            try {
                return future.get(2, SECONDS);
            } catch (Exception e) {
                future.cancel(true);
                return 100L;
            }
        }).limit(10).collect(groupingBy(Function.identity(), TreeMap::new, counting()));


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