package com.kma.project.expensemanagement.service;

import com.kma.project.expensemanagement.dto.response.ResourceDto;

import java.util.List;

public interface ExcelService {

    ResourceDto exportData(String fromDate, String toDate, List<Long> walletIds);

}
