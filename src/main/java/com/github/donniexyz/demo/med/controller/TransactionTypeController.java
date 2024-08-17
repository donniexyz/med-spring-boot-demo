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

import com.github.donniexyz.demo.med.entity.AccountTransactionType;
import com.github.donniexyz.demo.med.entity.AccountType;
import com.github.donniexyz.demo.med.entity.AccountTypeApplicableToTransactionType;
import com.github.donniexyz.demo.med.lib.PatchMapper;
import com.github.donniexyz.demo.med.repository.AccountTransactionTypeRepository;
import com.github.donniexyz.demo.med.repository.AccountTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/accountTransactionType")
public class TransactionTypeController {

    @Autowired
    AccountTransactionTypeRepository transactionTypeRepository;

    @Autowired
    AccountTypeRepository accountTypeRepository;

    @GetMapping("/{typeCode}")
    @Transactional
    public AccountTransactionType get(@PathVariable("typeCode") String typeCode) {
        return transactionTypeRepository.findById(typeCode).orElseThrow().copy(true);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    public AccountTransactionType create(@RequestBody AccountTransactionType accountTransactionType) {
        String typeCode = accountTransactionType.getTypeCode();
        if (transactionTypeRepository.existsById(typeCode))
            throw new RuntimeException("AccountTransactionType typeCode=" + typeCode + " already exists");

        return transactionTypeRepository.save(accountTransactionType);
    }

    @PutMapping(path = "/{typeCode}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    public AccountTransactionType update(@PathVariable("typeCode") String typeCode, @RequestBody AccountTransactionType accountTransactionType) {
        AccountTransactionType fetchedFromDb = transactionTypeRepository.findById(typeCode).orElseThrow();

        return transactionTypeRepository.save(accountTransactionType);
    }

    @PatchMapping(path = "/{typeCode}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    public AccountTransactionType patch(@PathVariable("typeCode") String typeCode, @RequestBody AccountTransactionType accountTransactionType) {
        AccountTransactionType fetchedFromDb = transactionTypeRepository.findById(typeCode).orElseThrow();

        PatchMapper.INSTANCE.patch(accountTransactionType, fetchedFromDb);

        return transactionTypeRepository.save(fetchedFromDb);
    }

}
