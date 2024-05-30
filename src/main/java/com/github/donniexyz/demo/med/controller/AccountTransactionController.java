package com.github.donniexyz.demo.med.controller;

import com.github.donniexyz.demo.med.entity.AccountTransaction;
import com.github.donniexyz.demo.med.service.AccountTransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/accountTransaction")
public class AccountTransactionController {

    @Autowired
    private AccountTransactionService accountTransactionService;

    @GetMapping("/{id}")
    public AccountTransaction get(@PathVariable("id") Long id) {
        return accountTransactionService.get(id);
    }

    @PostMapping("/")
    public AccountTransaction create(@RequestBody AccountTransaction accountTransaction) {
        return accountTransactionService.create(accountTransaction);
    }
}
