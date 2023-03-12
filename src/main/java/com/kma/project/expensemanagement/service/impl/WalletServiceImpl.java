package com.kma.project.expensemanagement.service.impl;

import com.kma.project.expensemanagement.dto.request.WalletInputDto;
import com.kma.project.expensemanagement.dto.response.DataResponse;
import com.kma.project.expensemanagement.dto.response.PageResponse;
import com.kma.project.expensemanagement.dto.response.WalletOutputDto;
import com.kma.project.expensemanagement.entity.WalletEntity;
import com.kma.project.expensemanagement.exception.AppException;
import com.kma.project.expensemanagement.mapper.WalletMapper;
import com.kma.project.expensemanagement.repository.WalletRepository;
import com.kma.project.expensemanagement.service.WalletService;
import com.kma.project.expensemanagement.utils.DataUtils;
import com.kma.project.expensemanagement.utils.PageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;

@Service
@Transactional(readOnly = true)
public class WalletServiceImpl implements WalletService {

    @Autowired
    WalletRepository repository;

    @Autowired
    WalletMapper mapper;

    @Transactional
    @Override
    public WalletOutputDto add(WalletInputDto inputDto) {
        WalletEntity walletEntity = mapper.convertToEntity(inputDto);
        walletEntity.setUpdatedAt(LocalDateTime.now());
        walletEntity.setCreatedAt(LocalDateTime.now());
        repository.save(walletEntity);
        return mapper.convertToDto(walletEntity);
    }

    @Transactional
    @Override
    public WalletOutputDto update(Long id, WalletInputDto inputDto) {
        WalletEntity walletEntity = repository.findById(id)
                .orElseThrow(() -> AppException.builder().errorCodes(Collections.singletonList("error.wallet-not-found")).build());
        mapper.update(inputDto, walletEntity);
        repository.save(walletEntity);
        return mapper.convertToDto(walletEntity);
    }

    @Transactional
    @Override
    public void delete(Long id) {
        WalletEntity walletEntity = repository.findById(id)
                .orElseThrow(() -> AppException.builder().errorCodes(Collections.singletonList("error.wallet-not-found")).build());
        repository.delete(walletEntity);
    }

    @Override
    public DataResponse<WalletOutputDto> getDetail(Long id) {
        WalletEntity walletEntity = repository.findById(id)
                .orElseThrow(() -> AppException.builder().errorCodes(Collections.singletonList("error.wallet-not-found")).build());
        return DataUtils.formatData(mapper.convertToDto(walletEntity));
    }

    @Override
    public PageResponse<WalletOutputDto> getAllWallet(Integer page, Integer size, String sort) {
        Pageable pageable = PageUtils.customPageable(page, size, sort);
        Page<WalletEntity> listWallet = repository.findAll(pageable);
        return PageUtils.formatPageResponse(listWallet.map(walletEntity -> mapper.convertToDto(walletEntity)));
    }
}
