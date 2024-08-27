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
