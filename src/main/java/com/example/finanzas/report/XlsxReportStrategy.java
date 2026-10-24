package com.example.finanzas.report;

import com.example.finanzas.model.Movimiento;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

public class XlsxReportStrategy implements ReportStrategy {
    @Override
    public String extension() { return ".xlsx"; }

    @Override
    public void generar(List<Movimiento> datos, File destino, String titulo) throws Exception {
        try (XSSFWorkbook wb = new XSSFWorkbook(); FileOutputStream fos = new FileOutputStream(destino)) {
            Sheet sh = wb.createSheet("Movimientos");

            CellStyle head = wb.createCellStyle();
            org.apache.poi.ss.usermodel.Font bold = wb.createFont();
            bold.setBold(true);
            head.setFont(bold);
            head.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            head.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            CellStyle money = wb.createCellStyle();
            money.setDataFormat(wb.createDataFormat().getFormat("$#,##0.00"));
            CellStyle fecha = wb.createCellStyle();
            fecha.setDataFormat(wb.createDataFormat().getFormat("yyyy-mm-dd"));

            Row hr = sh.createRow(0);
            for (int i = 0; i < PdfReportStrategy.ENCABEZADOS.length; i++) {
                Cell c = hr.createCell(i);
                c.setCellValue(PdfReportStrategy.ENCABEZADOS[i]);
                c.setCellStyle(head);
            }
            int r = 1;
            for (Movimiento m : datos) {
                Row row = sh.createRow(r++);
                Cell cf = row.createCell(0);
                cf.setCellValue(m.getFecha());
                cf.setCellStyle(fecha);
                row.createCell(1).setCellValue(m.getTipo());
                Cell cm = row.createCell(2);
                cm.setCellValue(m.getMonto());
                cm.setCellStyle(money);
                row.createCell(3).setCellValue(nvl(m.getConcepto()));
                row.createCell(4).setCellValue(nvl(m.getCategoria()));
                row.createCell(5).setCellValue(nvl(m.getCuenta()));
                row.createCell(6).setCellValue(nvl(m.getEstado()));
                row.createCell(7).setCellValue(nvl(m.getMetodoPago()));
                row.createCell(8).setCellValue(nvl(m.getEtiquetas()));
            }
            int ultima = Math.max(r, 2);
            String[] etiquetas = {"Total ingresos", "Total gastos"};
            String[] tipos = {"INGRESO", "GASTO"};
            for (int i = 0; i < 2; i++) {
                Row row = sh.createRow(r + 1 + i);
                row.createCell(0).setCellValue(etiquetas[i]);
                Cell c = row.createCell(2);
                c.setCellFormula("SUMIF(B2:B" + ultima + ",\"" + tipos[i] + "\",C2:C" + ultima + ")");
                c.setCellStyle(money);
            }
            for (int i = 0; i < 9; i++) sh.autoSizeColumn(i);
            sh.createFreezePane(0, 1);
            wb.write(fos);
        }
    }

    private static String nvl(String s) { return s == null ? "" : s; }
}
