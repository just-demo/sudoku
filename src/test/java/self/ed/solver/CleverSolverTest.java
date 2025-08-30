package self.ed.solver;

import static org.junit.jupiter.api.Assertions.assertEquals;

import static self.ed.util.FileUtils.readClasspathFile;
import static self.ed.util.SudokuUtils.fromString2D;
import static self.ed.util.SudokuUtils.toString2D;

import org.junit.jupiter.api.Test;

import self.ed.visitor.StatisticsCaptor;

public class CleverSolverTest {

  @Test
  public void testSolveSimple() {
    int[][] input = fromString2D(readClasspathFile("input-simple.txt"));
    int[][] output = fromString2D(readClasspathFile("output-simple.txt"));
    CleverSolver resolver = new CleverSolver(input);
    assertEquals(toString2D(output), toString2D(resolver.solve()));
  }

  @Test
  public void testSolveComplex() {
    int[][] input = fromString2D(readClasspathFile("input-complex.txt"));
    int[][] output = fromString2D(readClasspathFile("output-complex.txt"));
    CleverSolver resolver = new CleverSolver(input);
    assertEquals(toString2D(output), toString2D(resolver.solve()));
  }

  @Test
  public void testSolveMostComplex() {
    int[][] input = fromString2D(readClasspathFile("input-most-complex.txt"));
    int[][] output = fromString2D(readClasspathFile("output-most-complex.txt"));
    StatisticsCaptor statistics = new StatisticsCaptor();
    CleverSolver resolver = new CleverSolver(input, statistics);
    System.out.println(statistics);
    assertEquals(toString2D(output), toString2D(resolver.solve()));
  }
}