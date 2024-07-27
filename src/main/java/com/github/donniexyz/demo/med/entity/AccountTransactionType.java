package com.github.donniexyz.demo.med.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.github.donniexyz.demo.med.entity.ref.BaseEntity;
import com.github.donniexyz.demo.med.entity.ref.IBaseEntity;
import com.github.donniexyz.demo.med.entity.ref.IHasCopy;
import com.github.donniexyz.demo.med.lib.fieldsfilter.NonNullLazyFieldsFilter;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;
import lombok.experimental.WithBy;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.CurrentTimestamp;
import org.hibernate.annotations.Formula;
import org.jetbrains.annotations.NotNull;

import java.io.Serial;
import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;

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
@EqualsAndHashCode
@WithBy
@With
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Accessors(chain = true)
@JsonInclude(value = JsonInclude.Include.CUSTOM, valueFilter = NonNullLazyFieldsFilter.class)
@Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class AccountTransactionType implements IBaseEntity<AccountTransactionType>, IHasCopy<AccountTransactionType>, Serializable {

    @Serial
    private static final long serialVersionUID = -6621150075810071365L;

    @Id
    private String typeCode;
    private String name;
    private String notes;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable
    @EqualsAndHashCode.Exclude
    private Set<AccountType> applicableDebitAccountTypes;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable
    @EqualsAndHashCode.Exclude
    private Set<AccountType> applicableCreditAccountTypes;

    // ==============================================================
    // BaseEntity fields
    //---------------------------------------------------------------

    @Formula("true")
    @JsonIgnore
    private transient Boolean retrievedFromDb;

    @Version
    private Integer version;

    @CreationTimestamp
    private OffsetDateTime createdDateTime;

    @CurrentTimestamp
    private OffsetDateTime lastModifiedDate;

    /**
     * Explaining the status of this record:
     * A: Active
     * I: Inactive
     * D: Soft Deleted (will be hidden from .findAll() because entities has @Where(statusMajor not in ['D', 'R', 'V'])
     * R: Reserved (on case bulk creation of records, but the records actually not yet in use)
     * V: Marked for archival
     */
    private Character recordStatusMajor;

    /**
     * Further explaining the record status. Not handled by common libs. To be handled by individual lib.
     */
    private Character statusMinor;

    // -------------------------------------------------------------------------------------------------

    @JsonIgnore
    public AccountTransactionType copy(Boolean cascade) {
        return this.withRetrievedFromDb(BaseEntity.calculateRetrievedFromDb(retrievedFromDb))
                .setApplicableDebitAccountTypes(BaseEntity.cascadeSet(cascade, AccountType.class, applicableDebitAccountTypes))
                .setApplicableCreditAccountTypes(BaseEntity.cascadeSet(cascade, AccountType.class, applicableCreditAccountTypes))
                ;
    }

    @JsonIgnore
    public AccountTransactionType copy(@NotNull List<String> relFields) {
        return this.withRetrievedFromDb(BaseEntity.calculateRetrievedFromDb(retrievedFromDb))
                .setApplicableDebitAccountTypes(BaseEntity.cascadeSet("applicableDebitAccountTypes", relFields, AccountType.class, applicableDebitAccountTypes))
                .setApplicableCreditAccountTypes(BaseEntity.cascadeSet("applicableCreditAccountTypes", relFields, AccountType.class, applicableCreditAccountTypes))
                ;
    }

    @JsonIgnore
    public AccountTransactionType copyFrom(AccountTransactionType setValuesFromThisInstance, boolean nonNullOnly) {
        if (!nonNullOnly || null != setValuesFromThisInstance.typeCode)
            this.typeCode = setValuesFromThisInstance.typeCode;
        if (!nonNullOnly || null != setValuesFromThisInstance.name)
            this.name = setValuesFromThisInstance.name;
        if (!nonNullOnly || null != setValuesFromThisInstance.notes)
            this.notes = setValuesFromThisInstance.notes;
        if (!nonNullOnly || null != setValuesFromThisInstance.applicableDebitAccountTypes)
            this.applicableDebitAccountTypes = setValuesFromThisInstance.applicableDebitAccountTypes;
        if (!nonNullOnly || null != setValuesFromThisInstance.applicableCreditAccountTypes)
            this.applicableCreditAccountTypes = setValuesFromThisInstance.applicableCreditAccountTypes;
        return this;
    }
}
