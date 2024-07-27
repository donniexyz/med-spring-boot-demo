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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.money.CurrencyUnit;
import javax.money.MonetaryAmount;

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
    @Transactional(readOnly = true)
    public CashAccount get(@PathVariable("id") Long id,
                           @RequestParam(value = "cascade", required = false) String cascade) {
        return cashAccountRepository.findById(id).orElseThrow().copy(null == cascade || cascade.isEmpty() ? null : "true".equals(cascade));
    }

    @GetMapping("/")
    @Transactional(readOnly = true)
    public Page<CashAccount> getAll(Pageable pageable) {
        Specification<CashAccount> cashAccountSpecification = (root, query, criteriaBuilder) -> {
            root.fetch("accountOwner");
            return criteriaBuilder.conjunction();
        };
        return cashAccountRepository.findAll(cashAccountSpecification, null == pageable ? Pageable.unpaged() : pageable)
                .map(ca -> ca.copy());
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
        cashAccount.setAccountOwner(accountOwner);

        CurrencyUnit currency;
        if (null != accountType.getMinimumBalance()) {
            currency = accountType.getMinimumBalance().getCurrency();
        } else
            currency = MA.ALLOWED_CURRENCIES.get(0);
        MonetaryAmount accountBalance = MA.ZERO_THROUGH_TEN_PER_CCY.get(currency)[0];
        cashAccount.setAccountBalance(accountBalance);

        return cashAccountRepository.save(cashAccount);
    }

    @PutMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    public CashAccount update(@PathVariable("id") Long id, @RequestBody CashAccount cashAccount) {
        CashAccount fetchedFromDb = cashAccountRepository.findById(id).orElseThrow();
        CashAccount ca = cashAccount.copy();
        // may not change balance
        if ((null != ca.getAccountBalance() && fetchedFromDb.getAccountBalance().compareTo(ca.getAccountBalance()) != 0)
                || (null != ca.getId() && !fetchedFromDb.getId().equals(ca.getId()))
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

    @PostMapping("/changeId")
    @Transactional
    public CashAccount changeId(@RequestParam("from") Long from, @RequestParam("to") Long to) {
        if (!from.equals(to)) {
            int updatedCount = cashAccountRepository.changeId(from, to);
            if (updatedCount < 1) throw new RuntimeException("Unable to update record");
        }
        return cashAccountRepository.findById(to).orElseThrow();
    }
}
