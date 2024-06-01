package com.github.donniexyz.demo.med.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import lombok.*;
import lombok.experimental.WithBy;

import java.util.Set;

/**
 * <ul>
 * Examples of AccountTransactionType:
 * <li>INTERNAL, from_accts=[STAFF,INDI,CORP], to_accts=[STAFF,INDI,CORP], name="Bank internal transfer", notes="to be used for internal bank customer to customer transaction"</li>
 * <li>INCOMING, from_accts=[CLEARING], to_accts=[STAFF,INDI,CORP], name="Incoming transfer", notes="to be used for incoming transfer from clearing to customer account"</li>
 * <li>OUTGOING, from_accts=[STAFF,INDI,CORP], to_accts=[CLEARING], name="Outgoing transfer", notes="to be used for outgoing transfer from customer account to clearing"</li>
 * <li>ADJUSTMENT_DR, from_accts=[INTERNAL_DR], to_accts=[STAFF,INDI,CORP], name="Adjustment debit transaction", notes="Only for internal use only. Requires C Level and Finance Dept. approval."</li>
 * <li>ADJUSTMENT_CR, from_accts=[STAFF,INDI,CORP], to_accts=[INTERNAL_CR], name="Adjustment credit transaction", notes="Only for internal use only. Requires C Level and Finance Dept. approval."</li>
 * </ul>
 */
@WithBy
@With
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
public class AccountTransactionType {
    @Id
    private String typeCode;
    private String name;
    private String notes;

    @ManyToMany
    @JoinTable
    private Set<AccountType> applicableFromAccountTypes;

    @ManyToMany
    @JoinTable
    private Set<AccountType> applicableToAccountTypes;
}
