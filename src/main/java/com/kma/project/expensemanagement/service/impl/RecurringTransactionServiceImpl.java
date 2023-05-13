package com.kma.project.expensemanagement.service.impl;

import com.kma.project.expensemanagement.dto.request.RecurringTransactionInputDto;
import com.kma.project.expensemanagement.dto.response.DataResponse;
import com.kma.project.expensemanagement.dto.response.PageResponse;
import com.kma.project.expensemanagement.dto.response.RecurringTransactionOutputDto;
import com.kma.project.expensemanagement.entity.CategoryEntity;
import com.kma.project.expensemanagement.entity.CategoryLogoEntity;
import com.kma.project.expensemanagement.entity.RecurringTransactionEntity;
import com.kma.project.expensemanagement.entity.WalletEntity;
import com.kma.project.expensemanagement.exception.AppException;
import com.kma.project.expensemanagement.mapper.RecurringTransactionMapper;
import com.kma.project.expensemanagement.repository.CategoryLogoRepository;
import com.kma.project.expensemanagement.repository.CategoryRepository;
import com.kma.project.expensemanagement.repository.RecurringTransactionRepository;
import com.kma.project.expensemanagement.repository.WalletRepository;
import com.kma.project.expensemanagement.security.jwt.JwtUtils;
import com.kma.project.expensemanagement.service.RecurringTransactionService;
import com.kma.project.expensemanagement.utils.DataUtils;
import com.kma.project.expensemanagement.utils.EnumUtils;
import com.kma.project.expensemanagement.utils.PageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class RecurringTransactionServiceImpl implements RecurringTransactionService {

    @Autowired
    private RecurringTransactionRepository recurringTransactionRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private RecurringTransactionMapper recurringTransactionMapper;

    @Autowired
    private CategoryLogoRepository categoryLogoRepository;

    @Autowired
    private JwtUtils jwtUtils;

    @Transactional
    @Override
    public RecurringTransactionOutputDto add(RecurringTransactionInputDto inputDto) {

        RecurringTransactionEntity entity;
        try {
            entity = recurringTransactionMapper.convertToEntity(inputDto);
        } catch (DateTimeParseException e) {
            throw AppException.builder().errorCodes(Collections.singletonList("error.time-is-not-valid")).build();
        }

        CategoryEntity category = categoryRepository.findById(inputDto.getCategoryId())
                .orElseThrow(() -> AppException.builder().errorCodes(Collections.singletonList("error.category-not-found")).build());
        entity.setCategory(category);

        WalletEntity wallet = walletRepository.findById(inputDto.getWalletId())
                .orElseThrow(() -> AppException.builder().errorCodes(Collections.singletonList("error.wallet-not-found")).build());
        entity.setWallet(wallet);

        entity.setCreatedBy(jwtUtils.getCurrentUserId());
        entity.setAriseDate(LocalDateTime.now());
        recurringTransactionRepository.save(entity);
        return recurringTransactionMapper.convertToDto(entity);
    }

    @Transactional
    @Override
    public RecurringTransactionOutputDto update(Long id, RecurringTransactionInputDto inputDto) {
        RecurringTransactionEntity entity = recurringTransactionRepository.findById(id)
                .orElseThrow(() -> AppException.builder()
                        .errorCodes(Collections.singletonList("error.recurring-transaction-not-found")).build());

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

        try {
            recurringTransactionMapper.update(inputDto, entity);
        } catch (DateTimeParseException e) {
            throw AppException.builder().errorCodes(Collections.singletonList("error.time-is-not-valid")).build();
        }
        recurringTransactionRepository.save(entity);
        return recurringTransactionMapper.convertToDto(entity);
    }

    @Transactional
    @Override
    public void delete(Long id) {
        RecurringTransactionEntity entity = recurringTransactionRepository.findById(id)
                .orElseThrow(() -> AppException.builder()
                        .errorCodes(Collections.singletonList("error.recurring-transaction-not-found")).build());
        recurringTransactionRepository.delete(entity);
    }

    @Override
    public DataResponse<RecurringTransactionOutputDto> getDetail(Long id) {
        RecurringTransactionEntity entity = recurringTransactionRepository.findById(id)
                .orElseThrow(() -> AppException.builder()
                        .errorCodes(Collections.singletonList("error.recurring-transaction-not-found")).build());

        RecurringTransactionOutputDto outputDto = recurringTransactionMapper.convertToDto(entity);
        mapDataResponse(outputDto, entity);
        return DataUtils.formatData(outputDto);
    }

    @Override
    public PageResponse<RecurringTransactionOutputDto> getAllTransaction(Integer page, Integer size, String sort, String search) {
        Pageable pageable = PageUtils.customPageable(page, size, sort);
        Page<RecurringTransactionEntity> listTransaction = recurringTransactionRepository
                .findAllByCreatedBy(pageable, jwtUtils.getCurrentUserId());
        return PageUtils.formatPageResponse(listTransaction.map(entity -> {
            RecurringTransactionOutputDto outputDto = recurringTransactionMapper.convertToDto(entity);
            mapDataResponse(outputDto, entity);
            return outputDto;
        }));
    }

    public void mapDataResponse(RecurringTransactionOutputDto outputDto, RecurringTransactionEntity entity) {
        outputDto.setCategoryName(entity.getCategory().getName());
        outputDto.setWalletName(entity.getWallet().getName());

        Optional<CategoryLogoEntity> categoryLogoEntity = categoryLogoRepository.findById(entity.getCategory().getLogoImageID());
        categoryLogoEntity.ifPresent(logoEntity -> outputDto.setCategoryLogo(logoEntity.getFileUrl()));
    }

    public void jobAddTransaction() {
        List<RecurringTransactionEntity> recurringTransactionEntities = recurringTransactionRepository.getAllValidRecurring(LocalDateTime.now());
        recurringTransactionEntities.forEach(entity -> {
            switch (entity.getFrequencyType().name()) {
                case EnumUtils.HOURLY:

                case EnumUtils.DAILY:
                case EnumUtils.WEEKLY:
                case EnumUtils.MONTHLY:
                case EnumUtils.QUATERLY:
                case EnumUtils.YEARLY:
                case EnumUtils.WEEKDAY:
            }
        });
    }
}

