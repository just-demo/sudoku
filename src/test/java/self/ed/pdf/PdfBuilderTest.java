package self.ed.pdf;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import self.ed.SudokuUtils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;

import static java.nio.file.Files.createDirectories;
import static java.util.Arrays.asList;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.io.FileUtils.writeStringToFile;
import static org.apache.commons.lang3.StringUtils.repeat;
import static self.ed.SudokuUtils.parseFile;
import static self.ed.SudokuUtils.readFile;

public class PdfBuilderTest {

    @Test
    public void testBuild() throws Exception {
        Path baseDir = Paths.get("C:\\Users\\pc\\Desktop\\projects\\sudoku\\data-pdf");
        Path inDir = baseDir.resolve("in");
        Path outDir = baseDir.resolve("out");
        Path outFile = outDir.resolve("out.pdf");
        createDirectories(outDir);

        List<Integer[][]> tables = stream(inDir.toFile().listFiles())
                .map(file -> parseFile(readFile(file)))
                .collect(toList());

        Files.write(outFile, new PdfBuilder().build(tables));
    }

    @Test
    public void testMergeFiles() throws Exception {
        Path baseDir = Paths.get("C:\\Users\\pc\\Desktop\\projects\\sudoku\\data");
        Path inDir = baseDir.resolve("23");
        Path outFile = baseDir.resolve("23.txt");

        String delimiter = "\n" + repeat("-", 17) + "\n";

        String merged = stream(inDir.toFile().listFiles())
                .map(SudokuUtils::readFile)
                .collect(joining(delimiter));

        writeStringToFile(outFile.toFile(), merged);
    }

    @Test
    public void testReadAllFiles() throws Exception {
        Path baseDir = Paths.get("C:\\Users\\pc\\Desktop\\projects\\sudoku\\data");
        long totalCount = Stream.of("20.txt", "21.txt", "22.txt", "23.txt")
                .map(baseDir::resolve)
                .map(Path::toFile)
                .map(SudokuUtils::readFile)
                .flatMap(file -> stream(file.split("-+")))
                .map(String::trim)
                .count();
        System.out.println(totalCount);
    }
}