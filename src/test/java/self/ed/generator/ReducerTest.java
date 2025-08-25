package self.ed.generator;

import static org.junit.jupiter.api.Assertions.assertEquals;

import static self.ed.util.FileUtils.readClasspathFile;
import static self.ed.util.SudokuUtils.toString2D;
import static self.ed.util.SudokuUtils.countOpen;
import static self.ed.util.SudokuUtils.fromString2D;

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
    Integer[][] input = fromString2D(readClasspathFile("input-21.txt"));
    Integer[][] output = reducer.reduce(input);
    assertEquals(toString2D(input), toString2D(output));
  }

  @Test
  void reduce_minusOne() {
    Integer[][] input = fromString2D(readClasspathFile("input-22.txt"));
    Integer[][] output = reducer.reduce(input);
    assertEquals(countOpen(input) - 1, countOpen(output));
  }

  @Test
  void reduce_minusTwo() {
    Integer[][] input = fromString2D(readClasspathFile("input-24.txt"));
    Integer[][] output = reducer.reduce(input);
    assertEquals(countOpen(input) - 2, countOpen(output));
  }
}