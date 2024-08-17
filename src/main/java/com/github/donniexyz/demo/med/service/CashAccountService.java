package com.github.donniexyz.demo.med.service;

import com.github.donniexyz.demo.med.entity.CashAccount;
import com.github.donniexyz.demo.med.repository.CashAccountRepository;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CashAccountService {
    @Autowired
    private CashAccountRepository cashAccountRepository;


    @NotNull
    Optional<CashAccount> getById(CashAccount accountTransaction) {
        return cashAccountRepository.findById(accountTransaction.getId());
    }
}