/*
 * Copyright (c) 2017 Automation Anywhere. All rights reserved.
 *
 * This software is the proprietary information of Automation Anywhere. You
 * shall use it only in accordance with the terms of the license agreement you
 * entered into with Automation Anywhere.
 */
package self.ed.pdf;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

import static com.itextpdf.text.Element.ALIGN_CENTER;
import static com.itextpdf.text.Font.BOLD;
import static com.itextpdf.text.Font.FontFamily.HELVETICA;
import static org.apache.commons.lang3.StringUtils.EMPTY;

public class PdfBuilder {
    private static final int TABLE_SIZE = 9;
    private static final int CELL_WIDTH = 27;
    private static final int TABLE_WIDTH = TABLE_SIZE * CELL_WIDTH;
    private static final int TABLES_PER_LINE = 2;
    private static final Font CELL_FONT = new Font(HELVETICA, 18, BOLD);
    private static final float BORDER_NORMAL = 0.1f;
    private static final float BORDER_BOLD = 1.0f;

    public static void main(String[] args) {
        int TABLES_PER_LINE = 3;
        int size = 27;
        System.out.println(IntStream.range(0, TABLES_PER_LINE - size % TABLES_PER_LINE).count());
    }
    public byte[] build(List<Integer[][]> tables) throws DocumentException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Document document = new Document();
        PdfWriter.getInstance(document, outputStream);
        document.open();

        PdfPTable layout = new PdfPTable(TABLES_PER_LINE);
        layout.setTotalWidth(TABLES_PER_LINE * TABLE_WIDTH + 3 * CELL_WIDTH);
        layout.setLockedWidth(true);

        tables = new ArrayList<>(tables);
        while (tables.size() % TABLES_PER_LINE != 0) {
            tables.add(null);
        }

        tables.forEach(table -> {
            PdfPCell layoutCell = new PdfPCell();
            if (table != null) {
                layoutCell.addElement(createTable(table));
            }
            layoutCell.setBorder(PdfPCell.NO_BORDER);
            layoutCell.setPaddingBottom(3 * CELL_WIDTH / 2);
            layout.addCell(layoutCell);
        });

        document.add(layout);
        document.close();
        return outputStream.toByteArray();
    }

    private PdfPTable createTable(Integer[][] matrix) {
        PdfPTable table = new PdfPTable(TABLE_SIZE);
        table.setTotalWidth(TABLE_WIDTH);
        table.setLockedWidth(true);

        for (int row = 0; row < TABLE_SIZE; row++) {
            for (int col = 0; col < TABLE_SIZE; col++) {
                Integer value = matrix[row][col];
                PdfPCell cell = new PdfPCell();
                cell.setPhrase(new Phrase(Objects.toString(value, EMPTY), CELL_FONT));
                cell.setHorizontalAlignment(ALIGN_CENTER);
                cell.setVerticalAlignment(ALIGN_CENTER);
                cell.setFixedHeight(CELL_WIDTH);

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
        if (index % TABLE_SIZE == 0) {
            return 2 * BORDER_BOLD;
        }

        if (index % Math.sqrt(TABLE_SIZE) == 0) {
            return BORDER_BOLD;
        }

        return BORDER_NORMAL;
    }
}
