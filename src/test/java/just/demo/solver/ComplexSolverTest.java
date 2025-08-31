package just.demo.solver;

import static org.junit.jupiter.api.Assertions.assertEquals;

import static just.demo.util.FileUtils.readClasspathFile;
import static just.demo.util.SudokuUtils.fromString2D;
import static just.demo.util.SudokuUtils.toString2D;

import org.junit.jupiter.api.Test;

import just.demo.visitor.StatisticsCaptor;

public class ComplexSolverTest {

  @Test
  public void testSolveSimple() {
    int[][] input = fromString2D(readClasspathFile("input-simple.txt"));
    int[][] output = fromString2D(readClasspathFile("output-simple.txt"));
    ComplexSolver resolver = new ComplexSolver(input);
    assertEquals(toString2D(output), toString2D(resolver.solve()));
  }

  @Test
  public void testSolveComplex() {
    int[][] input = fromString2D(readClasspathFile("input-complex.txt"));
    int[][] output = fromString2D(readClasspathFile("output-complex.txt"));
    ComplexSolver resolver = new ComplexSolver(input);
    assertEquals(toString2D(output), toString2D(resolver.solve()));
  }

  @Test
  public void testSolveMostComplex() {
    int[][] input = fromString2D(readClasspathFile("input-most-complex.txt"));
    int[][] output = fromString2D(readClasspathFile("output-most-complex.txt"));
    StatisticsCaptor statistics = new StatisticsCaptor();
    ComplexSolver resolver = new ComplexSolver(input, statistics);
    System.out.println(statistics);
    assertEquals(toString2D(output), toString2D(resolver.solve()));
  }
}