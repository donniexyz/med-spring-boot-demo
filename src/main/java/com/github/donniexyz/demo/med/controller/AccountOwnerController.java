package com.github.donniexyz.demo.med.controller;

import com.github.donniexyz.demo.med.entity.AccountOwner;
import com.github.donniexyz.demo.med.repository.AccountOwnerRepository;
import com.github.donniexyz.demo.med.repository.AccountOwnerTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/accountOwner")
public class AccountOwnerController {

    @Autowired
    private AccountOwnerRepository accountOwnerRepository;

    @Autowired
    private AccountOwnerTypeRepository accountOwnerTypeRepository;

    @GetMapping("/{id}")
    public AccountOwner get(@PathVariable("id") Long id) {
        return accountOwnerRepository.getReferenceById(id);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    public AccountOwner create(@RequestBody AccountOwner accountOwner) {
        accountOwner.setType(
                accountOwnerTypeRepository.findById(accountOwner.getType().getTypeCode())
                        .orElseThrow(() -> new RuntimeException("ownerType not found")));
        return accountOwnerRepository.save(accountOwner);
    }

    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    public AccountOwner update(@RequestBody AccountOwner accountOwner) {
        AccountOwner fetchedFromDb = accountOwnerRepository.findById(accountOwner.getId()).orElseThrow();
        // cannot change ownerType
        if (!fetchedFromDb.getType().getTypeCode().equals(accountOwner.getType().getTypeCode()))
            throw new RuntimeException("Not allowed to change type");
        accountOwner.setType(null);
        fetchedFromDb.copyFrom(accountOwner, true);
        return accountOwnerRepository.save(fetchedFromDb);
    }
}
