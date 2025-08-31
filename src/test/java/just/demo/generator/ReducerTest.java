package just.demo.generator;

import static org.junit.jupiter.api.Assertions.assertEquals;

import static just.demo.util.SudokuUtils.countOpen;
import static just.demo.util.SudokuUtils.fromString2D;
import static just.demo.util.SudokuUtils.toString2D;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ReducerTest {

  private Reducer reducer;

  @BeforeEach
  void setUp() {
    reducer = new Reducer();
  }

  @Test
  void reduce_alreadyMinimal() {
    String inputData = """
        . . . 8 7 . . . .
        . . . . . . . 5 9
        3 . . 1 . . . . .
        . . . . 4 . 2 1 .
        5 8 . 7 . . . . .
        6 . . . . . . . 4
        . 2 . . 5 . 8 . .
        . . . . 3 4 . 9 .
        . . . . . . . 3 .
        """;

    int[][] input = fromString2D(inputData);
    int[][] output = reducer.reduce(input);
    assertEquals(toString2D(input), toString2D(output));
  }

  @Test
  void reduce_minusOne() {
    String inputData = """
        . 5 4 . 1 3 . . .
        6 . . . . . . . 2
        . . . . . 5 . 7 .
        . 8 . 2 . . 7 . .
        4 . . . . . . . .
        . . 6 . . . 9 . 1
        . . 2 . . . 6 . .
        . . 1 . 4 . . 8 .
        . . . . . 8 5 . .
        """;

    int[][] input = fromString2D(inputData);
    int[][] output = reducer.reduce(input);
    assertEquals(countOpen(input) - 1, countOpen(output));
  }

  @Test
  void reduce_minusTwo() {
    String inputData = """
        2 . 9 . . . 3 . .
        . . . . 2 4 6 . .
        1 . . . 7 . . 5 .
        . 1 . . . . . . .
        3 4 . . . . . 7 .
        . 5 . . . . 9 4 8
        4 . . . . 8 . . .
        . . . 6 . . . . .
        . 2 . 3 5 . . 6 .
        """;

    int[][] input = fromString2D(inputData);
    int[][] output = reducer.reduce(input);
    assertEquals(countOpen(input) - 2, countOpen(output));
  }
}