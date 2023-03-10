package com.kma.project.expensemanagement.service;

import com.kma.project.expensemanagement.dto.request.WalletInputDto;
import com.kma.project.expensemanagement.dto.response.WalletOutputDto;

import java.util.List;

public interface WalletService {

    WalletOutputDto add(WalletInputDto inputDto);

    WalletOutputDto update(Long id, WalletInputDto inputDto);

    void delete(Long id);

    WalletOutputDto getDetail(Long id);

    List<WalletOutputDto> getAllWallet();
}
