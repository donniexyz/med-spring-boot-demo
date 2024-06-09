package com.github.donniexyz.demo.med.service;

import com.github.donniexyz.demo.med.entity.AccountTransaction;
import com.github.donniexyz.demo.med.entity.AccountTransactionType;
import com.github.donniexyz.demo.med.entity.AccountType;
import com.github.donniexyz.demo.med.entity.CashAccount;
import com.github.donniexyz.demo.med.lib.CashAccountUtilities;
import com.github.donniexyz.demo.med.repository.AccountTransactionRepository;
import com.github.donniexyz.demo.med.repository.AccountTransactionTypeRepository;
import com.github.donniexyz.demo.med.repository.CashAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.money.MonetaryAmount;
import java.time.LocalDateTime;

@Service
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

        LocalDateTime transactionDate = null != accountTransaction.getTransactionDate() ? accountTransaction.getTransactionDate() : LocalDateTime.now();

        if (accountTransaction.getTransactionAmount().isNegativeOrZero())
            throw new RuntimeException("Transaction amount must be positive");

        // make sure trx type exists
        AccountTransactionType transactionType = accountTransactionTypeRepository.findById(accountTransaction.getType().getTypeCode()).orElseThrow();

        // make sure debitAccount is allowed
        CashAccount debitAccount = cashAccountRepository.findById(accountTransaction.getDebitAccount().getId()).orElseThrow();
        AccountType debitAccountType = debitAccount.getAccountType();
        transactionType.getApplicableDebitAccountTypes().stream().filter(at -> at.getTypeCode().equals(debitAccountType.getTypeCode())).findFirst()
                .orElseThrow(() -> new RuntimeException("Invalid combination of trx type - from account type"));

        // make sure creditAccount is allowed
        CashAccount creditAccount = cashAccountRepository.findById(accountTransaction.getCreditAccount().getId()).orElseThrow();
        AccountType creditAccountType = creditAccount.getAccountType();
        transactionType.getApplicableCreditAccountTypes().stream().filter(at -> at.getTypeCode().equals(creditAccountType.getTypeCode())).findFirst()
                .orElseThrow(() -> new RuntimeException("Invalid combination of trx type - to account type"));

        // no currency conversion for now
        if (!accountTransaction.getTransactionAmount().getCurrency().equals(debitAccountType.getMinimumBalance().getCurrency())
                || !accountTransaction.getTransactionAmount().getCurrency().equals(creditAccountType.getMinimumBalance().getCurrency())) {
            throw new RuntimeException("Invalid combination of currencies");
        }

        // make sure balance is positive after transaction (configurable via AccountType.minimumBalance)
        var debitAccountNewBalance = CashAccountUtilities.debit(debitAccount.getAccountBalance(), accountTransaction.getTransactionAmount(), debitAccount.getAccountType().getBalanceSheetEntry());
        if (null != debitAccountType.getMinimumBalance() && debitAccountType.getMinimumBalance().compareTo(debitAccountNewBalance) > 0)
            throw new RuntimeException("Transaction amount exceed available balance - debit");

        var creditAccountNewBalance = CashAccountUtilities.credit(creditAccount.getAccountBalance(), accountTransaction.getTransactionAmount(), creditAccount.getAccountType().getBalanceSheetEntry());
        if (null != creditAccountType.getMinimumBalance() && creditAccountType.getMinimumBalance().compareTo(creditAccountNewBalance) > 0)
            throw new RuntimeException("Transaction amount exceed available balance - credit");

        debitAccount.setAccountBalance(debitAccountNewBalance);
        if (null == debitAccount.getLastTransactionDate() || debitAccount.getLastTransactionDate().isBefore(transactionDate))
            debitAccount.setLastTransactionDate(transactionDate);

        creditAccount.credit(creditAccountNewBalance);
        if (null == creditAccount.getLastTransactionDate() || creditAccount.getLastTransactionDate().isBefore(transactionDate))
            creditAccount.setLastTransactionDate(transactionDate);

        AccountTransaction prepared = accountTransaction.copy(false)
                .setCreditAccount(creditAccount)
                .setDebitAccount(debitAccount)
                .setType(transactionType)
                .setTransactionDate(transactionDate);

        return accountTransactionRepository.save(prepared);
    }

}
