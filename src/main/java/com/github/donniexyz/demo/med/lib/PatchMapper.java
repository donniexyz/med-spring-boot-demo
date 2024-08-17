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
package com.github.donniexyz.demo.med.lib;

import com.github.donniexyz.demo.med.entity.*;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

@Mapper(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface PatchMapper {
    PatchMapper INSTANCE = Mappers.getMapper(PatchMapper.class);

    @Mapping(target = "retrievedFromDb", ignore = true)
    AccountTransactionType patch(AccountTransactionType source, @MappingTarget AccountTransactionType target);

    @InheritConfiguration
    AccountTransaction patch(AccountTransaction source, @MappingTarget AccountTransaction target);

    @InheritConfiguration
    AccountType patch(AccountType source, @MappingTarget AccountType target);

    @InheritConfiguration
    AccountOwnerType patch(AccountOwnerType setValuesFromThisInstance, @MappingTarget AccountOwnerType accountOwnerType);

    @InheritConfiguration
    AccountOwner patch(AccountOwner setValuesFromThisInstance, @MappingTarget AccountOwner accountOwner);

    @InheritConfiguration
    CashAccount patch(CashAccount setValuesFromThisInstance, @MappingTarget CashAccount cashAccount);

    @InheritConfiguration
    AccountHistoryType patch(AccountHistoryType setValuesFromThisInstance, @MappingTarget AccountHistoryType accountHistoryType);
}
