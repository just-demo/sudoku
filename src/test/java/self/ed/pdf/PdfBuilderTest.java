package self.ed.pdf;

import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static java.nio.file.Files.createDirectories;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;
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
}