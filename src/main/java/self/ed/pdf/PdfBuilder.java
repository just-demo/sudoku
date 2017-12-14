/*
 * Copyright (c) 2017 Automation Anywhere. All rights reserved.
 *
 * This software is the proprietary information of Automation Anywhere. You
 * shall use it only in accordance with the terms of the license agreement you
 * entered into with Automation Anywhere.
 */
package self.ed.pdf;

import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

import static com.itextpdf.text.Element.ALIGN_CENTER;
import static com.itextpdf.text.Element.ALIGN_LEFT;
import static com.itextpdf.text.Element.ALIGN_RIGHT;
import static java.nio.file.Files.createDirectories;
import static org.apache.commons.io.FileUtils.readFileToString;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static self.ed.SudokuUtils.parseFile;

public class PdfBuilder {
    //https://developers.itextpdf.com/examples/itext-action-second-edition/chapter-4
    //https://stackoverflow.com/questions/20815388/does-anyone-know-how-to-create-two-parallel-tables-using-itext-in-java
    public static void main(String[] args) throws Exception {
        new PdfBuilder().build();
    }

    public void build() throws Exception {
        Path baseDir = Paths.get("/Users/user/Work/projects/sudoku/data-pdf");
        Path inDir = baseDir.resolve("in");
        Path outDir = baseDir.resolve("out");
        createDirectories(outDir);

        File[] files = inDir.toFile().listFiles();
        for (File file : files) {
            System.out.println(file.getName());
            Integer[][] input = parseFile(readFileToString(file));
            Path outFile = outDir.resolve(file.getName() + ".pdf");
            Files.deleteIfExists(outFile);
            toPdf(input, outFile);
        }
    }

    private void toPdf(Integer[][] matrix, Path file) throws Exception {
        Document document = new Document();
        PdfWriter.getInstance(document, new FileOutputStream(file.toFile()));
        document.open();
        document.add(createTable(matrix, ALIGN_LEFT));
        document.add(createTable(matrix, ALIGN_RIGHT));
        document.close();
    }

    private PdfPTable createTable(Integer[][] matrix, int alignment) {
        int size = matrix.length;

        PdfPTable table = new PdfPTable(9);
        table.setTotalWidth(180);
        table.setLockedWidth(true);
        table.setSpacingAfter(20);
        table.setHorizontalAlignment(alignment);
        table.setBreakPoints();

        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                Integer value = matrix[row][col];
                PdfPCell cell = new PdfPCell(new Phrase(Objects.toString(value, EMPTY)));
                cell.setHorizontalAlignment(ALIGN_CENTER);
                cell.setVerticalAlignment(ALIGN_CENTER);
                cell.setPaddingBottom(6);

                cell.setBorderWidthTop(getBorderWidth(row));
                cell.setBorderWidthRight(getBorderWidth(col + 1));
                cell.setBorderWidthBottom(getBorderWidth(row + 1));
                cell.setBorderWidthLeft(getBorderWidth(col));

                table.addCell(cell);
            }
        }
        return table;
    }

    private float getBorderWidth(int index) {
        int size = 9;
        int blockSize = (int) Math.sqrt(size);

        float thin = 0.25f;
        float thick = 0.75f;
        return index % size == 0 ? 2 * thick : index % blockSize == 0 ? thick : thin;
    }
}
