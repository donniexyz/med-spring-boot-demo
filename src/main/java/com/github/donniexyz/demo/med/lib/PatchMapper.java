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
}
