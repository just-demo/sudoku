package just.demo.solver;

import static org.junit.jupiter.api.Assertions.assertEquals;

import static just.demo.util.SudokuUtils.fromString2D;
import static just.demo.util.SudokuUtils.toString2D;

import org.junit.jupiter.api.Test;

public class SolverTest {

  @Test
  public void testSolveSimple() {
    String inputData = """
        5 3 . . 7 . . . .
        6 . . 1 9 5 . . .
        . 9 8 . . . . 6 .
        8 . . . 6 . . . 3
        4 . . 8 . 3 . . 1
        7 . . . 2 . . . 6
        . 6 . . . . 2 8 .
        . . . 4 1 9 . . 5
        . . . . 8 . . 7 9
        """;

    String outputData = """
        5 3 4 6 7 8 9 1 2
        6 7 2 1 9 5 3 4 8
        1 9 8 3 4 2 5 6 7
        8 5 9 7 6 1 4 2 3
        4 2 6 8 5 3 7 9 1
        7 1 3 9 2 4 8 5 6
        9 6 1 5 3 7 2 8 4
        2 8 7 4 1 9 6 3 5
        3 4 5 2 8 6 1 7 9
        """;

    int[][] input = fromString2D(inputData);
    int[][] output = fromString2D(outputData);
    Solver resolver = new Solver(input);
    assertEquals(toString2D(output), toString2D(resolver.solve()));
  }

  @Test
  public void testSolveComplex() {
    String inputData = """
        5 7 . . . . . . .
        8 . . . . . 6 . 2
        . . 1 . . 3 . 9 .
        . . . 1 . 6 7 . .
        . . . . . . 5 1 8
        . 3 . . . . . . .
        6 2 . . . 7 . . .
        . . . . . 9 . . .
        . . 5 . 2 . . 3 9
        """;

    String outputData = """
        5 7 6 2 9 8 3 4 1
        8 9 3 4 7 1 6 5 2
        2 4 1 5 6 3 8 9 7
        9 5 8 1 4 6 7 2 3
        4 6 7 9 3 2 5 1 8
        1 3 2 7 8 5 9 6 4
        6 2 9 3 1 7 4 8 5
        3 1 4 8 5 9 2 7 6
        7 8 5 6 2 4 1 3 9
        """;

    int[][] input = fromString2D(inputData);
    int[][] output = fromString2D(outputData);
    Solver resolver = new Solver(input);
    assertEquals(toString2D(output), toString2D(resolver.solve()));
  }
}