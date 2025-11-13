package com.example.stockify.reportes.util;

import com.itextpdf.text.*;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Map;

public class PdfGenerator {

    public static ByteArrayInputStream generarReportePDF(Map<String, Object> data) {
        Document document = new Document(PageSize.A4);
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, BaseColor.BLACK);
            Font subTitleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.DARK_GRAY);
            Font textFont = FontFactory.getFont(FontFactory.HELVETICA, 10, BaseColor.BLACK);

            Paragraph title = new Paragraph("REPORTE DE INVENTARIO - STOCKIFY", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);

            document.add(Chunk.NEWLINE);

            document.add(new Paragraph("Periodo: " + data.get("periodo"), textFont));
            document.add(new Paragraph("Fecha de generación: " + data.get("fechaReporte"), textFont));
            document.add(Chunk.NEWLINE);

            document.add(new Paragraph("RESUMEN GENERAL", subTitleFont));
            document.add(new Paragraph("Valor total del inventario: S/ " + data.get("valorInventario"), textFont));
            document.add(new Paragraph("Costo de ventas del periodo: S/ " + data.get("costoVentas"), textFont));
            document.add(Chunk.NEWLINE);

            List<?> productos = (List<?>) data.get("productos");
            List<?> lotes = (List<?>) data.get("lotes");
            List<?> alertas = (List<?>) data.get("alertas");

            document.add(new Paragraph("Cantidad de productos registrados: " + productos.size(), textFont));
            document.add(new Paragraph("Lotes activos: " + lotes.size(), textFont));
            document.add(new Paragraph("Alertas activas: " + alertas.size(), textFont));

            document.add(Chunk.NEWLINE);
            LineSeparator line = new LineSeparator();
            document.add(line);
            document.add(Chunk.NEWLINE);

            document.add(new Paragraph("DETALLE DE PRODUCTOS", subTitleFont));
            PdfPTable table = new PdfPTable(3);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{3, 2, 2});

            PdfPCell cell;

            cell = new PdfPCell(new Phrase("Producto", subTitleFont));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("Stock Actual", subTitleFont));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("Stock Mínimo", subTitleFont));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);

            for (Object p : productos) {
                try {
                    String nombre = (String) p.getClass().getMethod("getNombre").invoke(p);
                    Object stockActual = p.getClass().getMethod("getStockActual").invoke(p);
                    Object stockMinimo = p.getClass().getMethod("getStockMinimo").invoke(p);

                    table.addCell(new Phrase(nombre, textFont));
                    table.addCell(new Phrase(String.valueOf(stockActual), textFont));
                    table.addCell(new Phrase(String.valueOf(stockMinimo), textFont));
                } catch (Exception ignored) { }
            }

            document.add(table);

            document.add(Chunk.NEWLINE);
            document.add(line);
            document.add(Chunk.NEWLINE);

            document.add(new Paragraph("Reporte generado automáticamente por el sistema Stockify.", textFont));

            document.close();

        } catch (DocumentException e) {
            e.printStackTrace();
        }

        return new ByteArrayInputStream(out.toByteArray());
    }
}