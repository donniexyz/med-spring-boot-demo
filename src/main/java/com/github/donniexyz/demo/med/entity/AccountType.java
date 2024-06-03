package com.github.donniexyz.demo.med.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.github.donniexyz.demo.med.enums.BalanceSheetComponentEnum;
import com.github.donniexyz.demo.med.lib.fieldsfilter.NonNullLazyFieldsFilter;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;
import lombok.experimental.WithBy;

import java.math.BigDecimal;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * <ul>
 * Examples of AccountType:
 * <li>SAVING, bs=L, ownerTypes=[STAFF,INDI,CORP], minBal=0</li>
 * <li>DRAWERS, bs=A, ownerTypes=[BANK_SELF], minBal=0, notes="to be used when customer deposit or withdraw cash via teller"</li>
 * <li>CLEARING, bs=A, ownerTypes=[BANK_SELF], notes="to be used for incoming/outgoing transfer"</li>
 * <li>INTERNAL_DR, bs=L, ownerTypes=[BANK_SELF], notes="Liability account for internal only"</li>
 * <li>INTERNAL_CR, bs=A, ownerTypes=[BANK_SELF], notes="Asset account for internal only"</li>
 * </ul>
 */
@WithBy
@With
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Accessors(chain = true)
@JsonInclude(value = JsonInclude.Include.CUSTOM, valueFilter = NonNullLazyFieldsFilter.class)
public class AccountType {
    @Id
    private String typeCode;
    private String name;
    @Enumerated(EnumType.STRING)
    private BalanceSheetComponentEnum balanceSheetEntry;

    private BigDecimal minimumBalance;
    private String notes;

    @ManyToMany(mappedBy = "applicableFromAccountTypes")
    @EqualsAndHashCode.Exclude
    private Set<AccountTransactionType> applicableFromTransactionTypes;

    @ManyToMany(mappedBy = "applicableToAccountTypes")
    @EqualsAndHashCode.Exclude
    private Set<AccountTransactionType> applicableToTransactionTypes;

    @ManyToMany
    @JoinTable
    @EqualsAndHashCode.Exclude
    private Set<AccountOwnerType> applicableForAccountOwnerTypes;

    // --------------------------------------------------------------------------

    @JsonIgnore
    public AccountType copy() {
        return copy(null);
    }

    @JsonIgnore
    public AccountType copy(Boolean cascade) {
        return this
                .withApplicableForAccountOwnerTypes(null == applicableForAccountOwnerTypes || !Boolean.TRUE.equals(cascade) ? null : applicableForAccountOwnerTypes.stream().map(accountOwnerType -> accountOwnerType.copy(false)).collect(Collectors.toSet()))
                .setApplicableFromTransactionTypes(null == applicableFromTransactionTypes || !Boolean.TRUE.equals(cascade) ? null : applicableFromTransactionTypes.stream().map(fromAccountTransactionType -> fromAccountTransactionType.copy(false)).collect(Collectors.toSet()))
                .setApplicableToTransactionTypes(null == applicableToTransactionTypes || !Boolean.TRUE.equals(cascade) ? null : applicableToTransactionTypes.stream().map(toAccountTransactionType -> toAccountTransactionType.copy(false)).collect(Collectors.toSet()));
    }

    @JsonIgnore
    public AccountType copyFrom(AccountType setValuesFromThisInstance, boolean nonNullOnly) {
        if (!nonNullOnly || null != setValuesFromThisInstance.typeCode)
            this.typeCode = setValuesFromThisInstance.typeCode;
        if (!nonNullOnly || null != setValuesFromThisInstance.name)
            this.name = setValuesFromThisInstance.name;
        if (!nonNullOnly || null != setValuesFromThisInstance.balanceSheetEntry)
            this.balanceSheetEntry = setValuesFromThisInstance.balanceSheetEntry;
        if (!nonNullOnly || null != setValuesFromThisInstance.minimumBalance)
            this.minimumBalance = setValuesFromThisInstance.minimumBalance;
        if (!nonNullOnly || null != setValuesFromThisInstance.notes)
            this.notes = setValuesFromThisInstance.notes;
        if (!nonNullOnly || null != setValuesFromThisInstance.applicableFromTransactionTypes)
            this.applicableFromTransactionTypes = setValuesFromThisInstance.applicableFromTransactionTypes;
        if (!nonNullOnly || null != setValuesFromThisInstance.applicableToTransactionTypes)
            this.applicableToTransactionTypes = setValuesFromThisInstance.applicableToTransactionTypes;
        if (!nonNullOnly || null != setValuesFromThisInstance.applicableForAccountOwnerTypes)
            this.applicableForAccountOwnerTypes = setValuesFromThisInstance.applicableForAccountOwnerTypes;
        return this;
    }
}
