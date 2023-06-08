package com.kma.project.expensemanagement.service.impl;

import com.kma.project.expensemanagement.dto.request.TransactionInputDto;
import com.kma.project.expensemanagement.dto.response.DataResponse;
import com.kma.project.expensemanagement.dto.response.PageResponse;
import com.kma.project.expensemanagement.dto.response.TransactionOutputDto;
import com.kma.project.expensemanagement.entity.CategoryEntity;
import com.kma.project.expensemanagement.entity.CategoryLogoEntity;
import com.kma.project.expensemanagement.entity.TransactionEntity;
import com.kma.project.expensemanagement.entity.WalletEntity;
import com.kma.project.expensemanagement.exception.AppException;
import com.kma.project.expensemanagement.mapper.TransactionMapper;
import com.kma.project.expensemanagement.repository.CategoryLogoRepository;
import com.kma.project.expensemanagement.repository.CategoryRepository;
import com.kma.project.expensemanagement.repository.TransactionRepository;
import com.kma.project.expensemanagement.repository.WalletRepository;
import com.kma.project.expensemanagement.security.jwt.JwtUtils;
import com.kma.project.expensemanagement.service.ExpenseLimitService;
import com.kma.project.expensemanagement.service.TransactionService;
import com.kma.project.expensemanagement.service.UploadFileService;
import com.kma.project.expensemanagement.utils.DataUtils;
import com.kma.project.expensemanagement.utils.EnumUtils;
import com.kma.project.expensemanagement.utils.PageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

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

    @Autowired
    CategoryLogoRepository categoryLogoRepository;

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    ExpenseLimitService expenseLimitService;

    @Transactional
    @Override
    public TransactionOutputDto add(TransactionInputDto inputDto) {
        TransactionEntity entity = mapper.convertToEntity(inputDto);
//        if (inputDto.getImageFile() != null) {
//            String fileUrl = uploadFileService.uploadFileCloud(inputDto.getImageFile());
//            entity.setImageUrl(fileUrl);
//        }

        CategoryEntity category = categoryRepository.findById(inputDto.getCategoryId())
                .orElseThrow(() -> AppException.builder().errorCodes(Collections.singletonList("error.category-not-found")).build());
        entity.setCategory(category);

        WalletEntity wallet = walletRepository.findById(inputDto.getWalletId())
                .orElseThrow(() -> AppException.builder().errorCodes(Collections.singletonList("error.wallet-not-found")).build());
        entity.setWallet(wallet);
        entity.setCreatedBy(jwtUtils.getCurrentUserId());
        if (inputDto.getAriseDate() == null) {
            entity.setAriseDate(LocalDateTime.now());
        }
        repository.save(entity);

        // update wallet
        if (EnumUtils.EXPENSE.equals(entity.getTransactionType().name())) {
            wallet.setAccountBalance(wallet.getAccountBalance().subtract(entity.getAmount()));
        } else {
            wallet.setAccountBalance(wallet.getAccountBalance().add(entity.getAmount()));
        }
        walletRepository.save(wallet);

        // update expense limit
        expenseLimitService.updateToLimit(entity);
        TransactionOutputDto transactionOutputDto = mapper.convertToDto(entity);
        mapDataResponse(transactionOutputDto, entity);

        return transactionOutputDto;
    }

    @Transactional
    @Override
    public TransactionOutputDto update(Long id, TransactionInputDto inputDto) {
        TransactionEntity entity = repository.findById(id)
                .orElseThrow(() -> AppException.builder().errorCodes(Collections.singletonList("error.transaction-not-found")).build());

//        if (inputDto.getImageFile() != null) {
//            String fileUrl = uploadFileService.uploadFileCloud(inputDto.getImageFile());
//            entity.setImageUrl(fileUrl);
//        }
        if (inputDto.getCategoryId() != null) {
            CategoryEntity category = categoryRepository.findById(inputDto.getCategoryId())
                    .orElseThrow(() -> AppException.builder().errorCodes(Collections.singletonList("error.category-not-found")).build());
            entity.setCategory(category);
        }
        if (inputDto.getWalletId() != null) {
            WalletEntity wallet = walletRepository.findById(inputDto.getWalletId())
                    .orElseThrow(() -> AppException.builder().errorCodes(Collections.singletonList("error.wallet-not-found")).build());
            entity.setWallet(wallet);
        }
        // update wallet
        if (inputDto.getAmount() != null && !(inputDto.getAmount().compareTo(entity.getAmount()) == 0)) {
            WalletEntity walletEntity = entity.getWallet();
            if (EnumUtils.EXPENSE.equals(entity.getTransactionType().name())) {
                walletEntity.setAccountBalance(walletEntity.getAccountBalance().add(entity.getAmount()));
            } else {
                walletEntity.setAccountBalance(walletEntity.getAccountBalance().subtract(entity.getAmount()));
            }
            walletRepository.save(walletEntity);
        }
        mapper.update(inputDto, entity);
        repository.save(entity);
        TransactionOutputDto transactionOutputDto = mapper.convertToDto(entity);
        mapDataResponse(transactionOutputDto, entity);
        return transactionOutputDto;
    }

    @Transactional
    @Override
    public void delete(Long id) {
        TransactionEntity entity = repository.findById(id)
                .orElseThrow(() -> AppException.builder().errorCodes(Collections.singletonList("error.transaction-not-found")).build());
        repository.delete(entity);

        // update wallet
        WalletEntity walletEntity = entity.getWallet();

        if (EnumUtils.EXPENSE.equals(entity.getTransactionType().name())) {
            walletEntity.setAccountBalance(walletEntity.getAccountBalance().add(entity.getAmount()));
        } else {
            walletEntity.setAccountBalance(walletEntity.getAccountBalance().subtract(entity.getAmount()));
        }
        walletRepository.save(walletEntity);

    }

    @Override
    public DataResponse<TransactionOutputDto> getDetail(Long id) {
        TransactionEntity entity = repository.findById(id)
                .orElseThrow(() -> AppException.builder().errorCodes(Collections.singletonList("error.transaction-not-found")).build());
        TransactionOutputDto transactionOutputDto = mapper.convertToDto(entity);
        mapDataResponse(transactionOutputDto, entity);
        return DataUtils.formatData(transactionOutputDto);
    }

    @Override
    public PageResponse<TransactionOutputDto> getAllTransaction(Integer page, Integer size, String sort, String search) {
        Pageable pageable = PageUtils.customPageable(page, size, sort);
        Page<TransactionEntity> listTransaction = repository.findAllByCreatedBy(pageable, jwtUtils.getCurrentUserId());
        return PageUtils.formatPageResponse(listTransaction.map(entity -> {
            TransactionOutputDto outputDto = mapper.convertToDto(entity);
            mapDataResponse(outputDto, entity);
            return outputDto;
        }));
    }

    @Override
    public void mapDataResponse(TransactionOutputDto outputDto, TransactionEntity entity) {
        outputDto.setCategoryName(entity.getCategory().getName());
        outputDto.setCategoryId(entity.getCategory().getId());
        outputDto.setWalletName(entity.getWallet().getName());
        outputDto.setWalletId(entity.getWallet().getId());
        outputDto.setWalletType(entity.getWallet().getAccountType());

        if (entity.getCategory().getLogoImageID() != null) {
            Optional<CategoryLogoEntity> categoryLogoEntity = categoryLogoRepository.findById(entity.getCategory().getLogoImageID());
            categoryLogoEntity.ifPresent(logoEntity -> outputDto.setCategoryLogo(logoEntity.getFileUrl()));
        }
    }
}
