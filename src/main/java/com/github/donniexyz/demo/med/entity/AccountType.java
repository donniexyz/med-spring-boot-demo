package com.github.donniexyz.demo.med.entity;

import com.github.donniexyz.demo.med.enums.BalanceSheetComponentEnum;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.WithBy;

import java.math.BigDecimal;
import java.util.Set;

/**
 * <ul>
 * Examples of AccountType:
 * <li>SAVING, bs=L, ownerTypes=[STAFF,INDIV,CORP], minBal=0</li>
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
public class AccountType {
    @Id
    private String typeCode;
    private String name;
    @Enumerated(EnumType.STRING)
    private BalanceSheetComponentEnum balanceSheetEntry;

    private BigDecimal minimumBalance;
    private String notes;

    @ManyToMany(mappedBy="applicableFromAccountTypes")
    private Set<AccountTransactionType> applicableFromTransactionTypes;

    @ManyToMany(mappedBy="applicableToAccountTypes")
    private Set<AccountTransactionType> applicableToTransactionTypes;

    @ManyToMany
    @JoinTable
    private Set<AccountOwnerType> applicableForAccountOwnerTypes;

}
