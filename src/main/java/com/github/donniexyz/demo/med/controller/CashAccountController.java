package com.github.donniexyz.demo.med.controller;

import com.github.donniexyz.demo.med.entity.CashAccount;
import com.github.donniexyz.demo.med.repository.CashAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cashAccount")
public class CashAccountController {

    @Autowired
    private CashAccountRepository cashAccountRepository;

    @GetMapping("/{id}")
    public CashAccount get(@PathVariable("id") Long id) {
        return cashAccountRepository.getReferenceById(id);
    }

    @PostMapping
    public CashAccount create(@RequestBody CashAccount cashAccount) {
        // TODO: add check & logic during createCashAccount
        return cashAccountRepository.save(cashAccount);
    }
}
