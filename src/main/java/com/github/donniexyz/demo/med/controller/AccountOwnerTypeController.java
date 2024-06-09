package com.github.donniexyz.demo.med.controller;

import com.github.donniexyz.demo.med.entity.AccountOwner;
import com.github.donniexyz.demo.med.entity.AccountOwnerType;
import com.github.donniexyz.demo.med.repository.AccountOwnerTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/accountOwnerType")
public class AccountOwnerTypeController {

    @Autowired
    private AccountOwnerTypeRepository accountOwnerTypeRepository;

    @GetMapping("/{typeCode}")
    public AccountOwnerType get(@PathVariable("typeCode") String typeCode) {
        return accountOwnerTypeRepository.findById(typeCode).orElseThrow();
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    public AccountOwnerType create(@RequestBody AccountOwnerType accountOwnerType) {
        accountOwnerTypeRepository.findById(accountOwnerType.getTypeCode()).orElseThrow();
        return accountOwnerTypeRepository.save(accountOwnerType);
    }

    @PutMapping(path = "/{typeCode}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    public AccountOwnerType update(@PathVariable("typeCode") String typeCode,
                               @RequestBody AccountOwnerType accountOwnerType) {
        AccountOwnerType fetchedFromDb = accountOwnerTypeRepository.findById(typeCode).orElseThrow();
        fetchedFromDb.copyFrom(accountOwnerType, true);
        return accountOwnerTypeRepository.save(fetchedFromDb);
    }
}
