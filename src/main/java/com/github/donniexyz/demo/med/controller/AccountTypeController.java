package com.github.donniexyz.demo.med.controller;

import com.github.donniexyz.demo.med.entity.AccountOwnerType;
import com.github.donniexyz.demo.med.entity.AccountType;
import com.github.donniexyz.demo.med.repository.AccountOwnerTypeRepository;
import com.github.donniexyz.demo.med.repository.AccountTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/accountType")
public class AccountTypeController {

    @Autowired
    private AccountTypeRepository accountTypeRepository;

    @Autowired
    private AccountOwnerTypeRepository accountOwnerTypeRepository;

    @GetMapping("/{typeCode}")
    public AccountType get(@PathVariable("typeCode") String typeCode) {
        return accountTypeRepository.findById(typeCode).orElseThrow();
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    public AccountType create(@RequestBody AccountType accountType) {
        if (accountTypeRepository.existsById(accountType.getTypeCode()))
            throw new RuntimeException("Record already exists");
        accountType.setApplicableForAccountOwnerTypes(
                CollectionUtils.isEmpty(accountType.getApplicableForAccountOwnerTypes()) ? null
                        : new HashSet<>(accountOwnerTypeRepository.findAllById(accountType.getApplicableForAccountOwnerTypes().stream().map(AccountOwnerType::getTypeCode).collect(Collectors.toSet()))
                ));
        return accountTypeRepository.save(accountType);
    }

    @PutMapping(path = "/{typeCode}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    public AccountType update(@PathVariable("typeCode") String typeCode,
                              @RequestBody AccountType accountType) {
        AccountType fetchedFromDb = accountTypeRepository.findById(typeCode).orElseThrow();
        // cannot change balanceSheetEntry
        if (!fetchedFromDb.getBalanceSheetEntry().equals(accountType.getBalanceSheetEntry()))
            throw new RuntimeException("Not allowed to change balanceSheetEntry");

        // ownerTypes
        Set<String> ownerTypeCodes = accountType.getApplicableForAccountOwnerTypes().stream().map(AccountOwnerType::getTypeCode).collect(Collectors.toSet());
        HashSet<AccountOwnerType> accountOwnerTypes = new HashSet<>(accountOwnerTypeRepository.findAllById(ownerTypeCodes));
        if (ownerTypeCodes.size() != accountOwnerTypes.size())
            throw new RuntimeException("Invalid ownerTypes");

        accountType.setApplicableForAccountOwnerTypes(accountOwnerTypes);

        // transactionTypes
        accountType.setApplicableDebitTransactionTypes(fetchedFromDb.getApplicableDebitTransactionTypes());
        accountType.setApplicableCreditTransactionTypes(fetchedFromDb.getApplicableCreditTransactionTypes());

        return accountTypeRepository.save(accountType);
    }

    @PatchMapping(path = "/{typeCode}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    public AccountType patch(@PathVariable("typeCode") String typeCode,
                             @RequestBody AccountType accountType) {
        AccountType fetchedFromDb = accountTypeRepository.findById(typeCode).orElseThrow();
        // cannot change balanceSheetEntry
        if (null != accountType.getBalanceSheetEntry() && !fetchedFromDb.getBalanceSheetEntry().equals(accountType.getBalanceSheetEntry()))
            throw new RuntimeException("Not allowed to change balanceSheetEntry");

        // ownerTypes
        if (null != accountType.getApplicableForAccountOwnerTypes()) {
            Set<String> ownerTypeCodes = accountType.getApplicableForAccountOwnerTypes().stream().map(AccountOwnerType::getTypeCode).collect(Collectors.toSet());
            HashSet<AccountOwnerType> accountOwnerTypes = new HashSet<>(accountOwnerTypeRepository.findAllById(ownerTypeCodes));
            if (ownerTypeCodes.size() != accountOwnerTypes.size())
                throw new RuntimeException("Invalid ownerTypes");

            accountType.setApplicableForAccountOwnerTypes(accountOwnerTypes);
        }

        // do not handle transactionTypes
        accountType.setApplicableDebitTransactionTypes(null);
        accountType.setApplicableCreditTransactionTypes(null);

        fetchedFromDb.copyFrom(accountType, true);
        return accountTypeRepository.save(fetchedFromDb);
    }

}
