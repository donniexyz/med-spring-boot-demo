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

import com.github.donniexyz.demo.med.entity.*;
import com.github.donniexyz.demo.med.enums.DebitCreditEnum;
import com.github.donniexyz.demo.med.enums.RecordStatusMajorEnum;
import com.github.donniexyz.demo.med.exception.CashAccountErrorCode;
import com.github.donniexyz.demo.med.exception.CashAccountException;
import com.github.donniexyz.demo.med.exception.ErrorDetail;
import com.github.donniexyz.demo.med.repository.AccountTransactionRepository;
import com.github.donniexyz.demo.med.repository.AccountTransactionTypeRepository;
import com.github.donniexyz.demo.med.repository.CashAccountRepository;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.money.MonetaryAmount;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.github.donniexyz.demo.med.lib.CashAccountConstants.MA.ZERO_USD;

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
        AccountTransactionType transactionType = accountTransactionTypeRepository.findById(accountTransaction.getType().getTypeCode()).orElseThrow();

        // get all accounts
        Set<Long> accountsFromTransaction = accountTransaction.getItems().stream().map(AccountTransactionItem::getAccountId).collect(Collectors.toSet());
        List<CashAccount> accounts = cashAccountRepository.findByIdInAndRecordStatusMajor(accountsFromTransaction, RecordStatusMajorEnum.ACTIVE.getFlag());
        if (accountsFromTransaction.size() > accounts.size()) {
            log.error("Transaction account not found! accountsFromTransaction: {}, existingActiveAccount: {} ",
                    accountsFromTransaction, accounts.stream().map(CashAccount::getId).toList());
        }

        AccountTransactionExecution accountTransactionExecution = new AccountTransactionExecution(accountTransaction);
        accountTransactionExecution.setTransactionType(transactionType);
        accountTransactionExecution.setAccountsFromDb(accounts);

        accountTransaction.getItems().sort(Comparator.comparingInt(AccountTransactionItem::getOrder));

        Map<Integer, AccountTransactionItem> accountMapFromTransaction = new HashMap<>();
        int smallestOrder = Integer.MAX_VALUE;
        int largestOrder = Integer.MIN_VALUE;
        for (AccountTransactionItem accountFromInput : accountTransaction.getItems()) {
            Integer order = accountFromInput.getOrder();
            if (accountMapFromTransaction.containsKey(order)) {
                throw new CashAccountException(CashAccountErrorCode.TRANSACTION_ITEM_DUPLICATE)
                        .addErrorDetail(ErrorDetail.builder().source("INPUT").value(order).notes("DUPLICATE").build())
                        ;
            }
            smallestOrder = Math.min(smallestOrder, order);
            largestOrder = Math.max(largestOrder, order);
            accountMapFromTransaction.put(order, accountFromInput);
        }

        // Process AccountTransactionItem per applicable [.order .. .maxOccurrences].
        // Anything outside that range will be ignored by this for-loop
        // and in the end compare the number of items between transaction items and execution items. Any diff means invalid transaction input.
        for (AccountTypeApplicableToTransactionType applicableAccountType : transactionType.getApplicableAccountTypes()) {
            // if settings min > max then error
            if (null != applicableAccountType.getMaxOccurrences() && (applicableAccountType.getMinOccurrences() > applicableAccountType.getMaxOccurrences())) {
                throw new CashAccountException(CashAccountErrorCode.TRANSACTION_SETTINGS_INVALID);
            }

            if (applicableAccountType.getMinOccurrences() == 0) {
                // if optional and not passed then continue
                if (!accountMapFromTransaction.containsKey(applicableAccountType.getOrder())) continue;
                // max = 0 but item is passed
                if (null == applicableAccountType.getMaxOccurrences() || applicableAccountType.getMaxOccurrences() == 0) {
                    log.error("[create] Invalid trx item: settings maxOccurrences = 0, i: {}", applicableAccountType.getOrder());
                    throw new CashAccountException(CashAccountErrorCode.TRANSACTION_ITEM_INVALID);
                }
                // if optional and passed then validate 1 item
                AccountTransactionItem accountFromMap = accountMapFromTransaction.get(applicableAccountType.getOrder());
                validateTransactionItem(applicableAccountType, accountFromMap, 0);

                accountTransactionExecution.add(accountFromMap.copy(false));

            } else {    // not optional, validate minimum items

                // process transaction item up until .minOccurrences
                for (int i = 0; i < applicableAccountType.getMinOccurrences(); i++) {
                    AccountTransactionItem accountFromMap = accountMapFromTransaction.get(applicableAccountType.getOrder() + i);
                    validateTransactionItem(applicableAccountType, accountFromMap, i);

                    accountTransactionExecution.add(accountFromMap.copy(false));
                }
            }

            // process transaction item up to .maxOccurrences
            if (null != applicableAccountType.getMaxOccurrences() && applicableAccountType.getMaxOccurrences() > applicableAccountType.getMinOccurrences()) {
                for (int i = applicableAccountType.getMinOccurrences(); i < applicableAccountType.getMaxOccurrences(); i++) {
                    AccountTransactionItem accountFromMap = accountMapFromTransaction.get(applicableAccountType.getOrder() + i);
                    if (null != accountFromMap) {
                        validateTransactionItem(applicableAccountType, accountFromMap, i);

                        accountTransactionExecution.add(accountFromMap.copy(false));
                    }
                }
            }
        }

        accountTransactionExecution.validate();

        AccountTransaction prepared = accountTransactionExecution.closing();

        return accountTransactionRepository.save(prepared);
    }

    private static void validateTransactionItem(AccountTypeApplicableToTransactionType applicableAccountType, AccountTransactionItem accountFromMap, int counter) {
        if (null == accountFromMap) {
            log.error("[validateTransactionItem] Failed. From settings: order: {}, minOccurrences: {}. Counter: {}",
                    applicableAccountType.getOrder(), applicableAccountType.getMinOccurrences(), counter);
            throw new CashAccountException(CashAccountErrorCode.TRANSACTION_ITEM_INVALID);
        }

        if (!accountFromMap.getAccount().getAccountType().getTypeCode().equals(applicableAccountType.getAccountTypeCode())) {
            log.error("[validateTransactionItem] Failed: order: {}, accNo: {}, typeCode: {}. From settings: order: {}, typeCode:{}. Counter: {}",
                    accountFromMap.getOrder(), accountFromMap.getAccountId(), accountFromMap.getAccount().getAccountType().getTypeCode(),
                    applicableAccountType.getOrder(), applicableAccountType.getAccountTypeCode(), counter);
            throw new CashAccountException(CashAccountErrorCode.TRANSACTION_ITEM_INVALID);
        } else if (!accountFromMap.getDebitCredit().equals(applicableAccountType.getDebitCredit())) {
            log.error("[validateTransactionItem] Failed: order: {}, accId: {}, debitCredit: {}. From settings: order: {}, debitCredit: {}. Counter: {}",
                    accountFromMap.getOrder(), accountFromMap.getAccountId(), accountFromMap.getDebitCredit(),
                    applicableAccountType.getOrder(), applicableAccountType.getDebitCredit(), counter);
            throw new CashAccountException(CashAccountErrorCode.TRANSACTION_ITEM_INVALID);
        }
    }

    @Getter
    static class AccountTransactionExecutionItem {
        final CashAccount account;
        final MonetaryAmount initialBalance;
        MonetaryAmount netTransactionAmount = ZERO_USD;
        MonetaryAmount closingBalance;
        int entryCount;

        public AccountTransactionExecutionItem(CashAccount account) {
            this.account = account;
            this.initialBalance = account.getAccountBalance();
        }

        public void add(AccountTransactionItem accountTransactionItem) {
            this.entryCount++;
            this.netTransactionAmount = account.getAccountType().getBalanceSheetEntry().isToBeIncreased(accountTransactionItem.getDebitCredit())
                    ? this.netTransactionAmount.add(accountTransactionItem.getTransactionAmount())
                    : this.netTransactionAmount.subtract(accountTransactionItem.getTransactionAmount());
        }

        public void validate() {
            this.closingBalance = this.initialBalance.add(this.netTransactionAmount);
            // closing balance check
            if (null != account.getAccountType().getMinimumBalance()
                    && account.getAccountType().getMinimumBalance().isGreaterThan(closingBalance)) {
                log.error("[validatePostExecutionDetail] Insufficient balance, accountId: {}", account.getId());
                throw new CashAccountException(CashAccountErrorCode.INSUFFICIENT_BALANCE);
            }
        }

    }

    @Getter
    static class AccountTransactionExecution {
        private final AccountTransaction accountTransaction;
        private final int expectedNumberOfItems;
        private AccountTransactionType transactionType;

        private MonetaryAmount totalDebit = ZERO_USD;
        private MonetaryAmount totalCredit = ZERO_USD;
        private int countOfDebit = 0;
        private int countOfCredit = 0;
        private Map<Long, AccountTransactionExecutionItem> accountMap;
        private List<AccountTransactionItem> preparedAccountTransactionItem = new ArrayList<>();

        public AccountTransactionExecution(AccountTransaction copyFromThisAccountTransaction) {
            this.accountTransaction = copyFromThisAccountTransaction.copy(false);
            this.expectedNumberOfItems = copyFromThisAccountTransaction.getItems().size();
            this.accountTransaction.setItems(preparedAccountTransactionItem);
            if (null == accountTransaction.getTransactionDate()) accountTransaction.setTransactionDate(LocalDateTime.now());
        }

        public void setTransactionType(AccountTransactionType transactionType) {
            this.accountTransaction.setType(transactionType);
            this.transactionType = transactionType;
        }

        public void setAccountsFromDb(List<CashAccount> accounts) {
            accountMap = accounts.stream().collect(Collectors.toMap(CashAccount::getId, AccountTransactionExecutionItem::new));
        }

        public void add(AccountTransactionItem accountTransactionItem) {
            addIntoTotalBalance(accountTransactionItem.getDebitCredit(), accountTransactionItem.getTransactionAmount());

            AccountTransactionExecutionItem accountTransactionExecutionItem = accountMap.get(accountTransactionItem.getAccountId());
            accountTransactionExecutionItem.add(accountTransactionItem);

            preparedAccountTransactionItem.add(
                    accountTransactionItem
                            .withRetrievedFromDb(false)
                            .setVersion(null)
                            .setAccount(accountTransactionExecutionItem.getAccount())
                            .setType(transactionType)
                            .setAccountTransaction(this.accountTransaction)
            );
        }

        void addIntoTotalBalance(DebitCreditEnum debitCreditEnum, MonetaryAmount monetaryAmount) {
            if (DebitCreditEnum.DEBIT.equals(debitCreditEnum)) {
                if (!monetaryAmount.getCurrency().getCurrencyCode().equals(totalDebit.getCurrency().getCurrencyCode())) {
                    log.error("[add] Currency mismatch: {} {}", monetaryAmount.getCurrency().getCurrencyCode(), totalDebit.getCurrency().getCurrencyCode());
                    throw new CashAccountException(CashAccountErrorCode.TRANSACTION_CURRENCY_MISMATCH);
                }
                totalDebit = totalDebit.add(monetaryAmount);
                countOfDebit++;
            } else if (DebitCreditEnum.CREDIT.equals(debitCreditEnum)) {
                if (!monetaryAmount.getCurrency().getCurrencyCode().equals(totalCredit.getCurrency().getCurrencyCode())) {
                    log.error("[add] Currency mismatch: {} {}", monetaryAmount.getCurrency().getCurrencyCode(), totalCredit.getCurrency().getCurrencyCode());
                    throw new CashAccountException(CashAccountErrorCode.TRANSACTION_CURRENCY_MISMATCH);
                }
                totalCredit = totalCredit.add(monetaryAmount);
                countOfCredit++;
            }
        }

        public void validate() {
            validateSummary();
            validatePostExecutionItems();
        }

        public AccountTransaction closing() {
            // reflect transaction to each accounts
            accountMap.forEach((accountId, accountCalculation) -> {
                CashAccount account = accountCalculation.getAccount();
                account.setAccountBalance(accountCalculation.getClosingBalance());
                if (null == account.getLastTransactionDate() || account.getLastTransactionDate().isBefore(accountTransaction.getTransactionDate()))
                    account.setLastTransactionDate(accountTransaction.getTransactionDate());
            });

            return accountTransaction;
        }

        void validateSummary() {
            if (countOfCredit != countOfDebit
                    || !totalDebit.isEqualTo(totalCredit)) {
                log.error("[validateSummary] TransactionExecutionVO: {}", this);
                throw new CashAccountException(CashAccountErrorCode.TRANSACTION_VALIDATION_FAILED);
            }
            if (expectedNumberOfItems != preparedAccountTransactionItem.size()) {
                log.error("[validateSummary] invalid transaction items, expected: {}, prepared: {}",
                        expectedNumberOfItems, preparedAccountTransactionItem.size());
                throw new CashAccountException(CashAccountErrorCode.TRANSACTION_VALIDATION_FAILED);
            }
        }

        void validatePostExecutionItems() {
            // validate each accounts
            accountMap.forEach((accountId, accountCalculation) -> {
                accountCalculation.validate();
            });
        }
    }
}
