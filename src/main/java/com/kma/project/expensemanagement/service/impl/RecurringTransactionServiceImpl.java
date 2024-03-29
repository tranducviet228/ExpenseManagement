package com.kma.project.expensemanagement.service.impl;

import com.kma.project.expensemanagement.dto.request.RecurringTransactionInputDto;
import com.kma.project.expensemanagement.dto.response.DataResponse;
import com.kma.project.expensemanagement.dto.response.PageResponse;
import com.kma.project.expensemanagement.dto.response.RecurringTransactionOutputDto;
import com.kma.project.expensemanagement.entity.CategoryEntity;
import com.kma.project.expensemanagement.entity.CategoryLogoEntity;
import com.kma.project.expensemanagement.entity.RecurringTransactionEntity;
import com.kma.project.expensemanagement.entity.WalletEntity;
import com.kma.project.expensemanagement.enums.TransactionType;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.Collections;
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
        LocalDateTime localDateTime = LocalDateTime.of(inputDto.getFromDate(), LocalTime.parse(inputDto.getTime()));
        entity.setAriseDate(localDateTime);
        recurringTransactionRepository.save(entity);
        return getResponse(entity);
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
        LocalDateTime localDateTime = LocalDateTime.of(inputDto.getFromDate(), LocalTime.parse(inputDto.getTime()));
        entity.setAriseDate(localDateTime);
        recurringTransactionRepository.save(entity);
        return getResponse(entity);
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

    public RecurringTransactionOutputDto getResponse(RecurringTransactionEntity entity) {
        RecurringTransactionOutputDto outputDto = recurringTransactionMapper.convertToDto(entity);
        mapDataResponse(outputDto, entity);
        return outputDto;
    }

    @Override
    public PageResponse<RecurringTransactionOutputDto> getAllTransaction(Integer page, Integer size, String sort,
                                                                         String search, String type, String status) {
        Pageable pageable = PageUtils.customPageable(page, size, sort);
        Page<RecurringTransactionEntity> pageTransaction = null;

        if (EnumUtils.FINISHED.equals(status)) {
            pageTransaction = recurringTransactionRepository
                    .findAllEndTransaction(pageable, jwtUtils.getCurrentUserId(), LocalDate.now(), TransactionType.valueOf(type));
        } else if (EnumUtils.ON_GOING.equals(status)) {
            pageTransaction = recurringTransactionRepository
                    .findAllStartTransaction(pageable, jwtUtils.getCurrentUserId(), LocalDate.now(), TransactionType.valueOf(type));
        } else if (EnumUtils.UP_COMING.equals(status)) {
            pageTransaction = recurringTransactionRepository
                    .findAllNextTransaction(pageable, jwtUtils.getCurrentUserId(), LocalDate.now(), TransactionType.valueOf(type));
        } else {
            pageTransaction = recurringTransactionRepository
                    .getAllRecurringByCreatedByAndTransactionType(pageable, jwtUtils.getCurrentUserId(), TransactionType.valueOf(type));
        }

        return PageUtils.formatPageResponse(pageTransaction.map(entity -> {
            RecurringTransactionOutputDto outputDto = recurringTransactionMapper.convertToDto(entity);
            mapDataResponse(outputDto, entity);
            return outputDto;
        }));
    }

    public void mapDataResponse(RecurringTransactionOutputDto outputDto, RecurringTransactionEntity entity) {
        if (entity.getCategory() != null) {
            outputDto.setCategoryName(entity.getCategory().getName());
            outputDto.setCategoryId(entity.getCategory().getId());
            if (entity.getCategory().getLogoImageID() != null) {
                Optional<CategoryLogoEntity> categoryLogoEntity = categoryLogoRepository.findById(entity.getCategory().getLogoImageID());
                categoryLogoEntity.ifPresent(logoEntity -> outputDto.setCategoryLogo(logoEntity.getFileUrl()));
            }
        }
        if (entity.getWallet() != null) {
            outputDto.setWalletName(entity.getWallet().getName());
            outputDto.setWalletId(entity.getWallet().getId());
        }

    }
}

