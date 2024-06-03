package com.github.donniexyz.demo.med.service;

import com.github.donniexyz.demo.med.entity.AccountTransaction;
import com.github.donniexyz.demo.med.entity.AccountTransactionType;
import com.github.donniexyz.demo.med.entity.AccountType;
import com.github.donniexyz.demo.med.entity.CashAccount;
import com.github.donniexyz.demo.med.repository.AccountTransactionRepository;
import com.github.donniexyz.demo.med.repository.AccountTransactionTypeRepository;
import com.github.donniexyz.demo.med.repository.CashAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
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

        if (BigDecimal.ZERO.compareTo(accountTransaction.getTransactionAmount()) >= 0)
            throw new RuntimeException("Transaction amount must be positive");

        // make sure trx type exists
        AccountTransactionType transactionType = accountTransactionTypeRepository.findById(accountTransaction.getType().getTypeCode()).orElseThrow();

        // make sure fromAccount is allowed
        CashAccount fromAccount = cashAccountRepository.findById(accountTransaction.getFromAccount().getId()).orElseThrow();
        AccountType fromAccountType = fromAccount.getAccountType();
        transactionType.getApplicableFromAccountTypes().stream().filter(at -> at.getTypeCode().equals(fromAccountType.getTypeCode())).findFirst()
                .orElseThrow(() -> new RuntimeException("Invalid combination of trx type - from account type"));

        // make sure toAccount is allowed
        CashAccount toAccount = cashAccountRepository.findById(accountTransaction.getToAccount().getId()).orElseThrow();
        AccountType toAccountType = toAccount.getAccountType();
        transactionType.getApplicableToAccountTypes().stream().filter(at -> at.getTypeCode().equals(toAccountType.getTypeCode())).findFirst()
                .orElseThrow(() -> new RuntimeException("Invalid combination of trx type - to account type"));

        // make sure balance is positive after transaction (configurable via AccountType.minimumBalance)
        BigDecimal fromAccountNewBalance = fromAccount.getBalance().subtract(accountTransaction.getTransactionAmount());
        if (null != fromAccountType.getMinimumBalance() && fromAccountType.getMinimumBalance().compareTo(fromAccountNewBalance) > 0)
            throw new RuntimeException("Transaction amount exceed available balance");

        fromAccount.setBalance(fromAccountNewBalance);
        if (null == fromAccount.getLastTransactionDate() || fromAccount.getLastTransactionDate().isBefore(transactionDate))
            fromAccount.setLastTransactionDate(transactionDate);

        toAccount.setBalance(toAccount.getBalance().add(accountTransaction.getTransactionAmount()));
        if (null == toAccount.getLastTransactionDate() || toAccount.getLastTransactionDate().isBefore(transactionDate))
            toAccount.setLastTransactionDate(transactionDate);

        AccountTransaction prepared = accountTransaction.copy(false)
                .setToAccount(toAccount)
                .setFromAccount(fromAccount)
                .setType(transactionType)
                .setTransactionDate(transactionDate)
                ;

        return accountTransactionRepository.save(prepared);
    }

}
