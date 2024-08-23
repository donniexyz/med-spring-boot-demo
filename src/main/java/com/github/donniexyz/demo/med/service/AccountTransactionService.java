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

import com.github.donniexyz.demo.med.entity.AccountTransaction;
import com.github.donniexyz.demo.med.entity.AccountTransactionItem;
import com.github.donniexyz.demo.med.entity.AccountTransactionType;
import com.github.donniexyz.demo.med.entity.CashAccount;
import com.github.donniexyz.demo.med.enums.RecordStatusMajorEnum;
import com.github.donniexyz.demo.med.repository.AccountTransactionRepository;
import com.github.donniexyz.demo.med.repository.AccountTransactionTypeRepository;
import com.github.donniexyz.demo.med.repository.CashAccountRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AccountTransactionService {

    @Autowired
    private AccountTransactionRepository accountTransactionRepository;

    @Autowired
    private CashAccountRepository cashAccountRepository;

    @Autowired
    private AccountTransactionTypeRepository accountTransactionTypeRepository;

    public AccountTransaction get(Long id) {
        return accountTransactionRepository.findById(id).orElseThrow();
    }

    @Transactional
    public AccountTransaction create(AccountTransaction accountTransaction) {
        Long id = accountTransaction.getId();
        if (null != id && accountTransactionRepository.existsById(id))
            throw new RuntimeException("AccountTransaction already exists, id: " + id);

        if (accountTransaction.getTransactionAmount().isNegativeOrZero())
            throw new RuntimeException("Transaction amount must be positive");

        // make sure trx type exists
        AccountTransactionType transactionType = accountTransactionTypeRepository.findById(accountTransaction.getTypeCode()).orElseThrow();

        // get all accounts
        Set<Long> accountsFromTransaction = accountTransaction.getItems().stream().map(AccountTransactionItem::getAccountId).collect(Collectors.toSet());
        List<CashAccount> accounts = cashAccountRepository.findByIdInAndRecordStatusMajor(accountsFromTransaction, RecordStatusMajorEnum.ACTIVE.getFlag());
        if (accountsFromTransaction.size() > accounts.size()) {
            log.error("Transaction account not found! accountsFromTransaction: {}, existingActiveAccount: {} ",
                    accountsFromTransaction, accounts.stream().map(CashAccount::getId).toList());
        }

        AccountTransactionExecutionDataContainer accountTransactionExecution =
                new AccountTransactionExecutionDataContainer(transactionType, accounts, accountTransaction);

        accountTransactionExecution.validate();

        AccountTransaction prepared = accountTransactionExecution.closing();

        return accountTransactionRepository.save(prepared);
    }

}
