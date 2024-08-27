/*
 * MIT License
 *
 * Copyright (c) 2024 (https://github.com/donniexyz)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.github.donniexyz.demo.med.service;

import com.github.donniexyz.demo.med.entity.AccountOwner;
import com.github.donniexyz.demo.med.entity.AccountOwnerType;
import com.github.donniexyz.demo.med.entity.AccountType;
import com.github.donniexyz.demo.med.entity.CashAccount;
import com.github.donniexyz.demo.med.lib.CashAccountConstants;
import com.github.donniexyz.demo.med.repository.AccountOwnerRepository;
import com.github.donniexyz.demo.med.repository.AccountTypeRepository;
import com.github.donniexyz.demo.med.repository.CashAccountRepository;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestBody;

import javax.money.CurrencyUnit;
import javax.money.MonetaryAmount;
import java.util.List;
import java.util.Optional;

@Service
public class CashAccountService {
    @Autowired
    private CashAccountRepository cashAccountRepository;

    @Autowired
    private AccountOwnerRepository accountOwnerRepository;

    @Autowired
    private AccountTypeRepository accountTypeRepository;


    @NotNull
    public Optional<CashAccount> findById(Long id) {
        return cashAccountRepository.findById(id);
    }

    public Page<CashAccount> findAll(Specification<CashAccount> cashAccountSpecification) {
        return findAll(cashAccountSpecification, Pageable.unpaged());
    }

    public Page<CashAccount> findAll(Specification<CashAccount> cashAccountSpecification, @NonNull Pageable pageable) {
        return cashAccountRepository.findAll(cashAccountSpecification, pageable);
    }

    public List<CashAccount> findAll(Specification<CashAccount> cashAccountSpecification, Sort sort) {
        return cashAccountRepository.findAll(cashAccountSpecification, null == sort ? Sort.unsorted() : sort);
    }

    /**
     *
     * @param cashAccount input to be persisted. Will not modified by this process.
     * @return
     */
    @Transactional
    public CashAccount create(@RequestBody CashAccount cashAccount) {

        Assert.isNull(cashAccount.getVersion(), "Invalid input: version field is not null");

        Long id = cashAccount.getId();
        if (null != id && cashAccountRepository.existsById(id))
            throw new RuntimeException("CashAccount already exists, id: " + id);

        CashAccount toBeSaved = cashAccount.copy(false);

        // make sure acct type exists
        AccountType accountType = accountTypeRepository.findById(cashAccount.getAccountTypeCode()).orElseThrow();
        toBeSaved.setAccountType(accountType);
        // make sure owner exists
        AccountOwner accountOwner = accountOwnerRepository.findById(cashAccount.getAccountOwnerId()).orElseThrow();
        // make sure owner type exists
        AccountOwnerType accountOwnerType = accountOwner.getType();
        // make sure owner type is allowed
        toBeSaved.getAccountType().getApplicableOwnerTypes().stream()
                .filter(ot -> ot.getOwnerTypeCode().equals(accountOwnerType.getTypeCode())).findFirst()
                .orElseThrow(() -> new RuntimeException("Invalid combination of owner type - account type"));
        toBeSaved.setAccountOwner(accountOwner);

        CurrencyUnit currency;
        if (null != accountType.getMinimumBalance()) {
            currency = accountType.getMinimumBalance().getCurrency();
        } else
            currency = CashAccountConstants.MA.ALLOWED_CURRENCIES.get(0);
        MonetaryAmount accountBalance = CashAccountConstants.MA.ZERO_THROUGH_TEN_PER_CCY.get(currency)[0];
        toBeSaved.setAccountBalance(accountBalance);

        return cashAccountRepository.save(toBeSaved);
    }

    @Transactional
    public CashAccount update(CashAccount cashAccount) {

        Assert.notNull(cashAccount.getVersion(), "Invalid input: version field is null");

        CashAccount fetchedFromDb = cashAccountRepository.findById(cashAccount.getId()).orElseThrow();
        CashAccount ca = cashAccount.copy(false);
        // may not change balance
        if ((null != ca.getAccountBalance() && fetchedFromDb.getAccountBalance().compareTo(ca.getAccountBalance()) != 0)
                || (null != ca.getId() && !fetchedFromDb.getId().equals(ca.getId()))
                || (null != ca.getLastTransactionDate() && !fetchedFromDb.getLastTransactionDate().equals(ca.getLastTransactionDate()))
                || (null != ca.getAccountOwnerId() && !fetchedFromDb.getAccountOwnerId().equals(ca.getAccountOwnerId()))
                || (null != ca.getAccountTypeCode() && !fetchedFromDb.getAccountTypeCode().equals(ca.getAccountTypeCode())))
            throw new RuntimeException("Not allowed to directly update balance, lastTrxDate, owner, or acct type");
        fetchedFromDb.copyFrom(ca, true);
        return cashAccountRepository.save(fetchedFromDb);
    }

    @Transactional
    public CashAccount changeId(Long fromId, Long toId) {
        if (!fromId.equals(toId)) {
            int updatedCount = cashAccountRepository.changeId(fromId, toId);
            if (updatedCount < 1) throw new RuntimeException("Unable to update record");
        }
        return cashAccountRepository.findById(toId).orElseThrow();
    }
}