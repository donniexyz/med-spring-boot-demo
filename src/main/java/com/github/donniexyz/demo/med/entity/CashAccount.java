package com.github.donniexyz.demo.med.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.github.donniexyz.demo.med.lib.LazyFieldsFilter;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;
import lombok.experimental.WithBy;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@WithBy
@With
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Accessors(chain = true)
@JsonInclude(value = JsonInclude.Include.CUSTOM, valueFilter = LazyFieldsFilter.class)
public class CashAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private BigDecimal balance;
    private LocalDateTime lastTransactionDate;
    private String notes;

    @OneToMany(mappedBy = "account")
    @JsonManagedReference
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<AccountHistory> accountHistories;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private AccountOwner accountOwner;

    @ManyToOne
    @JoinColumn(name = "type_code")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private AccountType accountType;

    // ------------------------------------------------------

    @JsonIgnore
    public CashAccount copy() {
        return copy(null);
    }

    public CashAccount copy(Boolean cascade) {
        return this.withAccountOwner(null == accountOwner || Boolean.FALSE.equals(cascade) ? null : accountOwner.copy(false))
                .setAccountType(null == accountType || Boolean.FALSE.equals(cascade) ? null : accountType.copy(false))
                .setAccountHistories(null == accountHistories || !Boolean.TRUE.equals(cascade) ? null : accountHistories.stream().map(accountHistory -> accountHistory.copy(false)).collect(Collectors.toList()));
    }

    @JsonIgnore
    public void copyFrom(CashAccount setValuesFromThisInstance, boolean nonNullOnly) {
        if (!nonNullOnly || null != setValuesFromThisInstance.id)
            this.id = setValuesFromThisInstance.id;
        if (!nonNullOnly || null != setValuesFromThisInstance.title)
            this.title = setValuesFromThisInstance.title;
        if (!nonNullOnly || null != setValuesFromThisInstance.balance)
            this.balance = setValuesFromThisInstance.balance;
        if (!nonNullOnly || null != setValuesFromThisInstance.lastTransactionDate)
            this.lastTransactionDate = setValuesFromThisInstance.lastTransactionDate;
        if (!nonNullOnly || null != setValuesFromThisInstance.notes)
            this.notes = setValuesFromThisInstance.notes;
        if (!nonNullOnly || null != setValuesFromThisInstance.accountHistories)
            this.accountHistories = setValuesFromThisInstance.accountHistories;
        if (!nonNullOnly || null != setValuesFromThisInstance.accountOwner)
            this.accountOwner = setValuesFromThisInstance.accountOwner;
        if (!nonNullOnly || null != setValuesFromThisInstance.accountType)
            this.accountType = setValuesFromThisInstance.accountType;
    }
}
