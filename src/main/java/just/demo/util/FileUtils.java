package just.demo.util;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.Files.createDirectories;
import static java.nio.file.Files.readString;
import static java.nio.file.Files.writeString;
import static java.nio.file.StandardOpenOption.APPEND;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.util.Arrays.stream;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

import lombok.experimental.UtilityClass;

@UtilityClass
public class FileUtils {

  public static String readFile(File file) {
    return readFile(file.toPath());
  }

  public static String readFile(Path file) {
    try {
      return readString(file, UTF_8);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  public static void writeFile(File file, String data) {
    writeFile(file.toPath(), data);
  }

  public static void writeFile(Path file, String data) {
    try {
      createDirectories(file.getParent());
      writeString(file, data, UTF_8);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  public static void writeFile(Path file, byte[] bytes) {
    try {
      Files.write(file, bytes);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  public static void appendFile(File file, String data) {
    try {
      createDirectories(file.toPath().getParent());
      writeString(file.toPath(), data, UTF_8, CREATE, APPEND);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  public static Stream<File> streamFiles(File dir) {
    return dir.isDirectory() ? stream(dir.listFiles()).flatMap(FileUtils::streamFiles) : Stream.of(dir);
  }

  public static Stream<File> streamFiles(Path dir) {
    return streamFiles(dir.toFile());
  }
}
