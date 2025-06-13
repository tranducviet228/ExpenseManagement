package com.kma.project.expensemanagement.service.impl;

import com.kma.project.expensemanagement.dto.request.WalletInputDto;
import com.kma.project.expensemanagement.dto.request.WalletTransferMoneyDto;
import com.kma.project.expensemanagement.dto.response.DataResponse;
import com.kma.project.expensemanagement.dto.response.PageResponse;
import com.kma.project.expensemanagement.dto.response.WalletInformationOutputDto;
import com.kma.project.expensemanagement.dto.response.WalletOutputDto;
import com.kma.project.expensemanagement.entity.GroupEntity;
import com.kma.project.expensemanagement.entity.GroupMemberEntity;
import com.kma.project.expensemanagement.entity.TransactionEntity;
import com.kma.project.expensemanagement.entity.WalletEntity;
import com.kma.project.expensemanagement.enums.RoleOfGroup;
import com.kma.project.expensemanagement.exception.AppException;
import com.kma.project.expensemanagement.mapper.WalletMapper;
import com.kma.project.expensemanagement.repository.GroupMemberRepository;
import com.kma.project.expensemanagement.repository.GroupRepository;
import com.kma.project.expensemanagement.repository.TransactionRepository;
import com.kma.project.expensemanagement.repository.WalletRepository;
import com.kma.project.expensemanagement.security.jwt.JwtUtils;
import com.kma.project.expensemanagement.service.WalletService;
import com.kma.project.expensemanagement.utils.DataUtils;
import com.kma.project.expensemanagement.utils.PageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class WalletServiceImpl implements WalletService {

    @Autowired
    WalletRepository repository;

    @Autowired
    GroupMemberRepository groupMemberRepository;

    @Autowired
    GroupRepository groupRepository;

    @Autowired
    TransactionRepository transactionRepository;

    @Autowired
    WalletMapper mapper;

    @Autowired
    JwtUtils jwtUtils;

    @Transactional
    @Override
    public WalletOutputDto add(WalletInputDto inputDto) {

        // check: if user is leader, this has permission to create wallet
        if(inputDto.getGroupId() != null){
            Optional<GroupMemberEntity> groupMemberEntity = groupMemberRepository
                    .findByGroupIdAndUserId(inputDto.getGroupId(), jwtUtils.getCurrentUserId());
            if(groupMemberEntity.isEmpty() || RoleOfGroup.MEMBER.equals(groupMemberEntity.get().getRoleOfGroup())){
                throw AppException.builder().errorCodes(Collections.singletonList("error.user-not-permission")).build();
            }
        }

        WalletEntity walletEntity = mapper.convertToEntity(inputDto);
        walletEntity.setCreatedBy(jwtUtils.getCurrentUserId());
        repository.save(walletEntity);
        return mapper.convertToDto(walletEntity);
    }

    @Transactional
    @Override
    public WalletOutputDto update(Long id, WalletInputDto inputDto) {
        WalletEntity walletEntity = repository.findById(id)
                .orElseThrow(() -> AppException.builder().errorCodes(Collections.singletonList("error.wallet-not-found")).build());
        if(!walletEntity.getCreatedBy().equals(jwtUtils.getCurrentUserId())){
            throw AppException.builder().errorCodes(Collections.singletonList("error.user-not-permission")).build();
        }
        mapper.update(inputDto, walletEntity);
        repository.save(walletEntity);
        return mapper.convertToDto(walletEntity);
    }

    @Transactional
    @Override
    public void delete(Long id) {
        WalletEntity walletEntity = repository.findById(id)
                .orElseThrow(() -> AppException.builder().errorCodes(Collections.singletonList("error.wallet-not-found")).build());
        List<TransactionEntity> transactionEntities = transactionRepository.findAllByWalletID(id);
        if(!transactionEntities.isEmpty()){
            throw AppException.builder().errorCodes(Collections.singletonList("error.wallet-has-transaction")).build();
        }
        if(!walletEntity.getCreatedBy().equals(jwtUtils.getCurrentUserId())){
            throw AppException.builder().errorCodes(Collections.singletonList("error.user-not-permission")).build();
        }
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
        Page<WalletEntity> listWallet = repository.findAllByCreatedBy(pageable, jwtUtils.getCurrentUserId());
        return PageUtils.formatPageResponse(listWallet.map(walletEntity -> mapper.convertToDto(walletEntity)));
    }

    @Override
    public WalletInformationOutputDto getInfoAllWallet(Long groupId) {
        BigDecimal moneyTotal = BigDecimal.ZERO;
        List<WalletOutputDto> listWalletOutput = new ArrayList<>();
        List<WalletEntity> wallets;
        if(Objects.nonNull(groupId)){
            wallets = repository.findAllByGroupIdOrderByCreatedAt(groupId);
        }else {
            wallets = repository.findAllWallet(jwtUtils.getCurrentUserId());
        }

        Set<Long> groupIds = wallets.stream().map(WalletEntity::getGroupId)
                .filter(Objects::nonNull).collect(Collectors.toSet());
        List<GroupEntity> groupEntities = groupRepository.findAllByIdIn(groupIds);
        Map<Long, String> groupMap = groupEntities.stream().collect(Collectors.toMap(GroupEntity::getId, GroupEntity::getName));
        for (WalletEntity item : wallets) {
            WalletOutputDto outputDto = mapper.convertToDto(item);
            outputDto.setGroupName(groupMap.get(item.getGroupId()));
            listWalletOutput.add(outputDto);
            moneyTotal = moneyTotal.add(item.getAccountBalance());
        }
        return WalletInformationOutputDto.builder()
                .moneyTotal(moneyTotal)
                .walletList(listWalletOutput)
                .build();
    }

    @Transactional
    @Override
    public void transferMoney(Long fromWalletId, WalletTransferMoneyDto walletTransferMoneyDto) {

        List<WalletEntity> list = new ArrayList<>();
        WalletEntity fromWalletEntity = repository.findById(fromWalletId)
                .orElseThrow(() -> AppException.builder().errorCodes(Collections.singletonList("error.wallet-not-found")).build());
        list.add(fromWalletEntity);

        WalletEntity toWalletEntity = repository.findById(walletTransferMoneyDto.getToWalletId())
                .orElseThrow(() -> AppException.builder().errorCodes(Collections.singletonList("error.wallet-not-found")).build());
        list.add(toWalletEntity);

        fromWalletEntity.setAccountBalance(fromWalletEntity.getAccountBalance().subtract(walletTransferMoneyDto.getAmount()));
        toWalletEntity.setAccountBalance(toWalletEntity.getAccountBalance().add(walletTransferMoneyDto.getAmount()));

        repository.saveAll(list);
    }
}
