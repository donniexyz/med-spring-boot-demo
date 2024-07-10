package com.github.donniexyz.demo.med.controller;

import com.github.donniexyz.demo.med.entity.AccountTransactionType;
import com.github.donniexyz.demo.med.entity.AccountType;
import com.github.donniexyz.demo.med.repository.AccountTransactionTypeRepository;
import com.github.donniexyz.demo.med.repository.AccountTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/accountTransactionType")
public class TransactionTypeController {

    @Autowired
    AccountTransactionTypeRepository transactionTypeRepository;

    @Autowired
    AccountTypeRepository accountTypeRepository;

    @GetMapping("/{typeCode}")
    @Transactional
    public AccountTransactionType get(@PathVariable("typeCode") String typeCode) {
        return transactionTypeRepository.findById(typeCode).orElseThrow().copy(true);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    public AccountTransactionType create(@RequestBody AccountTransactionType accountTransactionType) {
        String typeCode = accountTransactionType.getTypeCode();
        if (transactionTypeRepository.existsById(typeCode))
            throw new RuntimeException("AccountTransactionType typeCode=" + typeCode + " already exists");

        // get all AccountTypes in one go
        Set<String> applicableAccountTypeCodes = new HashSet<>();
        if (!CollectionUtils.isEmpty(accountTransactionType.getApplicableDebitAccountTypes()))
            applicableAccountTypeCodes.addAll(accountTransactionType.getApplicableDebitAccountTypes().stream().map(AccountType::getTypeCode).toList());
        if (!CollectionUtils.isEmpty(accountTransactionType.getApplicableCreditAccountTypes()))
            applicableAccountTypeCodes.addAll(accountTransactionType.getApplicableCreditAccountTypes().stream().map(AccountType::getTypeCode).toList());
        Map<String, AccountType> accountTypeMap = accountTypeRepository.findAllById(applicableAccountTypeCodes)
                .stream().collect(Collectors.toMap(AccountType::getTypeCode, at -> at));

        // when an account type does exist, throw exception. We do not want to create account type solely because transaction type demand it.
        if (applicableAccountTypeCodes.size() > accountTypeMap.size())
            throw new RuntimeException("AccountType does not exists");

        // prepare record to be inserted
        AccountTransactionType prepared = accountTransactionType.copy(false)
                .setApplicableCreditAccountTypes(null == accountTransactionType.getApplicableCreditAccountTypes() ? null
                        : accountTransactionType.getApplicableCreditAccountTypes().stream().map(at -> accountTypeMap.get(at.getTypeCode())).collect(Collectors.toSet()))
                .setApplicableDebitAccountTypes(null == accountTransactionType.getApplicableDebitAccountTypes() ? null
                        : accountTransactionType.getApplicableDebitAccountTypes().stream().map(at -> accountTypeMap.get(at.getTypeCode())).collect(Collectors.toSet()));
        return transactionTypeRepository.save(prepared);
    }

    @PutMapping(path = "/{typeCode}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    public AccountTransactionType update(@PathVariable("typeCode") String typeCode, @RequestBody AccountTransactionType accountTransactionType) {
        AccountTransactionType fetchedFromDb = transactionTypeRepository.findById(typeCode).orElseThrow();

        // accountTypes
        Set<String> accountTypeCodes = new HashSet<>();
        if (null != accountTransactionType.getApplicableDebitAccountTypes())
            accountTypeCodes.addAll(accountTransactionType.getApplicableDebitAccountTypes().stream().map(AccountType::getTypeCode).toList());
        if (null != accountTransactionType.getApplicableCreditAccountTypes())
            accountTypeCodes.addAll(accountTransactionType.getApplicableCreditAccountTypes().stream().map(AccountType::getTypeCode).toList());

        Map<String, AccountType> accountTypeMap = accountTypeCodes.isEmpty() ? Collections.emptyMap() : accountTypeRepository.findAllById(accountTypeCodes).stream().collect(Collectors.toMap(AccountType::getTypeCode, k -> k));

        if (null != accountTransactionType.getApplicableDebitAccountTypes()) {
            accountTransactionType.setApplicableDebitAccountTypes(
                    accountTransactionType.getApplicableDebitAccountTypes().stream().map(k -> accountTypeMap.get(k.getTypeCode())).collect(Collectors.toSet()));
        } else {
            accountTransactionType.setApplicableDebitAccountTypes(fetchedFromDb.getApplicableDebitAccountTypes());
        }
        if (null != accountTransactionType.getApplicableCreditAccountTypes()) {
            accountTransactionType.setApplicableCreditAccountTypes(
                    accountTransactionType.getApplicableCreditAccountTypes().stream().map(k -> accountTypeMap.get(k.getTypeCode())).collect(Collectors.toSet()));
        } else {
            accountTransactionType.setApplicableCreditAccountTypes(fetchedFromDb.getApplicableCreditAccountTypes());
        }

        return transactionTypeRepository.save(accountTransactionType);
    }

    @PatchMapping(path = "/{typeCode}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    public AccountTransactionType patch(@PathVariable("typeCode") String typeCode, @RequestBody AccountTransactionType accountTransactionType) {
        AccountTransactionType fetchedFromDb = transactionTypeRepository.findById(typeCode).orElseThrow();

        // accountTypes
        Set<String> accountTypeCodes = new HashSet<>();
        if (null != accountTransactionType.getApplicableDebitAccountTypes())
            accountTypeCodes.addAll(accountTransactionType.getApplicableDebitAccountTypes().stream().map(AccountType::getTypeCode).toList());
        if (null != accountTransactionType.getApplicableCreditAccountTypes())
            accountTypeCodes.addAll(accountTransactionType.getApplicableCreditAccountTypes().stream().map(AccountType::getTypeCode).toList());

        Map<String, AccountType> accountTypeMap = accountTypeCodes.isEmpty() ? Collections.emptyMap() : accountTypeRepository.findAllById(accountTypeCodes).stream().collect(Collectors.toMap(AccountType::getTypeCode, k -> k));

        if (null != accountTransactionType.getApplicableDebitAccountTypes()) {
            accountTransactionType.setApplicableDebitAccountTypes(
                    accountTransactionType.getApplicableDebitAccountTypes().stream().map(k -> accountTypeMap.get(k.getTypeCode())).collect(Collectors.toSet()));
        }
        if (null != accountTransactionType.getApplicableCreditAccountTypes()) {
            accountTransactionType.setApplicableCreditAccountTypes(
                    accountTransactionType.getApplicableCreditAccountTypes().stream().map(k -> accountTypeMap.get(k.getTypeCode())).collect(Collectors.toSet()));
        }

        return transactionTypeRepository.save(fetchedFromDb.copyFrom(accountTransactionType, true));
    }

}
