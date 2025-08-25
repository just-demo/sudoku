package self.ed.solver;

import static org.junit.jupiter.api.Assertions.assertEquals;

import static self.ed.util.FileUtils.readClasspathFile;
import static self.ed.util.SudokuUtils.toString2D;
import static self.ed.util.SudokuUtils.fromString2D;

import org.junit.jupiter.api.Test;

import self.ed.visitor.StatisticsCaptor;

public class CleverSolverTest {

  @Test
  public void testSolveSimple() {
    Integer[][] input = fromString2D(readClasspathFile("input-simple.txt"));
    Integer[][] output = fromString2D(readClasspathFile("output-simple.txt"));
    CleverSolver resolver = new CleverSolver(input);
    assertEquals(toString2D(output), toString2D(resolver.solve()));
  }

  @Test
  public void testSolveComplex() {
    Integer[][] input = fromString2D(readClasspathFile("input-complex.txt"));
    Integer[][] output = fromString2D(readClasspathFile("output-complex.txt"));
    CleverSolver resolver = new CleverSolver(input);
    assertEquals(toString2D(output), toString2D(resolver.solve()));
  }

  @Test
  public void testSolveMostComplex() {
    Integer[][] input = fromString2D(readClasspathFile("input-most-complex.txt"));
    Integer[][] output = fromString2D(readClasspathFile("output-most-complex.txt"));
    StatisticsCaptor statistics = new StatisticsCaptor();
    CleverSolver resolver = new CleverSolver(input, statistics);
    System.out.println(statistics);
    assertEquals(toString2D(output), toString2D(resolver.solve()));
  }
}