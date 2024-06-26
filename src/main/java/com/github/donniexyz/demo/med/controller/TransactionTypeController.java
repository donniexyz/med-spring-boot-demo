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

import java.util.*;
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
                        : accountTransactionType.getApplicableDebitAccountTypes().stream().map(at -> accountTypeMap.get(at.getTypeCode())).collect(Collectors.toSet()))
                ;
        return transactionTypeRepository.save(prepared);
    }

    @PutMapping(path = "/{typeCode}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public AccountTransactionType update(@PathVariable("typeCode") String typeCode, @RequestBody AccountTransactionType accountTransactionType) {
        AccountTransactionType fetchedFromDb = transactionTypeRepository.findById(typeCode).orElseThrow();
        return transactionTypeRepository.save(fetchedFromDb.copyFrom(accountTransactionType, true));
    }

}
