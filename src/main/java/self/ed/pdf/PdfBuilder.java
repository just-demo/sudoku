/*
 * Copyright (c) 2017 Automation Anywhere. All rights reserved.
 *
 * This software is the proprietary information of Automation Anywhere. You
 * shall use it only in accordance with the terms of the license agreement you
 * entered into with Automation Anywhere.
 */
package self.ed.pdf;

import com.google.common.collect.ImmutableMap;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.apache.commons.lang3.tuple.Pair;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.itextpdf.text.Element.*;
import static com.itextpdf.text.Font.BOLD;
import static com.itextpdf.text.Font.FontFamily.HELVETICA;
import static com.itextpdf.text.Font.NORMAL;
import static com.itextpdf.text.Rectangle.NO_BORDER;
import static org.apache.commons.lang3.StringUtils.EMPTY;

public class PdfBuilder {
    private float pageWidth;
    private int tableSize;
    private float cellWidth;
    private float tableWidth;
    private int tablesPerLine;
    private Font cellFont;
    private float borderNormal;
    private float borderBold;

    public PdfBuilder(int tablesPerLine) {
        this.tablesPerLine = tablesPerLine;
        // page width best fit for 2 tables per line
        this.pageWidth = 567;
        this.tableSize = 9;
        this.cellWidth = (pageWidth / (tablesPerLine * (tableSize + 1.5f)));
        this.tableWidth = tableSize * cellWidth;
        cellFont = new Font(HELVETICA, cellWidth * 20 / 27 - 2, BOLD);

        this.borderNormal = 0.1f;
        this.borderBold = (float) (Math.log(2) / Math.log(tablesPerLine));
        System.out.println(borderBold);
        System.out.println(cellWidth);
        System.out.println(cellFont.getSize());
    }

    public byte[] build(List<Pair<Integer[][], Map<String, String>>> tables) throws DocumentException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Document document = new Document();
        PdfWriter.getInstance(document, outputStream);
        document.open();

        PdfPTable layout = new PdfPTable(tablesPerLine);
        layout.setTotalWidth(pageWidth);
        layout.setLockedWidth(true);

        List<Pair<Integer[][], Map<String, String>>> tablesSized = new ArrayList<>(tables);
        while (tablesSized.size() % tablesPerLine != 0) {
            tablesSized.add(null);
        }

        tablesSized.forEach(table -> {
            PdfPCell layoutCell = new PdfPCell();
            if (table != null) {
                layoutCell.addElement(createSummary(table.getValue()));
                layoutCell.addElement(createTable(table.getKey()));
            }
            layoutCell.setBorder(NO_BORDER);
            layoutCell.setPaddingBottom(cellWidth * 1.5f);
            layout.addCell(layoutCell);
        });

        document.add(layout);
        document.close();
        return outputStream.toByteArray();
    }

    private Element createSummary(Map<String, String> summaryItems) {
        PdfPTable summary = new PdfPTable(2);
        Font summaryFont = new Font(cellFont);
        summaryFont.setSize(cellFont.getSize() * 0.8f);
        summaryFont.setStyle(NORMAL);
        summary.setTotalWidth(tableWidth);
        summary.setLockedWidth(true);
        ImmutableMap.of(
                "# " + summaryItems.get("id"), ALIGN_LEFT,
                summaryItems.get("complexity"), ALIGN_RIGHT
        ).forEach((text, alignment) -> {
            PdfPCell cell = new PdfPCell();
            cell.setPhrase(new Phrase(text, summaryFont));
            cell.setHorizontalAlignment(alignment);
            cell.setBorder(NO_BORDER);
            cell.setPaddingBottom(cellWidth * 0.5f);
            summary.addCell(cell);
        });
        return summary;
    }

    private PdfPTable createTable(Integer[][] matrix) {
        PdfPTable table = new PdfPTable(tableSize);
        table.setTotalWidth(tableWidth);
        table.setLockedWidth(true);

        for (int row = 0; row < tableSize; row++) {
            for (int col = 0; col < tableSize; col++) {
                Integer value = matrix[row][col];
                PdfPCell cell = new PdfPCell();
                cell.setPhrase(new Phrase(Objects.toString(value, EMPTY), cellFont));
                cell.setHorizontalAlignment(ALIGN_CENTER);
                cell.setVerticalAlignment(ALIGN_CENTER);
                cell.setFixedHeight(cellWidth);

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
        if (index % tableSize == 0) {
            return 2 * borderBold;
        }

        if (index % Math.sqrt(tableSize) == 0) {
            return borderBold;
        }

        return borderNormal;
    }
}
