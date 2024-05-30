package com.github.donniexyz.demo.med.service;

import com.github.donniexyz.demo.med.entity.AccountTransaction;
import com.github.donniexyz.demo.med.repository.AccountTransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccountTransactionService {

    @Autowired
    private AccountTransactionRepository accountTransactionRepository;

    public AccountTransaction get(Long id) {
        return accountTransactionRepository.getReferenceById(id);
    }

    public AccountTransaction create(AccountTransaction accountTransaction) {
        // TODO: put create transaction logic here
        return accountTransactionRepository.save(accountTransaction);
    }

}
