package self.ed.solver;

import org.junit.Test;
import self.ed.visitor.StatisticsCaptor;

import static org.junit.Assert.assertEquals;
import static self.ed.SudokuUtils.*;

public class CleverSolverTest {
    @Test
    public void testSolveSimple() {
        Integer[][] input = parseFile(readFile("input-simple.txt"));
        Integer[][] output = parseFile(readFile("output-simple.txt"));
        CleverSolver resolver = new CleverSolver(input);
        assertEquals(asString(output), asString(resolver.solve()));
    }

    @Test
    public void testSolveComplex() {
        Integer[][] input = parseFile(readFile("input-complex.txt"));
        Integer[][] output = parseFile(readFile("output-complex.txt"));
        CleverSolver resolver = new CleverSolver(input);
        assertEquals(asString(output), asString(resolver.solve()));
    }

    @Test
    public void testSolveMostComplex() {
        Integer[][] input = parseFile(readFile("input-most-complex.txt"));
        Integer[][] output = parseFile(readFile("output-most-complex.txt"));
        StatisticsCaptor statistics = new StatisticsCaptor();
        CleverSolver resolver = new CleverSolver(input, statistics);
        System.out.println(statistics);
        assertEquals(asString(output), asString(resolver.solve()));
    }
}