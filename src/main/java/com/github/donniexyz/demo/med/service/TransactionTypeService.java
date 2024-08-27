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

import com.github.donniexyz.demo.med.entity.AccountTransactionType;
import com.github.donniexyz.demo.med.entity.AccountTypeApplicableToTransactionType;
import com.github.donniexyz.demo.med.repository.AccountTransactionTypeRepository;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class TransactionTypeService {

    @Autowired
    AccountTransactionTypeRepository transactionTypeRepository;

    @NotNull
    public Optional<AccountTransactionType> findById(String typeCode) {
        return transactionTypeRepository.findById(typeCode);
    }

    @Transactional
    public AccountTransactionType create(AccountTransactionType accountTransactionType) {

        Assert.isNull(accountTransactionType.getVersion(), "Invalid input: version field is not null");

        String typeCode = accountTransactionType.getTypeCode();
        if (transactionTypeRepository.existsById(typeCode))
            throw new RuntimeException("AccountTransactionType typeCode=" + typeCode + " already exists");

        prepare(accountTransactionType);

        accountTransactionType.validate();

        return transactionTypeRepository.save(accountTransactionType);
    }

    @Transactional
    public AccountTransactionType update(AccountTransactionType accountTransactionType) {

        Assert.notNull(accountTransactionType.getVersion(), "Invalid request: version field is null");

        prepare(accountTransactionType);

        return transactionTypeRepository.save(accountTransactionType);
    }

    @Transactional
    public AccountTransactionType patch(AccountTransactionType accountTransactionType) {

        Assert.notNull(accountTransactionType.getVersion(), "Invalid request: version field is null");

        AccountTransactionType fetchedFromDb = transactionTypeRepository.findById(accountTransactionType.getTypeCode()).orElseThrow();
        if (!fetchedFromDb.getVersion().equals(accountTransactionType.getVersion()))
            throw new RuntimeException("Optimistic locking check failed");

        // patch @OneToMany field
        if (null != accountTransactionType.getApplicableAccountTypes()) {
            patchFetchedChildListByInput(fetchedFromDb, accountTransactionType);
        }

        List<AccountTypeApplicableToTransactionType> tempChild = accountTransactionType.getApplicableAccountTypes();
        try {
            accountTransactionType.setApplicableAccountTypes(null);
            fetchedFromDb.copyFrom(accountTransactionType.copy(false), true);
        } finally {
            accountTransactionType.setApplicableAccountTypes(tempChild);
        }
        return transactionTypeRepository.save(fetchedFromDb);
    }


    // ------------------------------------------------------------------------------------------------

    private static void prepare(AccountTransactionType accountTransactionType) {
        // help fill in 'order' if it is null
        int currentOrder = 0;
        int debitCount = 0;
        int creditCount = 0;
        for (AccountTypeApplicableToTransactionType applicableAccountType : accountTransactionType.getApplicableAccountTypes()) {

            Assert.notNull(applicableAccountType.getDebitCredit(), "Invalid value: debitCredit cannot be null");
            switch (applicableAccountType.getDebitCredit()) {
                case DEBIT -> debitCount++;
                case CREDIT -> creditCount++;
            }
            currentOrder = calculateCurrentOrder(applicableAccountType, currentOrder, true);
        }
        Assert.isTrue(debitCount > 0 && creditCount > 0, "Invalid transaction: must have at least 1 debit 1 credit");
        accountTransactionType.setSingleDebit(debitCount == 1);
        accountTransactionType.setSingleCredit(creditCount == 1);
    }

    private static int calculateCurrentOrder(AccountTypeApplicableToTransactionType applicableAccountType,
                                             int currentOrder, boolean strictOrderCheck) {
        if (null == applicableAccountType.getMinOccurrences()) applicableAccountType.setMinOccurrences(1);
        if (null == applicableAccountType.getMaxOccurrences())
            applicableAccountType.setMaxOccurrences(applicableAccountType.getMinOccurrences() == 0 ? 1 : applicableAccountType.getMinOccurrences());

        Assert.isTrue(applicableAccountType.getMaxOccurrences() >= applicableAccountType.getMinOccurrences(),
                "Invalid value: MaxOccurrences < MinOccurrences");

        if (null == applicableAccountType.getOrderNumber()) {
            applicableAccountType.setOrderNumber(currentOrder);
        } else {
            if (strictOrderCheck) {
                Assert.isTrue(applicableAccountType.getOrderNumber() >= currentOrder,
                        "Invalid value: order < currentOrder");
                currentOrder = applicableAccountType.getOrderNumber();
            } else {
                if (currentOrder > applicableAccountType.getOrderNumber())
                    applicableAccountType.setOrderNumber(currentOrder);
                else
                    currentOrder = applicableAccountType.getOrderNumber();
            }
        }
        currentOrder += applicableAccountType.getMaxOccurrences();

        return currentOrder;
    }

    private static void patchFetchedChildListByInput(AccountTransactionType fetchedFromDb, AccountTransactionType accountTransactionTypeInput) {
        int currentOrder = 0;
        int debitCount = 0;
        int creditCount = 0;
        final var childListFromInput = accountTransactionTypeInput.getApplicableAccountTypes();
        final Function<AccountTypeApplicableToTransactionType, Integer> extractDominantRecordId = AccountTypeApplicableToTransactionType::getOrderNumber;
        var joinMapFromInput = childListFromInput.stream().filter(k -> null != extractDominantRecordId.apply(k)).collect(Collectors.toMap(extractDominantRecordId, Function.identity()));
        for (var fetchedApplicableAccountType : fetchedFromDb.getApplicableAccountTypes()) {
            if (joinMapFromInput.containsKey(extractDominantRecordId.apply(fetchedApplicableAccountType))) {

                var joinFromInput = joinMapFromInput.remove(extractDominantRecordId.apply(fetchedApplicableAccountType));
                if (!fetchedApplicableAccountType.getVersion().equals(joinFromInput.getVersion()))
                    throw new RuntimeException("Optimistic locking check failed");
                fetchedApplicableAccountType.copyFrom(joinFromInput.copy(false), true);

                Assert.isTrue(fetchedApplicableAccountType.getMaxOccurrences() >= fetchedApplicableAccountType.getMinOccurrences(),
                        "Invalid value: MaxOccurrences < MinOccurrences");

                if (fetchedApplicableAccountType.getOrderNumber() < currentOrder) fetchedApplicableAccountType.setOrderNumber(currentOrder);
            }
            currentOrder = fetchedApplicableAccountType.getOrderNumber() + fetchedApplicableAccountType.getMaxOccurrences();
            switch (fetchedApplicableAccountType.getDebitCredit()) {
                case DEBIT -> debitCount++;
                case CREDIT -> creditCount++;
            }
        }
        Collection<AccountTypeApplicableToTransactionType> toBeAdded = new ArrayList<>();
        // add remaining child that has orderNumber without match with fetched child
        toBeAdded.addAll(joinMapFromInput.values());
        // add child that does not have orderNumber
        toBeAdded.addAll(childListFromInput.stream().filter(k -> null == extractDominantRecordId.apply(k)).toList());

        // calculate orderNumber for toBeAdded child
        for (var toBeAddedWithOrderNumber : toBeAdded) {
            Assert.notNull(toBeAddedWithOrderNumber.getDebitCredit(), "Invalid value: debitCredit cannot be null");
            switch (toBeAddedWithOrderNumber.getDebitCredit()) {
                case DEBIT -> debitCount++;
                case CREDIT -> creditCount++;
            }
            currentOrder = calculateCurrentOrder(toBeAddedWithOrderNumber, currentOrder, false);
        }

        Assert.isTrue(debitCount > 0 && creditCount > 0, "Invalid transaction: must have at least 1 debit 1 credit");
        accountTransactionTypeInput.setSingleDebit(debitCount == 1);
        accountTransactionTypeInput.setSingleCredit(creditCount == 1);

        fetchedFromDb.getApplicableAccountTypes().addAll(toBeAdded);
        fetchedFromDb.getApplicableAccountTypes().sort(Comparator.comparingInt(AccountTypeApplicableToTransactionType::getOrderNumber));
    }
}
