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
import com.github.donniexyz.demo.med.repository.AccountOwnerRepository;
import com.github.donniexyz.demo.med.repository.AccountOwnerTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/accountOwner")
public class AccountOwnerController {

    @Autowired
    private AccountOwnerRepository accountOwnerRepository;

    @Autowired
    private AccountOwnerTypeRepository accountOwnerTypeRepository;

    @GetMapping("/{id}")
    @Transactional(readOnly = true)
    public AccountOwner get(@PathVariable("id") Long id,
                            @RequestParam(required = false) Boolean cascade,
                            @RequestParam(required = false) List<String> relFields) {
        AccountOwner fetched = accountOwnerRepository.getReferenceById(id);
        return null != relFields ? fetched.copy(relFields) : fetched.copy(cascade);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    public AccountOwner create(@RequestBody AccountOwner accountOwner) {
        accountOwner.setType(
                accountOwnerTypeRepository.findById(accountOwner.getType().getTypeCode())
                        .orElseThrow(() -> new RuntimeException("ownerType not found")));
        return accountOwnerRepository.save(accountOwner);
    }

    @PutMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    public AccountOwner update(@PathVariable("id") Long id,
                               @RequestBody AccountOwner accountOwner) {
        AccountOwner fetchedFromDb = accountOwnerRepository.findById(id).orElseThrow();
        // cannot change ownerType
        if (null != accountOwner.getType() && !fetchedFromDb.getType().getTypeCode().equals(accountOwner.getType().getTypeCode()))
            throw new RuntimeException("Not allowed to change type");
        accountOwner.setType(fetchedFromDb.getType());
        return accountOwnerRepository.save(accountOwner);
    }

    @PatchMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    public AccountOwner patch(@PathVariable("id") Long id,
                               @RequestBody AccountOwner accountOwner) {
        AccountOwner fetchedFromDb = accountOwnerRepository.findById(id).orElseThrow();
        // cannot change ownerType
        if (null != accountOwner.getType() && !fetchedFromDb.getType().getTypeCode().equals(accountOwner.getType().getTypeCode()))
            throw new RuntimeException("Not allowed to change type");
        accountOwner.setType(null);
        fetchedFromDb.copyFrom(accountOwner, true);
        return accountOwnerRepository.save(fetchedFromDb);
    }

    @PostMapping("/changeId")
    @Transactional
    public AccountOwner changeId(@RequestParam("from") Long from, @RequestParam("to") Long to) {
        if (!from.equals(to)) {
            int updatedCount = accountOwnerRepository.changeId(from, to);
            if (updatedCount < 1) throw new RuntimeException("Unable to update record");
        }
        return accountOwnerRepository.findById(to).orElseThrow();
    }
}
