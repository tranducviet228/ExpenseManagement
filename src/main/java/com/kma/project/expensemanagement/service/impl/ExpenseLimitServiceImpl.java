package com.kma.project.expensemanagement.service.impl;

import com.kma.project.expensemanagement.dto.request.ExpenseLimitInputDto;
import com.kma.project.expensemanagement.dto.response.DataResponse;
import com.kma.project.expensemanagement.dto.response.ExpenseLimitOutputDto;
import com.kma.project.expensemanagement.dto.response.PageResponse;
import com.kma.project.expensemanagement.dto.response.WalletOutputDto;
import com.kma.project.expensemanagement.entity.CategoryEntity;
import com.kma.project.expensemanagement.entity.ExpenseLimitEntity;
import com.kma.project.expensemanagement.entity.TransactionEntity;
import com.kma.project.expensemanagement.entity.WalletEntity;
import com.kma.project.expensemanagement.exception.AppException;
import com.kma.project.expensemanagement.mapper.ExpenseLimitMapper;
import com.kma.project.expensemanagement.mapper.WalletMapper;
import com.kma.project.expensemanagement.repository.CategoryRepository;
import com.kma.project.expensemanagement.repository.DeviceTokenRepository;
import com.kma.project.expensemanagement.repository.ExpenseLimitRepository;
import com.kma.project.expensemanagement.repository.WalletRepository;
import com.kma.project.expensemanagement.security.jwt.JwtUtils;
import com.kma.project.expensemanagement.service.ExpenseLimitService;
import com.kma.project.expensemanagement.service.NotificationService;
import com.kma.project.expensemanagement.utils.DataUtils;
import com.kma.project.expensemanagement.utils.PageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true, propagation = Propagation.REQUIRES_NEW)
public class ExpenseLimitServiceImpl implements ExpenseLimitService {

    @Autowired
    ExpenseLimitRepository repository;

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    WalletRepository walletRepository;

    @Autowired
    ExpenseLimitMapper mapper;

    @Autowired
    WalletMapper walletMapper;

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    NotificationService notificationService;

    @Autowired
    DeviceTokenRepository deviceTokenRepository;

    @Transactional
    @Override
    public void updateToLimit(TransactionEntity transactionEntity) {
        List<ExpenseLimitEntity> listLimit = repository
                .getValidExpenseLimit(transactionEntity.getCategory().getId().toString(), transactionEntity.getWallet().getId().toString(),
                        LocalDate.now(), transactionEntity.getCreatedBy());
        System.out.println(LocalDate.now());
        for (ExpenseLimitEntity expenseLimit : listLimit) {
            expenseLimit.setActualAmount(expenseLimit.getActualAmount().add(transactionEntity.getAmount()));

            if (expenseLimit.getActualAmount().compareTo(transactionEntity.getAmount()) > 0) {
                // bắn noti lên app
                String message = "Hạn mức :name đã bội chi :value";
                message = message.replace(":name", expenseLimit.getLimitName());
                BigDecimal value = expenseLimit.getActualAmount().subtract(transactionEntity.getAmount());
                message = message.replace(":value", value.toString());

                String finalMessage = message;
                deviceTokenRepository.findFirstByUserId(transactionEntity.getCreatedBy()).ifPresent(deviceTokenEntity -> {
                    String deviceToken = deviceTokenEntity.getToken();
                    notificationService.sendNotification(deviceToken, "Viet Wallet", finalMessage);
                });
            }
        }
        repository.saveAll(listLimit);

    }

    @Transactional
    @Override
    public ExpenseLimitOutputDto add(ExpenseLimitInputDto inputDto) {
        ExpenseLimitEntity entity = mapper.convertToEntity(inputDto);
        entity.setActualAmount(BigDecimal.ZERO);
        entity.setCreatedBy(jwtUtils.getCurrentUserId());
        checkExist(inputDto);
        repository.save(entity);
        return mapper.convertToOutputDto(entity);
    }

    @Transactional
    @Override
    public ExpenseLimitOutputDto update(Long id, ExpenseLimitInputDto inputDto) {
        ExpenseLimitEntity entity = repository.findById(id)
                .orElseThrow(() -> AppException.builder().errorCodes(Collections.singletonList("error.expense-limit-not-found")).build());

        mapper.update(inputDto, entity);
        checkExist(inputDto);
        repository.save(entity);
        return mapper.convertToOutputDto(entity);
    }

    public void checkExist(ExpenseLimitInputDto inputDto) {
        for (String categoryId : inputDto.getCategoryIds()) {
            CategoryEntity categoryEntity = categoryRepository.findById(Long.valueOf(categoryId))
                    .orElseThrow(() -> AppException.builder().errorCodes(Collections.singletonList("error.category-not-found")).build());
        }

        for (String walletId : inputDto.getWalletIds()) {
            WalletEntity walletEntity = walletRepository.findById(Long.valueOf(walletId))
                    .orElseThrow(() -> AppException.builder().errorCodes(Collections.singletonList("error.wallet-not-found")).build());
        }
    }

    @Transactional
    @Override
    public void delete(Long id) {
        ExpenseLimitEntity entity = repository.findById(id)
                .orElseThrow(() -> AppException.builder().errorCodes(Collections.singletonList("error.expense-limit-not-found")).build());
        repository.delete(entity);
    }

    @Override
    public DataResponse<ExpenseLimitOutputDto> getDetail(Long id) {
        ExpenseLimitEntity entity = repository.findById(id)
                .orElseThrow(() -> AppException.builder().errorCodes(Collections.singletonList("error.expense-limit-not-found")).build());
        return DataUtils.formatData(mapper.convertToOutputDto(entity));
    }

    @Override
    public PageResponse<ExpenseLimitOutputDto> getAllExpenseLimit(Integer page, Integer size, String sort, String search) {
        Pageable pageable = PageUtils.customPageable(page, size, sort);
        search = PageUtils.buildSearch(search);
        Page<ExpenseLimitEntity> pageExpenseLimit = repository.getAllByCreatedByAndLimitNameLikeIgnoreCase(pageable, jwtUtils.getCurrentUserId(), search);

        Set<Long> walletIds = new HashSet<>();
        pageExpenseLimit.forEach(entity -> {
            walletIds.addAll(Arrays.stream(entity.getWalletIds()).map(Long::valueOf).collect(Collectors.toSet()));
        });

        Map<Long, WalletEntity> walletMap = walletRepository.findAllById(walletIds)
                .stream().collect(Collectors.toMap(WalletEntity::getId, Function.identity()));
        return PageUtils.formatPageResponse(pageExpenseLimit.map(userEntity ->
                {
                    ExpenseLimitOutputDto outputDto = mapper.convertToOutputDto(userEntity);
                    List<WalletOutputDto> walletOutputs = new ArrayList<>();
                    outputDto.getWalletIds().forEach(s -> {
                        walletOutputs.add(walletMapper.convertToDto(walletMap.get(Long.valueOf(s))));
                    });
                    outputDto.setWalletOutputs(walletOutputs);
                    return outputDto;
                }
        ));
    }
}
