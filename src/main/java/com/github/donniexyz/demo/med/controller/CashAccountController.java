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
package com.github.donniexyz.demo.med.controller;

import com.github.donniexyz.demo.med.entity.CashAccount;
import com.github.donniexyz.demo.med.exception.CashAccountErrorCode;
import com.github.donniexyz.demo.med.exception.CashAccountException;
import com.github.donniexyz.demo.med.service.CashAccountService;
import jakarta.persistence.EntityManager;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/cashAccount")
public class CashAccountController {

    @Autowired
    private CashAccountService cashAccountService;


    @GetMapping("/{id}")
    @Transactional(readOnly = true)
    public CashAccount get(@PathVariable("id") Long id,
                           @RequestParam(required = false) Boolean cascade,
                           @RequestParam(required = false) List<String> relFields) {
        CashAccount fetched = cashAccountService.findById(id).orElseThrow();
        return null != relFields ? fetched.copy(relFields) : fetched.copy(cascade);
    }

    @GetMapping("/")
    @Transactional(readOnly = true)
    public Page<CashAccount> getAll(
            @RequestParam(required = false) Boolean cascade,
            @RequestParam(required = false) List<String> relFields,
            @PageableDefault Pageable pageable) {
        Specification<CashAccount> cashAccountSpecification = (root, query, criteriaBuilder) -> {
            root.fetch(CashAccount.Fields.accountOwner);
            return criteriaBuilder.conjunction();
        };
        return cashAccountService.findAll(cashAccountSpecification, pageable)
                .map(ca -> null != relFields ? ca.copy(relFields) : ca.copy(cascade));
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public CashAccount create(@RequestBody CashAccount cashAccount) {
        return cashAccountService.create(cashAccount);
    }

    @PutMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public CashAccount update(@PathVariable("id") Long id, @RequestBody CashAccount cashAccount) {
        if (!Objects.equals(id, cashAccount.getId())) throw new CashAccountException(CashAccountErrorCode.ID_MISMATCH);
        return cashAccountService.update(cashAccount);
    }

    @PostMapping("/changeId")
    public CashAccount changeId(@RequestParam("from") Long from, @RequestParam("to") Long to) {
        return cashAccountService.changeId(from, to);
    }
}
