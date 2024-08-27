package com.github.donniexyz.demo.med.service;

import com.github.donniexyz.demo.med.entity.*;
import com.github.donniexyz.demo.med.enums.DebitCreditEnum;
import com.github.donniexyz.demo.med.exception.CashAccountErrorCode;
import com.github.donniexyz.demo.med.exception.CashAccountException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.util.Assert;

import javax.money.MonetaryAmount;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.github.donniexyz.demo.med.lib.CashAccountConstants.MA.ZERO_USD;

@Getter
@Slf4j
class AccountTransactionExecutionDataContainer {
    private final AccountTransactionType transactionType;
    private final AccountTransaction accountTransaction;
    private int expectedNumberOfItems;

    private MonetaryAmount totalDebit = ZERO_USD;
    private MonetaryAmount totalCredit = ZERO_USD;
    private int countOfDebit = 0;
    private int countOfCredit = 0;
    // key is accId
    private final Map<Long, AccountTransactionExecutionItem> accountMap = new HashMap<>();
    // key is debitCredit.getSign() + "|" + accountTypeCode     // generateApplicableMapKey()
    private final Map<String, AccountTypeApplicableToTransactionType> settingsApplicableMap;
    // key is orderNumber aka itemIndexNumber
    private final Map<Integer, AccountTransactionItem> preparedAccountTransactionItemMap = new HashMap<>();

    public AccountTransactionExecutionDataContainer(AccountTransactionType transactionType, List<CashAccount> accounts,
                                                    AccountTransaction copyFromThisAccountTransaction) {

        this.transactionType = transactionType;
        this.settingsApplicableMap = this.transactionType.getApplicableAccountTypes().stream()
                .collect(Collectors.toMap(
                        k -> generateApplicableMapKey(k.getDebitCredit(), k.getAccountTypeCode()),
                        Function.identity()
                ));

        if (null != accounts) setAccountsFromDb(accounts);

        if (null != copyFromThisAccountTransaction) {
            if (null != copyFromThisAccountTransaction.getType()) {
                Assert.isTrue(transactionType.getTypeCode().equals(copyFromThisAccountTransaction.getTypeCode()),
                        "accountTransactionType typeCode mismatch");
            }
            this.accountTransaction = copyFromThisAccountTransaction.copy(false);
            this.expectedNumberOfItems = copyFromThisAccountTransaction.getItems().size();
            for (AccountTransactionItem item : Optional.ofNullable(copyFromThisAccountTransaction.getItems()).orElse(Collections.emptyList())) {
                this.add(item);
            }

        } else {

            this.accountTransaction = new AccountTransaction();
        }

        this.accountTransaction.setType(transactionType);

        if (null == accountTransaction.getTransactionDate()) {
            accountTransaction.setTransactionDate(LocalDateTime.now());
        }

    }

    @NotNull
    private static String generateApplicableMapKey(DebitCreditEnum debitCredit, String accountTypeCode) {
        return debitCredit.getSign() + "|" + accountTypeCode;
    }

    public void setAccountsFromDb(List<CashAccount> accounts) {
        for (CashAccount account : accounts) {
            accountMap.put(account.getId(), new AccountTransactionExecutionItem(account));
        }
    }

    public void add(AccountTransactionItem accountTransactionItem) {
        add(accountTransactionItem, null);
    }

    public void add(AccountTransactionItem accountTransactionItem, CashAccount account) {

        CashAccount effectiveAccount;
        if (null != account && Boolean.TRUE.equals(account.getRetrievedFromDb())) effectiveAccount = account;
        else if (null != accountTransactionItem.getAccount() && Boolean.TRUE.equals(accountTransactionItem.getAccount().getRetrievedFromDb()))
            effectiveAccount = accountTransactionItem.getAccount();
        else
            effectiveAccount = null;

        AccountTransactionItem effectiveTransactionItem = accountTransactionItem.withFlippedRetrievedFromDb()
                .setVersion(null)
                .setType(transactionType)
                .setAccountTransaction(this.accountTransaction);

        AccountTransactionExecutionItem accountTransactionExecutionItem = getAccountTransactionExecutionItem(effectiveTransactionItem, effectiveAccount);
        effectiveAccount = accountTransactionExecutionItem.account;
        effectiveTransactionItem.setAccount(effectiveAccount);

        String applicableMapKey = generateApplicableMapKey(effectiveTransactionItem.getDebitCredit(), effectiveAccount.getAccountType().getTypeCode());
        AccountTypeApplicableToTransactionType applicableToTransactionType = settingsApplicableMap.get(applicableMapKey);
        if (null == applicableToTransactionType) {
            log.error("[add] Invalid transaction item, key: {}", applicableMapKey);
            throw new CashAccountException(CashAccountErrorCode.TRANSACTION_ITEM_INVALID);
        }

        if (null == effectiveTransactionItem.getOrderNumber()) {
            effectiveTransactionItem.setOrderNumber(applicableToTransactionType.getOrderNumber() +
                    (DebitCreditEnum.DEBIT.equals(effectiveTransactionItem.getDebitCredit())
                            ? accountTransactionExecutionItem.debitEntryCount
                            : accountTransactionExecutionItem.creditEntryCount));
        }

        if ((effectiveTransactionItem.getOrderNumber() < applicableToTransactionType.getOrderNumber() + applicableToTransactionType.getMinOccurrences() - 1)
                || (effectiveTransactionItem.getOrderNumber() > applicableToTransactionType.getOrderNumber() + applicableToTransactionType.getMaxOccurrences())) {
            log.error("[add] Invalid item index, min {} <= index {} <= max {}",
                    applicableToTransactionType.getOrderNumber() + applicableToTransactionType.getMinOccurrences() - 1,
                    effectiveTransactionItem.getOrderNumber(),
                    applicableToTransactionType.getOrderNumber() + applicableToTransactionType.getMaxOccurrences());
            throw new CashAccountException(CashAccountErrorCode.TRANSACTION_ITEM_INVALID);
        }

        if (preparedAccountTransactionItemMap.containsKey(effectiveTransactionItem.getOrderNumber())) {
            log.error("[add] Duplicated item index, index: {}", applicableToTransactionType.getOrderNumber());
            throw new CashAccountException(CashAccountErrorCode.TRANSACTION_ITEM_INVALID);
        }
        preparedAccountTransactionItemMap.put(effectiveTransactionItem.getOrderNumber(), effectiveTransactionItem);
        accountTransactionExecutionItem.addTransactionItem(effectiveTransactionItem);
        addIntoTotalBalance(effectiveTransactionItem.getDebitCredit(), effectiveTransactionItem.getTransactionAmount());
    }

    @NotNull
    private AccountTransactionExecutionItem getAccountTransactionExecutionItem(AccountTransactionItem accountTransactionItem, CashAccount effectiveAccount) {
        AccountTransactionExecutionItem accountTransactionExecutionItem;
        if (null == effectiveAccount) {
            accountTransactionExecutionItem = accountMap.get(accountTransactionItem.getAccountId());
            if (null == accountTransactionExecutionItem) {
                log.error("[add] accountTransactionItem account not found: {}", accountTransactionItem.getAccountId());
                throw new CashAccountException(CashAccountErrorCode.TRANSACTION_ITEM_INVALID);
            }
        } else {
            accountTransactionExecutionItem = accountMap.get(effectiveAccount.getId());
            if (null == accountTransactionExecutionItem) {
                accountTransactionExecutionItem = new AccountTransactionExecutionItem(effectiveAccount);
                accountMap.put(effectiveAccount.getId(), accountTransactionExecutionItem);
            }
        }
        return accountTransactionExecutionItem;
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
        accountTransaction.setItems(preparedAccountTransactionItemMap.values().stream().sorted(Comparator.comparingInt(AccountTransactionItem::getOrderNumber)).toList());
        return accountTransaction;
    }

    void validateSummary() {
        if (countOfCredit != countOfDebit
                || !totalDebit.isEqualTo(totalCredit)) {
            log.error("[validateSummary] TransactionExecutionVO: {}", this);
            throw new CashAccountException(CashAccountErrorCode.TRANSACTION_VALIDATION_FAILED);
        }
        if (expectedNumberOfItems != preparedAccountTransactionItemMap.size()) {
            log.error("[validateSummary] invalid transaction items, expected: {}, prepared: {}",
                    expectedNumberOfItems, preparedAccountTransactionItemMap.size());
            throw new CashAccountException(CashAccountErrorCode.TRANSACTION_VALIDATION_FAILED);
        }
    }

    void validatePostExecutionItems() {
        // validate each accounts
        accountMap.forEach((accountId, accountCalculation) -> {
            accountCalculation.validate();
        });
    }

    public void sortTransactionItems() {
        accountTransaction.getItems().sort(Comparator.comparingInt(AccountTransactionItem::getOrderNumber));
    }

    @Getter
    static class AccountTransactionExecutionItem {
        final CashAccount account;
        final MonetaryAmount initialBalance;
        MonetaryAmount netTransactionAmount = ZERO_USD;
        MonetaryAmount closingBalance;
        int debitEntryCount;
        int creditEntryCount;

        public AccountTransactionExecutionItem(CashAccount account) {
            this.account = account;
            this.initialBalance = account.getAccountBalance();
        }

        public void addTransactionItem(AccountTransactionItem accountTransactionItem) {
            if (DebitCreditEnum.DEBIT.equals(accountTransactionItem.getDebitCredit())) this.debitEntryCount++;
            else this.creditEntryCount++;

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
}
