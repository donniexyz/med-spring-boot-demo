package com.github.donniexyz.demo.med.controller;

import com.github.donniexyz.demo.med.entity.AccountOwner;
import com.github.donniexyz.demo.med.entity.AccountOwnerType;
import com.github.donniexyz.demo.med.entity.AccountType;
import com.github.donniexyz.demo.med.entity.CashAccount;
import com.github.donniexyz.demo.med.repository.AccountOwnerRepository;
import com.github.donniexyz.demo.med.repository.AccountOwnerTypeRepository;
import com.github.donniexyz.demo.med.repository.AccountTypeRepository;
import com.github.donniexyz.demo.med.repository.CashAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import static com.github.donniexyz.demo.med.lib.CashAccountConstants.MA;

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
        AccountType accountType = accountTypeRepository.findById(cashAccount.getAccountType().getTypeCode()).orElseThrow();
        cashAccount.setAccountType(accountType);
        // make sure owner exists
        AccountOwner accountOwner = accountOwnerRepository.findById(cashAccount.getAccountOwner().getId()).orElseThrow();
        // make sure owner type exists
        AccountOwnerType accountOwnerType = accountOwner.getType();
        // make sure owner type is allowed
        cashAccount.getAccountType().getApplicableForAccountOwnerTypes().stream()
                .filter(ot -> ot.getTypeCode().equals(accountOwnerType.getTypeCode())).findFirst()
                .orElseThrow(() -> new RuntimeException("Invalid combination of owner type - account type"));

        cashAccount.setAccountBalance(MA.ZERO_THROUGH_TEN_PER_CCY.get(accountType.getMinimumBalance().getCurrency())[0]);

        return cashAccountRepository.save(cashAccount);
    }

    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    public CashAccount update(@RequestBody CashAccount cashAccount) {
        CashAccount fetchedFromDb = cashAccountRepository.findById(cashAccount.getId()).orElseThrow();
        CashAccount ca = cashAccount.copy();
        // may not change balance
        if ((null != ca.getAccountBalance() && fetchedFromDb.getAccountBalance().compareTo(ca.getAccountBalance()) != 0)
                || (null != ca.getLastTransactionDate() && !fetchedFromDb.getLastTransactionDate().equals(ca.getLastTransactionDate()))
                || (null != ca.getAccountOwner() && !fetchedFromDb.getAccountOwner().getId().equals(ca.getAccountOwner().getId()))
                || (null != ca.getAccountType() && !fetchedFromDb.getAccountType().getTypeCode().equals(ca.getAccountType().getTypeCode())))
            throw new RuntimeException("Not allowed to directly update balance, lastTrxDate, owner, or acct type");
        ca.setLastTransactionDate(null);
        ca.setAccountOwner(null);
        ca.setAccountType(null);
        fetchedFromDb.copyFrom(ca, true);
        return cashAccountRepository.save(fetchedFromDb);
    }
}
