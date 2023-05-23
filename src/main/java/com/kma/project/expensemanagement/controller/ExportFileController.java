package com.kma.project.expensemanagement.controller;

import com.kma.project.expensemanagement.dto.response.ResourceDto;
import com.kma.project.expensemanagement.service.ExcelService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/v1/export")
@Api(tags = "Xuất file")
public class ExportFileController {

    @Autowired
    private ExcelService excelService;

    @PutMapping
    public ResponseEntity<Resource> exportData(String fromDate, String toDate, @RequestBody List<Long> walletIds) {
        ResourceDto resourceDTO = excelService.exportData(fromDate, toDate, walletIds);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Content-Disposition",
                "attachment; filename= " + " Transaction.xlsx");

        return ResponseEntity.ok()
                .contentType(resourceDTO.getMediaType())
                .headers(httpHeaders)
                .body(resourceDTO.getResource());
    }
}
