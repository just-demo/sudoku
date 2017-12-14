package self.ed.visitor;

import org.junit.Test;
import self.ed.SudokuSolver;

import java.nio.file.Path;
import java.nio.file.Paths;

import static java.util.Arrays.asList;
import static self.ed.SudokuUtils.parseFile;
import static self.ed.SudokuUtils.readFile;

public class StatisticsCaptorTest {

    @Test
    public void testCapture() {
        Path baseDir = Paths.get("C:\\Users\\pc\\Desktop\\projects\\sudoku\\data-pdf");
        Path inDir = baseDir.resolve("in");
        asList(inDir.toFile().listFiles()).forEach(file -> {
            Integer[][] table = parseFile(readFile(file));
            StatisticsCaptor statisticsCaptor = new StatisticsCaptor();
            new SudokuSolver(table, statisticsCaptor).solve();
            System.out.println(file.getName() + ": " + statisticsCaptor.toString());
        });
    }
}