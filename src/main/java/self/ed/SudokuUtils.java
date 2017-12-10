package self.ed;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Arrays;
import java.util.Objects;

import static java.lang.ClassLoader.getSystemResourceAsStream;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.joining;

public class SudokuUtils {
    private static final String EMPTY_VALUE = ".";

    public static String asString(Integer[][] values) {
        return stream(values)
                .map(line -> stream(line)
                        .map(cell -> Objects.toString(cell, EMPTY_VALUE))
                        .collect(joining(" ")))
                .collect(joining("\n"));
    }

    public static String readFile(String fileName) {
        try {
            return IOUtils.toString(getSystemResourceAsStream(fileName));
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
}
