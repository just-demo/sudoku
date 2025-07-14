package self.ed.pdf;

import static com.itextpdf.text.Element.ALIGN_CENTER;
import static com.itextpdf.text.Element.ALIGN_LEFT;
import static com.itextpdf.text.Element.ALIGN_RIGHT;
import static com.itextpdf.text.Font.BOLD;
import static com.itextpdf.text.Font.FontFamily.HELVETICA;
import static com.itextpdf.text.Font.NORMAL;
import static com.itextpdf.text.Rectangle.NO_BORDER;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang3.tuple.Pair;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

public class PdfBuilder {

  private final float pageWidth;
  private final int tableSize;
  private final float cellWidth;
  private final float tableWidth;
  private final int tablesPerLine;
  private final Font cellFont;
  private final float borderNormal;
  private final float borderBold;

  public PdfBuilder(int tablesPerLine) {
    this.tablesPerLine = tablesPerLine;
    // page width best fit for 2 tables per line
    this.pageWidth = 609;
    this.tableSize = 9;
    this.cellWidth = (pageWidth / (tablesPerLine * (tableSize + 1.5f)));
    this.tableWidth = tableSize * cellWidth;
    cellFont = new Font(HELVETICA, cellWidth * 22 / 29 - 2, BOLD);

    this.borderNormal = 0.1f;
    this.borderBold = (float) (Math.log(2) / Math.log(tablesPerLine));
    System.out.println(borderBold);
    System.out.println(cellWidth);
    System.out.println(cellFont.getSize());
  }

  public byte[] build(List<Pair<Integer[][], List<String>>> tables) throws DocumentException {
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    Document document = new Document();
    PdfWriter.getInstance(document, outputStream);
    document.open();

    PdfPTable layout = new PdfPTable(tablesPerLine);
    layout.setTotalWidth(pageWidth);
    layout.setLockedWidth(true);

    List<Pair<Integer[][], List<String>>> tablesSized = new ArrayList<>(tables);
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

  private Element createSummary(List<String> summaryItems) {
    PdfPTable summary = new PdfPTable(2);
    Font summaryFont = new Font(cellFont);
    summaryFont.setSize(cellFont.getSize() * 0.8f);
    summaryFont.setStyle(NORMAL);
    summary.setTotalWidth(tableWidth);
    summary.setLockedWidth(true);
    Map.of(
        summaryItems.get(0), ALIGN_LEFT,
        summaryItems.get(1), ALIGN_RIGHT
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
        cell.setPhrase(new Phrase(Objects.toString(value, ""), cellFont));
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
