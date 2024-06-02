package com.github.donniexyz.demo.med.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.github.donniexyz.demo.med.lib.LazyFieldsFilter;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import lombok.*;
import lombok.experimental.Accessors;
import lombok.experimental.WithBy;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * <ul>
 * Examples of AccountTransactionType:
 * <li>INTERNAL, from_accts=[SAVING], to_accts=[SAVING], name="Bank internal transfer", notes="to be used for internal bank customer to customer transaction"</li>
 * <li>INCOMING, from_accts=[CLEARING], to_accts=[SAVING], name="Incoming transfer", notes="to be used for incoming transfer from clearing to customer account"</li>
 * <li>OUTGOING, from_accts=[SAVING], to_accts=[CLEARING], name="Outgoing transfer", notes="to be used for outgoing transfer from customer account to clearing"</li>
 * <li>ADJUSTMENT_DR, from_accts=[INTERNAL_DR], to_accts=[INTERNAL_DR,INTERNAL_CR], name="Adjustment debit transaction", notes="Only for internal use only. Requires C Level and Finance Dept. approval."</li>
 * <li>ADJUSTMENT_CR, from_accts=[INTERNAL_CR], to_accts=[INTERNAL_CR,INTERNAL_DR], name="Adjustment credit transaction", notes="Only for internal use only. Requires C Level and Finance Dept. approval."</li>
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
@JsonInclude(value = JsonInclude.Include.CUSTOM, valueFilter = LazyFieldsFilter.class)
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

    // -------------------------------------------------------------------------------------------------

    @JsonIgnore
    public AccountTransactionType copy() {
        return copy(null);
    }

    @JsonIgnore
    public AccountTransactionType copy(Boolean cascade) {
        return this
                .withApplicableFromAccountTypes(null == applicableFromAccountTypes || !Boolean.TRUE.equals(cascade) ? null : applicableFromAccountTypes.stream().map(fromAccountType -> fromAccountType.copy(false)).collect(Collectors.toSet()))
                .setApplicableToAccountTypes(null == applicableToAccountTypes || !Boolean.TRUE.equals(cascade) ? null : applicableToAccountTypes.stream().map(toAccountType -> toAccountType.copy(false)).collect(Collectors.toSet()));
    }
}
