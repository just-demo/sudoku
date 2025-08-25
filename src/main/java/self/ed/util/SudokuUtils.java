package self.ed.util;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.joining;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Objects;

public class SudokuUtils {

  private static final String EMPTY_VALUE = ".";

  public static String toString1D(Integer[][] matrix) {
    return stream(matrix)
        .map(line -> stream(line)
            .map(cell -> Objects.toString(cell, EMPTY_VALUE))
            .collect(joining()))
        .collect(joining());
  }

  public static String toString2D(Integer[][] matrix) {
    return stream(matrix)
        .map(line -> stream(line)
            .map(cell -> Objects.toString(cell, EMPTY_VALUE))
            .collect(joining(" ")))
        .collect(joining("\n"));
  }

  public static Integer[][] fromString1D(String flat) {
    String[] values = flat.split("");
    int size = (int) Math.sqrt(values.length);
    Integer[][] matrix = new Integer[size][size];
    for (int row = 0; row < size; row++) {
      for (int col = 0; col < size; col++) {
        String value = values[row * size + col];
        matrix[row][col] = EMPTY_VALUE.equals(value) ? null : Integer.valueOf(value);
      }
    }
    return matrix;
  }

  public static Integer[][] fromString2D(String file) {
    return stream(file.split("\n"))
        .map(String::trim)
        .map(SudokuUtils::parseLine2D)
        .toArray(Integer[][]::new);
  }

  public static long countOpen(Integer[][] matrix) {
    return stream(matrix).mapToLong(line -> stream(line).filter(Objects::nonNull).count()).sum();
  }

  public static Integer[][] copy(Integer[][] source) {
    Integer[][] target = new Integer[source.length][];
    for (int row = 0; row < source.length; row++) {
      target[row] = Arrays.copyOf(source[row], source[row].length);
    }
    return target;
  }

  private static Integer[] parseLine2D(String line) {
    return stream(line.split(" "))
        .map(String::trim)
        .map(SudokuUtils::parseCell)
        .toArray(Integer[]::new);
  }

  private static Integer parseCell(String cell) {
    return EMPTY_VALUE.equals(cell) ? null : Integer.valueOf(cell);
  }

  public static String getCurrentTime() {
    return new SimpleDateFormat("yyyyMMdd-HHmmss").format(new Date());
  }

  public static String join(String delimiter, Object... items) {
    return stream(items).map(Object::toString).collect(joining(delimiter));
  }
}
