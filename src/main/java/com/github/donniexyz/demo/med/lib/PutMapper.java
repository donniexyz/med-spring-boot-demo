package com.github.donniexyz.demo.med.lib;

import com.github.donniexyz.demo.med.entity.*;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

@Mapper
public interface PutMapper {
    PutMapper INSTANCE = Mappers.getMapper(PutMapper.class);

    @Mapping(target = "retrievedFromDb", ignore = true)
    AccountTransactionType put(AccountTransactionType source, @MappingTarget AccountTransactionType target);

    @InheritConfiguration
    AccountTransaction put(AccountTransaction source, @MappingTarget AccountTransaction target);

    @InheritConfiguration
    AccountType put(AccountType source, @MappingTarget AccountType target);

    @InheritConfiguration
    AccountOwnerType put(AccountOwnerType setValuesFromThisInstance, @MappingTarget AccountOwnerType accountOwnerType);

    @InheritConfiguration
    AccountOwner put(AccountOwner setValuesFromThisInstance, @MappingTarget AccountOwner accountOwner);

    @InheritConfiguration
    CashAccount put(CashAccount setValuesFromThisInstance, @MappingTarget CashAccount cashAccount);
}
