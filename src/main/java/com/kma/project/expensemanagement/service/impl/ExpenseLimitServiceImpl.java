package com.kma.project.expensemanagement.service.impl;

import com.kma.project.expensemanagement.dto.request.ExpenseLimitInputDto;
import com.kma.project.expensemanagement.dto.request.TransactionInputDto;
import com.kma.project.expensemanagement.dto.response.DataResponse;
import com.kma.project.expensemanagement.dto.response.ExpenseLimitOutputDto;
import com.kma.project.expensemanagement.dto.response.PageResponse;
import com.kma.project.expensemanagement.entity.CategoryEntity;
import com.kma.project.expensemanagement.entity.ExpenseLimitEntity;
import com.kma.project.expensemanagement.entity.WalletEntity;
import com.kma.project.expensemanagement.exception.AppException;
import com.kma.project.expensemanagement.mapper.ExpenseLimitMapper;
import com.kma.project.expensemanagement.repository.CategoryRepository;
import com.kma.project.expensemanagement.repository.ExpenseLimitRepository;
import com.kma.project.expensemanagement.repository.WalletRepository;
import com.kma.project.expensemanagement.security.jwt.JwtUtils;
import com.kma.project.expensemanagement.service.ExpenseLimitService;
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
import java.util.Collections;
import java.util.List;

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
    JwtUtils jwtUtils;

    @Transactional
    @Override
    public void updateToLimit(TransactionInputDto inputDto) {
        List<ExpenseLimitEntity> listLimit = repository
                .getValidExpenseLimit(inputDto.getCategoryId().toString(), inputDto.getWalletId().toString(), LocalDate.now());

        for (ExpenseLimitEntity entity : listLimit) {
            entity.setActualAmount(entity.getActualAmount().add(inputDto.getAmount()));
            repository.save(entity);

            if (entity.getActualAmount().compareTo(entity.getAmount()) > 0) {
                // bắn noti lên app

            }
        }


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
    public PageResponse<ExpenseLimitOutputDto> getAllTransaction(Integer page, Integer size, String sort, String search) {
        Pageable pageable = PageUtils.customPageable(page, size, sort);
        search = PageUtils.buildSearch(search);
        Page<ExpenseLimitEntity> pageUser = repository.getAllByCreatedByAndLimitNameLikeIgnoreCase(pageable, jwtUtils.getCurrentUserId(), search);
        return PageUtils.formatPageResponse(pageUser.map(userEntity -> mapper.convertToOutputDto(userEntity)));
    }
}
