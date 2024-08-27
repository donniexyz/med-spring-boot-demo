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
package com.github.donniexyz.demo.med.service;

import com.github.donniexyz.demo.med.entity.AccountOwnerTypeApplicableToAccountType;
import com.github.donniexyz.demo.med.entity.AccountType;
import com.github.donniexyz.demo.med.repository.AccountTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class AccountTypeService {

    @Autowired
    private AccountTypeRepository accountTypeRepository;

    public Optional<AccountType> findById(String typeCode) {
        return accountTypeRepository.findById(typeCode);
    }

    @Transactional
    public AccountType create(AccountType accountType) {

        Assert.isNull(accountType.getVersion(), "Invalid input: version field is not null");

        if (accountTypeRepository.existsById(accountType.getTypeCode()))
            throw new RuntimeException("Record already exists");

        return accountTypeRepository.save(accountType);
    }

    @Transactional
    public AccountType update(AccountType accountType) {

        Assert.notNull(accountType.getVersion(), "Invalid request: version field is null");

        AccountType fetchedFromDb = accountTypeRepository.findById(accountType.getTypeCode()).orElseThrow();
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

    @Transactional
    public AccountType patch(AccountType accountType) {

        Assert.notNull(accountType.getVersion(), "Invalid request: version field is null");

        AccountType fetchedFromDb = accountTypeRepository.findById(accountType.getTypeCode()).orElseThrow();
        if (!fetchedFromDb.getVersion().equals(accountType.getVersion()))
            throw new RuntimeException("Optimistic locking check failed");
        // cannot change balanceSheetEntry
        if (null != accountType.getBalanceSheetEntry() && !fetchedFromDb.getBalanceSheetEntry().equals(accountType.getBalanceSheetEntry()))
            throw new RuntimeException("Not allowed to change balanceSheetEntry");

        // patch @OneToMany field
        if (null != accountType.getApplicableOwnerTypes()) {
            final Function<AccountOwnerTypeApplicableToAccountType, String> accountOwnerTypeApplicableToAccountTypeTypeCodeExtractorFunction = AccountOwnerTypeApplicableToAccountType::getOwnerTypeCode;
            var applicableOwnerMapFromInput = accountType.getApplicableOwnerTypes().stream().collect(Collectors.toMap(accountOwnerTypeApplicableToAccountTypeTypeCodeExtractorFunction, Function.identity()));
            for (AccountOwnerTypeApplicableToAccountType fetchedApplicableOwnerType : fetchedFromDb.getApplicableOwnerTypes()) {
                if (applicableOwnerMapFromInput.containsKey(accountOwnerTypeApplicableToAccountTypeTypeCodeExtractorFunction.apply(fetchedApplicableOwnerType))) {
                    fetchedApplicableOwnerType.copyFrom(applicableOwnerMapFromInput.remove(accountOwnerTypeApplicableToAccountTypeTypeCodeExtractorFunction.apply(fetchedApplicableOwnerType)).copy(false), true);
                }
            }
            fetchedFromDb.getApplicableOwnerTypes().addAll(applicableOwnerMapFromInput.values());
        }

        fetchedFromDb.copyFrom(accountType.copy(false), true);
        return accountTypeRepository.save(fetchedFromDb);
    }

}
