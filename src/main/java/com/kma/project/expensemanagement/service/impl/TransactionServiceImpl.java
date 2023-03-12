package com.kma.project.expensemanagement.service.impl;

import com.kma.project.expensemanagement.dto.request.TransactionInputDto;
import com.kma.project.expensemanagement.dto.response.DataResponse;
import com.kma.project.expensemanagement.dto.response.PageResponse;
import com.kma.project.expensemanagement.dto.response.TransactionOutputDto;
import com.kma.project.expensemanagement.entity.CategoryEntity;
import com.kma.project.expensemanagement.entity.TransactionEntity;
import com.kma.project.expensemanagement.entity.WalletEntity;
import com.kma.project.expensemanagement.exception.AppException;
import com.kma.project.expensemanagement.mapper.TransactionMapper;
import com.kma.project.expensemanagement.repository.CategoryRepository;
import com.kma.project.expensemanagement.repository.TransactionRepository;
import com.kma.project.expensemanagement.repository.WalletRepository;
import com.kma.project.expensemanagement.service.TransactionService;
import com.kma.project.expensemanagement.service.UploadFileService;
import com.kma.project.expensemanagement.utils.DataUtils;
import com.kma.project.expensemanagement.utils.PageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;

@Transactional(readOnly = true)
@Service
public class TransactionServiceImpl implements TransactionService {

    @Autowired
    TransactionRepository repository;

    @Autowired
    TransactionMapper mapper;

    @Autowired
    UploadFileService uploadFileService;

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    WalletRepository walletRepository;

    @Transactional
    @Override
    public TransactionOutputDto add(TransactionInputDto inputDto) {
        TransactionEntity entity = mapper.convertToEntity(inputDto);
        String fileUrl = uploadFileService.uploadFileCloud(inputDto.getImageFile());
        entity.setImageUrl(fileUrl);

        CategoryEntity category = categoryRepository.findById(inputDto.getCategoryId())
                .orElseThrow(() -> AppException.builder().errorCodes(Collections.singletonList("error.category-not-found")).build());
        entity.setCategory(category);
        entity.setCategoryName(category.getName());

        WalletEntity wallet = walletRepository.findById(inputDto.getWalletId())
                .orElseThrow(() -> AppException.builder().errorCodes(Collections.singletonList("error.wallet-not-found")).build());
        entity.setWallet(wallet);
        entity.setWalletName(wallet.getName());
        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());
        repository.save(entity);
        return mapper.convertToDto(entity);
    }

    @Transactional
    @Override
    public TransactionOutputDto update(Long id, TransactionInputDto inputDto) {
        TransactionEntity entity = repository.findById(id)
                .orElseThrow(() -> AppException.builder().errorCodes(Collections.singletonList("error.transaction-not-found")).build());

        if (inputDto.getImageFile() != null) {
            String fileUrl = uploadFileService.uploadFileCloud(inputDto.getImageFile());
            entity.setImageUrl(fileUrl);
        }
        if (inputDto.getCategoryId() != null) {
            CategoryEntity category = categoryRepository.findById(inputDto.getCategoryId())
                    .orElseThrow(() -> AppException.builder().errorCodes(Collections.singletonList("error.category-not-found")).build());
            entity.setCategory(category);
            entity.setCategoryName(category.getName());
        }
        if (inputDto.getWalletId() != null) {
            WalletEntity wallet = walletRepository.findById(inputDto.getWalletId())
                    .orElseThrow(() -> AppException.builder().errorCodes(Collections.singletonList("error.wallet-not-found")).build());
            entity.setWallet(wallet);
            entity.setWalletName(wallet.getName());
        }
        mapper.update(inputDto, entity);
        repository.save(entity);
        return mapper.convertToDto(entity);
    }

    @Transactional
    @Override
    public void delete(Long id) {
        TransactionEntity entity = repository.findById(id)
                .orElseThrow(() -> AppException.builder().errorCodes(Collections.singletonList("error.transaction-not-found")).build());
        repository.delete(entity);
    }

    @Override
    public DataResponse<TransactionOutputDto> getDetail(Long id) {
        TransactionEntity entity = repository.findById(id)
                .orElseThrow(() -> AppException.builder().errorCodes(Collections.singletonList("error.transaction-not-found")).build());
        TransactionOutputDto transactionOutputDto = mapper.convertToDto(entity);
        transactionOutputDto.setCategoryId(entity.getCategory().getId());
        transactionOutputDto.setWalletId(entity.getWallet().getId());
        return DataUtils.formatData(transactionOutputDto);
    }

    @Override
    public PageResponse<TransactionOutputDto> getAllTransaction(Integer page, Integer size, String sort, String search) {
        Pageable pageable = PageUtils.customPageable(page, size, sort);
        Page<TransactionEntity> listTransaction = repository.findAll(pageable);
        return PageUtils.formatPageResponse(listTransaction.map(entity -> mapper.convertToDto(entity)));
    }
}
