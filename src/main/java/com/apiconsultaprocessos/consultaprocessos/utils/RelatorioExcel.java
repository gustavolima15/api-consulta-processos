package com.apiconsultaprocessos.consultaprocessos.utils;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class RelatorioExcel {
    public static void gerarRelatorio(List<Map<String, String>> dados, String nomeArquivo) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Relatório");

        int rowNum = 0;
        Row headerRow = sheet.createRow(rowNum++);
        
        // Adiciona cabeçalhos
        int cellNum = 0;
        for (String key : dados.get(0).keySet()) {
            headerRow.createCell(cellNum++).setCellValue(key);
        }

        // Adiciona os dados
        for (Map<String, String> rowData : dados) {
            Row row = sheet.createRow(rowNum++);
            cellNum = 0;
            for (String value : rowData.values()) {
                row.createCell(cellNum++).setCellValue(value);
            }
        }

        try (FileOutputStream fileOut = new FileOutputStream(nomeArquivo)) {
            workbook.write(fileOut);
        }
        workbook.close();
        System.out.println("Relatório salvo como " + nomeArquivo);
    }
}