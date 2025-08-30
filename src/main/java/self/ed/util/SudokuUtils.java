package self.ed.util;

import static java.lang.Integer.parseInt;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.joining;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

public class SudokuUtils {

  private static final String EMPTY_AS_STRING = ".";

  public static String toString1D(int[][] matrix) {
    return stream(matrix)
        .map(line -> stream(line)
            .mapToObj(SudokuUtils::cellToString)
            .collect(joining()))
        .collect(joining());
  }

  public static String toString2D(int[][] matrix) {
    return stream(matrix)
        .map(line -> stream(line)
            .mapToObj(SudokuUtils::cellToString)
            .collect(joining(" ")))
        .collect(joining("\n"));
  }

  public static int[][] fromString1D(String flat) {
    String[] values = flat.split("");
    int size = (int) Math.sqrt(values.length);
    int[][] matrix = new int[size][size];
    for (int row = 0; row < size; row++) {
      for (int col = 0; col < size; col++) {
        String value = values[row * size + col];
        matrix[row][col] = cellFromString(value);
      }
    }
    return matrix;
  }

  public static int[][] fromString2D(String file) {
    return stream(file.split("\n"))
        .map(String::trim)
        .map(SudokuUtils::parseLine2D)
        .toArray(int[][]::new);
  }

  public static long countOpen(int[][] matrix) {
    return stream(matrix).mapToLong(line -> stream(line).filter(v -> v != 0).count()).sum();
  }

  public static int[][] copy(int[][] source) {
    int[][] target = new int[source.length][];
    for (int row = 0; row < source.length; row++) {
      target[row] = Arrays.copyOf(source[row], source[row].length);
    }
    return target;
  }

  private static int[] parseLine2D(String line) {
    return stream(line.split(" "))
        .map(String::trim)
        .mapToInt(SudokuUtils::cellFromString)
        .toArray();
  }

  private static int cellFromString(String cell) {
    return EMPTY_AS_STRING.equals(cell) ? 0 : parseInt(cell);
  }

  private static String cellToString(int cell) {
    return cell == 0 ? EMPTY_AS_STRING : String.valueOf(cell);
  }

  public static String getCurrentTime() {
    return new SimpleDateFormat("yyyyMMdd-HHmmss").format(new Date());
  }

  public static String join(String delimiter, Object... items) {
    return stream(items).map(Object::toString).collect(joining(delimiter));
  }
}
