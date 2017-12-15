package self.ed;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Arrays;
import java.util.Objects;

import static java.lang.ClassLoader.getSystemResourceAsStream;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.joining;
import static org.apache.commons.io.FileUtils.readFileToString;

public class SudokuUtils {
    private static final String EMPTY_VALUE = ".";

    public static String asString(Integer[][] matrix) {
        return stream(matrix)
                .map(line -> stream(line)
                        .map(cell -> Objects.toString(cell, EMPTY_VALUE))
                        .collect(joining(" ")))
                .collect(joining("\n"));
    }

    public static String asFlatString(Integer[][] matrix) {
        return stream(matrix)
                .map(line -> stream(line)
                        .map(cell -> Objects.toString(cell, "0"))
                        .collect(joining()))
                .collect(joining());
    }

    public static long countOpen(Integer[][] matrix) {
        return stream(matrix).mapToLong(line -> stream(line).filter(Objects::nonNull).count()).sum();
    }

    public static String readFile(String fileName) {
        try {
            return IOUtils.toString(getSystemResourceAsStream(fileName));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static String readFile(File file) {
        try {
            return readFileToString(file);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static Integer[][] parseFile(String file) {
        return stream(file.split("\n"))
                .map(String::trim)
                .map(SudokuUtils::parseLine)
                .toArray(Integer[][]::new);
    }

    public static Integer[][] copy(Integer[][] source) {
        Integer[][] target = new Integer[source.length][];
        for (int row = 0; row < source.length; row++) {
            target[row] = Arrays.copyOf(source[row], source[row].length);
        }
        return target;
    }

    private static Integer[] parseLine(String line) {
        return stream(line.split(" "))
                .map(String::trim)
                .map(SudokuUtils::parseCell)
                .toArray(Integer[]::new);
    }

    private static Integer parseCell(String cell) {
        return EMPTY_VALUE.equals(cell) ? null : Integer.valueOf(cell);
    }

    public static Integer[][] parseFlatString(String flat) {
        String[] values = flat.split("");
        int size = (int)Math.sqrt(values.length);
        Integer[][] matrix = new Integer[size][size];
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                String value = values[row * size + col];
                matrix[row][col] = "0".equals(value) ? null : Integer.valueOf(value);
            }
        }
        return matrix;
    }
}
