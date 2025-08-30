package self.ed.solver;


import static org.junit.jupiter.api.Assertions.assertEquals;

import static self.ed.util.FileUtils.readClasspathFile;
import static self.ed.util.SudokuUtils.fromString2D;
import static self.ed.util.SudokuUtils.toString2D;

import org.junit.jupiter.api.Test;

public class SimpleSolverTest {

  @Test
  void solve_simple() {
    int[][] input = fromString2D(readClasspathFile("input-simple.txt"));
    int[][] output = fromString2D(readClasspathFile("output-simple.txt"));
    SimpleSolver resolver = new SimpleSolver(input);
    assertEquals(toString2D(output), toString2D(resolver.solve()));
  }

  @Test
  void solve_complex() {
    int[][] input = fromString2D(readClasspathFile("input-complex.txt"));
    int[][] output = fromString2D(readClasspathFile("output-complex.txt"));
    SimpleSolver resolver = new SimpleSolver(input);
    assertEquals(toString2D(output), toString2D(resolver.solve()));
  }

  @Test
  void solve_mostComplex() {
    int[][] input = fromString2D(readClasspathFile("input-most-complex.txt"));
    int[][] output = fromString2D(readClasspathFile("output-most-complex.txt"));
    SimpleSolver resolver = new SimpleSolver(input);
    assertEquals(toString2D(output), toString2D(resolver.solve()));
  }
}