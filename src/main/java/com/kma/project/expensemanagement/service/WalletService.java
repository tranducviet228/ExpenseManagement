package com.kma.project.expensemanagement.service;

import com.kma.project.expensemanagement.dto.request.WalletInputDto;
import com.kma.project.expensemanagement.dto.request.WalletTransferMoneyDto;
import com.kma.project.expensemanagement.dto.response.DataResponse;
import com.kma.project.expensemanagement.dto.response.PageResponse;
import com.kma.project.expensemanagement.dto.response.WalletInformationOutputDto;
import com.kma.project.expensemanagement.dto.response.WalletOutputDto;

public interface WalletService {

    WalletOutputDto add(WalletInputDto inputDto);

    WalletOutputDto update(Long id, WalletInputDto inputDto);

    void delete(Long id);

    DataResponse<WalletOutputDto> getDetail(Long id);

    PageResponse<WalletOutputDto> getAllWallet(Integer page, Integer size, String sort);

    WalletInformationOutputDto getInfoAllWallet();

    void transferMoney(Long fromWalletId, WalletTransferMoneyDto walletTransferMoneyDto);
}
