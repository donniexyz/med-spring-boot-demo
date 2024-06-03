package com.github.donniexyz.demo.med.controller;

import com.github.donniexyz.demo.med.entity.AccountOwner;
import com.github.donniexyz.demo.med.entity.AccountOwnerType;
import com.github.donniexyz.demo.med.entity.CashAccount;
import com.github.donniexyz.demo.med.repository.AccountOwnerRepository;
import com.github.donniexyz.demo.med.repository.AccountOwnerTypeRepository;
import com.github.donniexyz.demo.med.repository.AccountTypeRepository;
import com.github.donniexyz.demo.med.repository.CashAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/cashAccount")
public class CashAccountController {

    @Autowired
    private CashAccountRepository cashAccountRepository;

    @Autowired
    private AccountOwnerRepository accountOwnerRepository;

    @Autowired
    private AccountTypeRepository accountTypeRepository;

    @Autowired
    private AccountOwnerTypeRepository accountOwnerTypeRepository;

    @GetMapping("/{id}")
    public CashAccount get(@PathVariable("id") Long id) {
        return cashAccountRepository.getReferenceById(id);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    public CashAccount create(@RequestBody CashAccount cashAccount) {
        // make sure acct type exists
        cashAccount.setAccountType(accountTypeRepository.findById(cashAccount.getAccountType().getTypeCode()).orElseThrow());
        // make sure owner exists
        AccountOwner accountOwner = accountOwnerRepository.findById(cashAccount.getAccountOwner().getId()).orElseThrow();
        // make sure owner type exists
        AccountOwnerType accountOwnerType = accountOwner.getType();
        // make sure owner type is allowed
        cashAccount.getAccountType().getApplicableForAccountOwnerTypes().stream()
                .filter(ot -> ot.getTypeCode().equals(accountOwnerType.getTypeCode())).findFirst()
                .orElseThrow(() -> new RuntimeException("Invalid combination of owner type - account type"));
        cashAccount.setBalance(BigDecimal.ZERO);
        return cashAccountRepository.save(cashAccount);
    }

    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    public CashAccount update(@RequestBody CashAccount cashAccount) {
        CashAccount fetchedFromDb = cashAccountRepository.findById(cashAccount.getId()).orElseThrow();
        CashAccount comparator = fetchedFromDb.copy();
        // may not change balance
        if (!fetchedFromDb.getBalance().equals(cashAccount.getBalance())
                || !fetchedFromDb.getLastTransactionDate().equals(cashAccount.getLastTransactionDate())
                || !fetchedFromDb.getAccountOwner().getId().equals(cashAccount.getAccountOwner().getId())
                || !fetchedFromDb.getAccountType().getTypeCode().equals(cashAccount.getAccountType().getTypeCode()))
            throw new RuntimeException("Not allowed to directly update balance, lastTrxDate, owner, or acct type");
        fetchedFromDb.copyFrom(cashAccount, true);
        return cashAccountRepository.save(fetchedFromDb);
    }
}
