/*
 * MIT License
 *
 * Copyright (c) 2024 (https://github.com/donniexyz)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.github.donniexyz.demo.med.controller;

import com.github.donniexyz.demo.med.entity.AccountOwnerTypeApplicableToAccountType;
import com.github.donniexyz.demo.med.entity.AccountType;
import com.github.donniexyz.demo.med.lib.PatchMapper;
import com.github.donniexyz.demo.med.repository.AccountOwnerTypeRepository;
import com.github.donniexyz.demo.med.repository.AccountTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/accountType")
public class AccountTypeController {

    @Autowired
    private AccountTypeRepository accountTypeRepository;

    @Autowired
    private AccountOwnerTypeRepository accountOwnerTypeRepository;

    @GetMapping("/{typeCode}")
    @Transactional(readOnly = true)
    public AccountType get(@PathVariable("typeCode") String typeCode,
                           @RequestParam(required = false) Boolean cascade,
                           @RequestParam(required = false) List<String> relFields) {
        AccountType fetched = accountTypeRepository.findById(typeCode).orElseThrow();
        return null != relFields ? fetched.copy(relFields) : fetched.copy(cascade);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    public AccountType create(@RequestBody AccountType accountType) {
        if (accountTypeRepository.existsById(accountType.getTypeCode()))
            throw new RuntimeException("Record already exists");
        return accountTypeRepository.save(accountType);
    }

    @PutMapping(path = "/{typeCode}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    public AccountType update(@PathVariable("typeCode") String typeCode,
                              @RequestBody AccountType accountType) {

        Assert.isTrue(typeCode.equals(accountType.getTypeCode()), "Invalid request: id mismatch");
        Assert.notNull(accountType.getVersion(), "Invalid request: version field is null");

        AccountType fetchedFromDb = accountTypeRepository.findById(typeCode).orElseThrow();
        if (!fetchedFromDb.getVersion().equals(accountType.getVersion()))
            throw new RuntimeException("Optimistic locking check failed");
        // cannot change balanceSheetEntry
        if (!fetchedFromDb.getBalanceSheetEntry().equals(accountType.getBalanceSheetEntry()))
            throw new RuntimeException("Not allowed to change balanceSheetEntry");

        var tempApplicableOwnerTypes = accountType.getApplicableOwnerTypes();
        try {
            accountType.setApplicableTransactionTypes(fetchedFromDb.getApplicableTransactionTypes());
            return accountTypeRepository.save(accountType);
        } finally {
            accountType.setApplicableOwnerTypes(tempApplicableOwnerTypes);
        }
    }

    @PatchMapping(path = "/{typeCode}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    public AccountType patch(@PathVariable("typeCode") String typeCode,
                             @RequestBody AccountType accountType) {

        Assert.isTrue(typeCode.equals(accountType.getTypeCode()), "Invalid request: id mismatch");
        Assert.notNull(accountType.getVersion(), "Invalid request: version field is null");

        AccountType fetchedFromDb = accountTypeRepository.findById(typeCode).orElseThrow();
        if (!fetchedFromDb.getVersion().equals(accountType.getVersion()))
            throw new RuntimeException("Optimistic locking check failed");
        // cannot change balanceSheetEntry
        if (null != accountType.getBalanceSheetEntry() && !fetchedFromDb.getBalanceSheetEntry().equals(accountType.getBalanceSheetEntry()))
            throw new RuntimeException("Not allowed to change balanceSheetEntry");

        if (null != accountType.getApplicableOwnerTypes()) {
            final Function<AccountOwnerTypeApplicableToAccountType, String> accountOwnerTypeApplicableToAccountTypeTypeCodeExtractorFunction = AccountOwnerTypeApplicableToAccountType::getOwnerTypeCode;
            var applicableOwnerMapFromInput = accountType.getApplicableOwnerTypes().stream().collect(Collectors.toMap(accountOwnerTypeApplicableToAccountTypeTypeCodeExtractorFunction, Function.identity()));
            for (AccountOwnerTypeApplicableToAccountType fetchedApplicableOwnerType : fetchedFromDb.getApplicableOwnerTypes()) {
                if (applicableOwnerMapFromInput.containsKey(accountOwnerTypeApplicableToAccountTypeTypeCodeExtractorFunction.apply(fetchedApplicableOwnerType))) {
                    PatchMapper.INSTANCE.patch(applicableOwnerMapFromInput.remove(accountOwnerTypeApplicableToAccountTypeTypeCodeExtractorFunction.apply(fetchedApplicableOwnerType)), fetchedApplicableOwnerType);
                }
            }
            fetchedFromDb.getApplicableOwnerTypes().addAll(applicableOwnerMapFromInput.values());
        }

        var tempApplicableOwnerTypes = accountType.getApplicableOwnerTypes();
        var tempApplicableTransactionTypes = accountType.getApplicableTransactionTypes();
        try {
            accountType.setApplicableOwnerTypes(null);
            accountType.setApplicableTransactionTypes(null);
            PatchMapper.INSTANCE.patch(accountType, fetchedFromDb);
        } finally {
            accountType.setApplicableOwnerTypes(tempApplicableOwnerTypes);
            accountType.setApplicableTransactionTypes(tempApplicableTransactionTypes);
        }
        return accountTypeRepository.save(fetchedFromDb);
    }

}
