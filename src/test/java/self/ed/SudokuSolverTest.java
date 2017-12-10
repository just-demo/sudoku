package self.ed;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static self.ed.SudokuUtils.*;

public class SudokuSolverTest {
    @Test
    public void testSolveSimple() {
        Integer[][] input = parseFile(readFile("input-simple.txt"));
        Integer[][] output = parseFile(readFile("output-simple.txt"));
        SudokuSolver resolver = new SudokuSolver(input);
        assertEquals(asString(output), asString(resolver.solve()));
    }

    @Test
    public void testSolveComplex() {
        Integer[][] input = parseFile(readFile("input-complex.txt"));
        Integer[][] output = parseFile(readFile("output-complex.txt"));
        SudokuSolver resolver = new SudokuSolver(input);
        assertEquals(asString(output), asString(resolver.solve()));
    }

    @Test
    public void testSolveMostComplex() {
        Integer[][] input = parseFile(readFile("input-most-complex.txt"));
        Integer[][] output = parseFile(readFile("output-most-complex.txt"));
        SudokuSolver resolver = new SudokuSolver(input);
        assertEquals(asString(output), asString(resolver.solve()));
    }
}