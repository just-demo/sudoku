package self.ed;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import static java.lang.ClassLoader.getSystemResourceAsStream;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.io.FileUtils.readFileToString;
import static org.apache.commons.io.FileUtils.writeStringToFile;

public class SudokuUtils {
    private static final String EMPTY_VALUE = ".";

    public static String asString(Integer[][] matrix) {
        return stream(matrix)
                .map(line -> stream(line)
                        .map(cell -> Objects.toString(cell, EMPTY_VALUE))
                        .collect(joining(" ")))
                .collect(joining("\n"));
    }

    public static String asSimpleString(Integer[][] matrix) {
        return stream(matrix)
                .map(line -> stream(line)
                        .map(cell -> Objects.toString(cell, EMPTY_VALUE))
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

    public static void writeFile(File file, String data) {
        try {
            writeStringToFile(file, data);
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

    public static Integer[][] parseSimpleString(String flat) {
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

    public static List<File> listFiles(File dir) {
        return streamFiles(dir).collect(toList());
    }

    public static Stream<File> streamFiles(File dir) {
        return dir.isDirectory() ? stream(dir.listFiles()).flatMap(SudokuUtils::streamFiles) : Stream.of(dir);
    }

    public static String getCurrentTime() {
        return new SimpleDateFormat("yyyyMMdd-HHmmss").format(new Date());
    }

    public static String join(String delimiter, Object... items) {
        return stream(items).map(Object::toString).collect(joining(delimiter));
    }
}
