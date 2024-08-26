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

import com.github.donniexyz.demo.med.entity.AccountType;
import com.github.donniexyz.demo.med.service.AccountTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/accountType")
public class AccountTypeController {

    @Autowired
    private AccountTypeService accountTypeService;

    @GetMapping("/{typeCode}")
    @Transactional(readOnly = true)
    public AccountType get(@PathVariable("typeCode") String typeCode,
                           @RequestParam(required = false) Boolean cascade,
                           @RequestParam(required = false) List<String> relFields) {
        AccountType fetched = accountTypeService.findById(typeCode).orElseThrow();
        return null != relFields ? fetched.copy(relFields) : fetched.copy(cascade);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public AccountType create(@RequestBody AccountType accountType) {
        return accountTypeService.create(accountType);
    }

    @PutMapping(path = "/{typeCode}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public AccountType update(@PathVariable("typeCode") String typeCode,
                              @RequestBody AccountType accountType) {

        Assert.isTrue(typeCode.equals(accountType.getTypeCode()), "Invalid request: id mismatch");

        return accountTypeService.update(accountType);
    }

    @PatchMapping(path = "/{typeCode}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public AccountType patch(@PathVariable("typeCode") String typeCode,
                             @RequestBody AccountType accountType) {

        Assert.isTrue(typeCode.equals(accountType.getTypeCode()), "Invalid request: id mismatch");

        return accountTypeService.patch(accountType);
    }

}
