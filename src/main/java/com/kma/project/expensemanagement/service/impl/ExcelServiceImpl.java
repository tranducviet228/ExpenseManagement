package com.kma.project.expensemanagement.service.impl;

import com.kma.project.expensemanagement.dto.response.ResourceDto;
import com.kma.project.expensemanagement.entity.TransactionEntity;
import com.kma.project.expensemanagement.repository.TransactionRepository;
import com.kma.project.expensemanagement.service.ExcelService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Service
public class ExcelServiceImpl implements ExcelService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Override
    public ResourceDto exportData() {
        List<TransactionEntity> tranList = transactionRepository.findAll();
        Resource resource = prepareExcel(tranList);
        return ResourceDto.builder().resource(resource).
                mediaType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")).build();
    }


    private Resource prepareExcel(List<TransactionEntity> tranList) {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("transactions");

        prepareHeaders(workbook, sheet, "Id", "Category", "Amount", "Description", "Type", "Date");
        populateUserData(workbook, sheet, tranList);

        try (ByteArrayOutputStream byteArrayOutputStream
                     = new ByteArrayOutputStream()) {

            workbook.write(byteArrayOutputStream);
            return new
                    ByteArrayResource
                    (byteArrayOutputStream.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException
                    ("Error while generating excel.");
        }
    }

    private void populateUserData(Workbook workbook, Sheet sheet,
                                  List<TransactionEntity> tranList) {

        int rowNo = 1;
        Font font = workbook.createFont();
        font.setFontName("Arial");

        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setFont(font);

        for (TransactionEntity tran : tranList) {
            int columnNo = 0;
            Row row = sheet.createRow(rowNo);
            populateCell(sheet, row, columnNo++,
                    String.valueOf(tran.getId()), cellStyle);
            populateCell(sheet, row, columnNo++,
                    tran.getCategoryName(), cellStyle);
            populateCell(sheet, row, columnNo++,
                    tran.getAmount().toString(), cellStyle);
            populateCell(sheet, row, columnNo++,
                    tran.getDescription(), cellStyle);
            populateCell(sheet, row, columnNo++,
                    tran.getTransactionType().toString(), cellStyle);
            populateCell(sheet, row, columnNo++,
                    tran.getAriseDate().toString(), cellStyle);
            rowNo++;
        }
    }

    private void populateCell(Sheet sheet, Row row, int columnNo,
                              String value, CellStyle cellStyle) {

        Cell cell = row.createCell(columnNo);
        cell.setCellStyle(cellStyle);
        cell.setCellValue(value);
        sheet.autoSizeColumn(columnNo);
    }

    private void prepareHeaders(Workbook workbook,
                                Sheet sheet, String... headers) {

        Row headerRow = sheet.createRow(0);
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontName("Arial");

        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setFont(font);

        int columnNo = 0;
        for (String header : headers) {
            Cell headerCell = headerRow.createCell(columnNo++);
            headerCell.setCellValue(header);
            headerCell.setCellStyle(cellStyle);
        }
    }
}

