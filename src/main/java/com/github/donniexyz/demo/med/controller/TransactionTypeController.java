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
import com.github.donniexyz.demo.med.service.TransactionTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/accountTransactionType")
public class TransactionTypeController {

    @Autowired
    TransactionTypeService transactionTypeService;

    @GetMapping("/{typeCode}")
    @Transactional(readOnly = true)
    public AccountTransactionType get(@PathVariable("typeCode") String typeCode,
                                      @RequestParam(required = false) Boolean cascade,
                                      @RequestParam(required = false) List<String> relFields) {
        AccountTransactionType fetched = transactionTypeService.findById(typeCode).orElseThrow();
        return null != relFields ? fetched.copy(relFields) : fetched.copy(cascade);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public AccountTransactionType create(@RequestBody AccountTransactionType accountTransactionType) {
        return transactionTypeService.create(accountTransactionType);
    }

    @PutMapping(path = "/{typeCode}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public AccountTransactionType update(@PathVariable("typeCode") String typeCode, @RequestBody AccountTransactionType accountTransactionType) {

        Assert.isTrue(typeCode.equals(accountTransactionType.getTypeCode()), "Invalid request: id mismatch");

        return transactionTypeService.update(accountTransactionType);
    }

    /**
     * @param typeCode
     * @param accountTransactionType <li>/applicableAccountTypes without .transactionTypeCode means new record</li>
     *                               <li>/applicableAccountTypes without .orderNumber means new record</li>
     * @return
     */
    @PatchMapping(path = "/{typeCode}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public AccountTransactionType patch(@PathVariable("typeCode") String typeCode, @RequestBody AccountTransactionType accountTransactionType) {

        Assert.isTrue(typeCode.equals(accountTransactionType.getTypeCode()), "Invalid request: id mismatch");

        return transactionTypeService.patch(accountTransactionType);
    }

    // --------------------------------------------------------------------------------------


}
