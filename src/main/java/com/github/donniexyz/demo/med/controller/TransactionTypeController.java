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

import com.github.donniexyz.demo.med.entity.AccountTransactionType;
import com.github.donniexyz.demo.med.entity.AccountTypeApplicableToTransactionType;
import com.github.donniexyz.demo.med.repository.AccountTransactionTypeRepository;
import com.github.donniexyz.demo.med.repository.AccountTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/accountTransactionType")
public class TransactionTypeController {

    @Autowired
    AccountTransactionTypeRepository transactionTypeRepository;

    @Autowired
    AccountTypeRepository accountTypeRepository;

    @GetMapping("/{typeCode}")
    @Transactional(readOnly = true)
    public AccountTransactionType get(@PathVariable("typeCode") String typeCode,
                                      @RequestParam(required = false) Boolean cascade,
                                      @RequestParam(required = false) List<String> relFields) {
        AccountTransactionType fetched = transactionTypeRepository.findById(typeCode).orElseThrow();
        return null != relFields ? fetched.copy(relFields) : fetched.copy(cascade);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    public AccountTransactionType create(@RequestBody AccountTransactionType accountTransactionType) {

        prepare(accountTransactionType);

        String typeCode = accountTransactionType.getTypeCode();
        if (transactionTypeRepository.existsById(typeCode))
            throw new RuntimeException("AccountTransactionType typeCode=" + typeCode + " already exists");

        accountTransactionType.validate();

        return transactionTypeRepository.save(accountTransactionType);
    }

    @PutMapping(path = "/{typeCode}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    public AccountTransactionType update(@PathVariable("typeCode") String typeCode, @RequestBody AccountTransactionType accountTransactionType) {

        Assert.isTrue(typeCode.equals(accountTransactionType.getTypeCode()), "Invalid request: id mismatch");
        Assert.notNull(accountTransactionType.getVersion(), "Invalid request: version field is null");

        prepare(accountTransactionType);

        AccountTransactionType fetchedFromDb = transactionTypeRepository.findById(typeCode).orElseThrow();
        if (!fetchedFromDb.getVersion().equals(accountTransactionType.getVersion()))
            throw new RuntimeException("Optimistic locking check failed");

        return transactionTypeRepository.save(accountTransactionType);
    }

    /**
     *
     * @param typeCode
     * @param accountTransactionType
     * <li>/applicableAccountTypes without .transactionTypeCode means new record</li>
     * <li>/applicableAccountTypes without .orderNumber means new record</li>
     * @return
     */
    @PatchMapping(path = "/{typeCode}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    public AccountTransactionType patch(@PathVariable("typeCode") String typeCode, @RequestBody AccountTransactionType accountTransactionType) {

        Assert.isTrue(typeCode.equals(accountTransactionType.getTypeCode()), "Invalid request: id mismatch");
        Assert.notNull(accountTransactionType.getVersion(), "Invalid request: version field is null");

        AccountTransactionType fetchedFromDb = transactionTypeRepository.findById(typeCode).orElseThrow();
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

    // --------------------------------------------------------------------------------------

    private static void patchFetchedChildListByInput(AccountTransactionType fetchedFromDb, AccountTransactionType accountTransactionTypeInput) {
        int currentOrder = 0;
        int debitCount = 0;
        int creditCount = 0;
        final var childListFromInput = accountTransactionTypeInput.getApplicableAccountTypes();
        final Function<AccountTypeApplicableToTransactionType, Integer> extractDominantRecordId = AccountTypeApplicableToTransactionType::getOrderNumber;
        var joinMapFromInput = childListFromInput.stream().filter(k -> null != extractDominantRecordId.apply(k)).collect(Collectors.toMap(extractDominantRecordId, Function.identity()));
        for (var fetchedDominant : fetchedFromDb.getApplicableAccountTypes()) {
            if (joinMapFromInput.containsKey(extractDominantRecordId.apply(fetchedDominant))) {

                var joinFromInput = joinMapFromInput.remove(extractDominantRecordId.apply(fetchedDominant));
                if (!fetchedDominant.getVersion().equals(joinFromInput.getVersion()))
                    throw new RuntimeException("Optimistic locking check failed");
                fetchedDominant.copyFrom(joinFromInput.copy(false), true);

                Assert.isTrue(fetchedDominant.getMaxOccurrences() >= fetchedDominant.getMinOccurrences(),
                        "Invalid value: MaxOccurrences < MinOccurrences");

                if (fetchedDominant.getOrderNumber() < currentOrder) fetchedDominant.setOrderNumber(currentOrder);
            }
            currentOrder = fetchedDominant.getOrderNumber() + fetchedDominant.getMaxOccurrences();
            switch (fetchedDominant.getDebitCredit()) {
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

}
