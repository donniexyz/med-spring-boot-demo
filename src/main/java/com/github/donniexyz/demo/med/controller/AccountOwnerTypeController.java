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

import com.github.donniexyz.demo.med.entity.AccountOwnerType;
import com.github.donniexyz.demo.med.service.AccountOwnerTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/accountOwnerType")
public class AccountOwnerTypeController {

    @Autowired
    private AccountOwnerTypeService accountOwnerTypeService;

    @GetMapping("/{typeCode}")
    @Transactional(readOnly = true)
    public AccountOwnerType get(@PathVariable("typeCode") String typeCode,
                                @RequestParam(required = false) Boolean cascade,
                                @RequestParam(required = false) List<String> relFields) {
        AccountOwnerType fetched = accountOwnerTypeService.findById(typeCode).orElseThrow();
        return null != relFields ? fetched.copy(relFields) : fetched.copy(cascade);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public AccountOwnerType create(@RequestBody AccountOwnerType accountOwnerType) {
        return accountOwnerTypeService.create(accountOwnerType);
    }

    @PutMapping(path = "/{typeCode}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public AccountOwnerType update(@PathVariable("typeCode") String typeCode,
                                   @RequestBody AccountOwnerType accountOwnerType) {

        Assert.isTrue(typeCode.equals(accountOwnerType.getTypeCode()), "Invalid request: id mismatch");

        return accountOwnerTypeService.update(accountOwnerType);
    }

    @PatchMapping(path = "/{typeCode}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public AccountOwnerType patch(@PathVariable("typeCode") String typeCode,
                                  @RequestBody AccountOwnerType accountOwnerType) {

        Assert.isTrue(typeCode.equals(accountOwnerType.getTypeCode()), "Invalid request: id mismatch");

        return accountOwnerTypeService.patch(accountOwnerType);
    }
}
