package com.github.donniexyz.demo.med.service;

import com.github.donniexyz.demo.med.entity.AccountTransaction;
import com.github.donniexyz.demo.med.entity.CashAccount;
import com.github.donniexyz.demo.med.repository.AccountTransactionRepository;
import com.github.donniexyz.demo.med.repository.CashAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AccountTransactionService {

    @Autowired
    private AccountTransactionRepository accountTransactionRepository;

    @Autowired
    private CashAccountRepository cashAccountRepository;

    public AccountTransaction get(Long id) {
        return accountTransactionRepository.getReferenceById(id);
    }

    @Transactional
    public AccountTransaction create(AccountTransaction accountTransaction) {
        Long id = accountTransaction.getId();
        if (null != id && accountTransactionRepository.existsById(id))
            throw new RuntimeException("AccountTransaction already exists, id: " + id);

        Long fromAccountId = accountTransaction.getFromAccount().getId();
        CashAccount fromAccount = cashAccountRepository.getReferenceById(fromAccountId);
        Long toAccountId = accountTransaction.getFromAccount().getId();
        CashAccount toAccount = cashAccountRepository.getReferenceById(toAccountId);

        fromAccount.setBalance(fromAccount.getBalance().subtract(accountTransaction.getTransactionAmount()));
        toAccount.setBalance(toAccount.getBalance().subtract(accountTransaction.getTransactionAmount()));

        return accountTransactionRepository.save(accountTransaction);
    }

}
