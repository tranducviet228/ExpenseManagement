package com.kma.project.expensemanagement.service.impl;

import com.kma.project.expensemanagement.dto.response.ResourceDto;
import com.kma.project.expensemanagement.dto.response.TransactionExcelOutputDto;
import com.kma.project.expensemanagement.entity.TransactionEntity;
import com.kma.project.expensemanagement.enums.TransactionType;
import com.kma.project.expensemanagement.repository.TransactionRepository;
import com.kma.project.expensemanagement.repository.WalletRepository;
import com.kma.project.expensemanagement.security.jwt.JwtUtils;
import com.kma.project.expensemanagement.service.ExcelService;
import com.kma.project.expensemanagement.utils.EnumUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class ExcelServiceImpl implements ExcelService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    JwtUtils jwtUtils;
    @Autowired
    private WalletRepository walletRepository;

    @Override
    public ResourceDto exportData(String fromDate, String toDate, List<Long> walletIds) {

        LocalDate fromDates = fromDate == null ? LocalDate.now() : LocalDate.parse(fromDate);
        LocalDate toDates = toDate == null ? LocalDate.now() : LocalDate.parse(toDate);

        LocalDateTime firstDate = fromDates.atTime(0, 0, 0);
        LocalDateTime lastDate = toDates.atTime(23, 59, 59);

        walletIds = walletIds.isEmpty() ? walletRepository.getAllWalletId(jwtUtils.getCurrentUserId()) : walletIds;

//        List<TransactionEntity> tranList = transactionRepository
//                .findAllTransactionByAriseDate(firstDate, lastDate);

        List<TransactionEntity> tranList = transactionRepository
                .findAllTransactionByAriseDate(firstDate, lastDate, walletIds, jwtUtils.getCurrentUserId());

        List<TransactionExcelOutputDto> tranOutput = new ArrayList<>();
        tranList.forEach(transactionEntity -> {
            TransactionExcelOutputDto outputDto = new TransactionExcelOutputDto();
            outputDto.setAmount(transactionEntity.getAmount());
            outputDto.setDate(transactionEntity.getAriseDate().toLocalDate());
            outputDto.setTime(transactionEntity.getAriseDate().toLocalTime());
            outputDto.setDescription(transactionEntity.getDescription());
            outputDto.setTransactionTypeName(convertTransactionType(transactionEntity.getTransactionType()));
            outputDto.setTransactionType(transactionEntity.getTransactionType().name());
            outputDto.setCategoryName(transactionEntity.getCategory().getName());
            outputDto.setWalletName(transactionEntity.getWallet().getName());
            tranOutput.add(outputDto);
        });
        Resource resource = prepareExcel(tranOutput, fromDate, toDate);
        return ResourceDto.builder().resource(resource).
                mediaType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")).build();
    }

    public String convertTransactionType(TransactionType transactionType) {
        if (transactionType.name().equals(EnumUtils.EXPENSE)) {
            return "Thu";
        } else {
            return "Chi";
        }
    }


    private Resource prepareExcel(List<TransactionExcelOutputDto> tranList, String fromDate, String toDate) {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("transactions");

        prepareHeaders(workbook, sheet, "STT", "Danh mục", "Số tiền", "Đơn vị", "Mô tả", "Loại giao dịch",
                "Ngày tạo", "Giờ tạo", "Tên tài khoản");
        populateUserData(workbook, sheet, tranList, fromDate, toDate);

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
                                  List<TransactionExcelOutputDto> tranList, String fromDate, String toDate) {
        DecimalFormat decimalFormat = new DecimalFormat("###,###,###");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        Font font = workbook.createFont();
        font.setFontName("Arial");
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setFont(font);

        CellStyle cellStyle1 = workbook.createCellStyle();
        Font font1 = workbook.createFont();
        font1.setBold(true);
        font1.setFontHeightInPoints((short) 26); // Set font size
        cellStyle1.setFont(font1);

        BigDecimal expenseTotal = BigDecimal.ZERO;
        BigDecimal incomeTotal = BigDecimal.ZERO;

        for (TransactionExcelOutputDto transactionExcelOutputDto : tranList) {
            if (EnumUtils.EXPENSE.equals(transactionExcelOutputDto.getTransactionType())) {
                expenseTotal = expenseTotal.add(transactionExcelOutputDto.getAmount());
            } else {
                incomeTotal = incomeTotal.add(transactionExcelOutputDto.getAmount());
            }
        }

        populateCell(sheet, sheet.createRow(1), 5, "BẢNG THỐNG KÊ CHI TIÊU", cellStyle1);
        populateCell(sheet, sheet.createRow(2), 5, "Từ ngày " + fromDate + " đến ngày " + toDate, cellStyle);


        Row row4 = sheet.createRow(4);
        Row row5 = sheet.createRow(5);
        populateCell(sheet, row4, 3, "Tổng thu", cellStyle);
        populateCell(sheet, row4, 4, decimalFormat.format(incomeTotal), cellStyle);
        populateCell(sheet, row5, 3, "Tổng chi", cellStyle);
        populateCell(sheet, row5, 4, decimalFormat.format(expenseTotal), cellStyle);

        int rowNo = 8;

        int i = 0;
        for (TransactionExcelOutputDto tran : tranList) {
            int columnNo = 0;
            Row row = sheet.createRow(rowNo);
            populateCell(sheet, row, columnNo++, String.valueOf(i++), cellStyle);
            populateCell(sheet, row, columnNo++, tran.getCategoryName(), cellStyle);
            populateCell(sheet, row, columnNo++, decimalFormat.format(tran.getAmount()), cellStyle);
            populateCell(sheet, row, columnNo++, "VND", cellStyle);
            populateCell(sheet, row, columnNo++, tran.getDescription(), cellStyle);
            populateCell(sheet, row, columnNo++, tran.getTransactionTypeName(), cellStyle);
            populateCell(sheet, row, columnNo++, tran.getDate().toString(), cellStyle);
            populateCell(sheet, row, columnNo++, formatter.format(tran.getTime()), cellStyle);
            populateCell(sheet, row, columnNo++, tran.getWalletName(), cellStyle);
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

        Row headerRow = sheet.createRow(7);
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontName("Arial");

        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setFont(font);
        headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);


        int columnNo = 0;
        for (String header : headers) {
            Cell headerCell = headerRow.createCell(columnNo++);
            headerCell.setCellValue(header);
            headerCell.setCellStyle(headerStyle);
        }
    }
}

