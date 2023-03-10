package com.kma.project.expensemanagement.service.impl;

import com.kma.project.expensemanagement.dto.request.WalletInputDto;
import com.kma.project.expensemanagement.dto.response.WalletOutputDto;
import com.kma.project.expensemanagement.entity.WalletEntity;
import com.kma.project.expensemanagement.exception.AppException;
import com.kma.project.expensemanagement.mapper.WalletMapper;
import com.kma.project.expensemanagement.repository.WalletRepository;
import com.kma.project.expensemanagement.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class WalletServiceImpl implements WalletService {

    @Autowired
    WalletRepository repository;

    @Autowired
    WalletMapper mapper;

    @Override
    public WalletOutputDto add(WalletInputDto inputDto) {
        WalletEntity walletEntity = mapper.convertToEntity(inputDto);
        repository.save(walletEntity);
        return mapper.convertToDto(walletEntity);
    }

    @Override
    public WalletOutputDto update(Long id, WalletInputDto inputDto) {
        WalletEntity walletEntity = repository.findById(id)
                .orElseThrow(() -> AppException.builder().errorCodes(Collections.singletonList("error.wallet-not-found")).build());
        mapper.update(inputDto, walletEntity);
        repository.save(walletEntity);
        return mapper.convertToDto(walletEntity);
    }

    @Override
    public void delete(Long id) {
        WalletEntity walletEntity = repository.findById(id)
                .orElseThrow(() -> AppException.builder().errorCodes(Collections.singletonList("error.wallet-not-found")).build());
        repository.delete(walletEntity);
    }

    @Override
    public WalletOutputDto getDetail(Long id) {
        WalletEntity walletEntity = repository.findById(id)
                .orElseThrow(() -> AppException.builder().errorCodes(Collections.singletonList("error.wallet-not-found")).build());
        return mapper.convertToDto(walletEntity);
    }

    @Override
    public List<WalletOutputDto> getAllWallet() {
        List<WalletEntity> listWallet = repository.findAll();
        return listWallet.stream().map(walletEntity -> mapper.convertToDto(walletEntity)).collect(Collectors.toList());
    }
}
