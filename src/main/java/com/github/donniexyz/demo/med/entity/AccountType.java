package com.github.donniexyz.demo.med.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.github.donniexyz.demo.med.entity.ref.BaseEntity;
import com.github.donniexyz.demo.med.entity.ref.IBaseEntity;
import com.github.donniexyz.demo.med.entity.ref.IHasCopy;
import com.github.donniexyz.demo.med.enums.BalanceSheetComponentEnum;
import com.github.donniexyz.demo.med.lib.fieldsfilter.NonNullLazyFieldsFilter;
import io.hypersistence.utils.hibernate.type.money.MonetaryAmountType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;
import lombok.experimental.WithBy;
import org.hibernate.annotations.*;
import org.jetbrains.annotations.NotNull;

import javax.money.MonetaryAmount;
import java.io.Serial;
import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;

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
@Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class AccountType implements IBaseEntity<AccountType>, IHasCopy<AccountType>, Serializable {

    @Serial
    private static final long serialVersionUID = -5832102092513250717L;

    @Id
    private String typeCode;
    private String name;
    @Enumerated(EnumType.STRING)
    private BalanceSheetComponentEnum balanceSheetEntry;

    @AttributeOverride(name = "amount", column = @Column(name = "min_bal"))
    @AttributeOverride(name = "currency", column = @Column(name = "min_bal_ccy"))
    @CompositeType(MonetaryAmountType.class)
    private MonetaryAmount minimumBalance;

    private String notes;

    @ManyToMany(mappedBy = "applicableDebitAccountTypes")
    @EqualsAndHashCode.Exclude
    private Set<AccountTransactionType> applicableDebitTransactionTypes;

    @ManyToMany(mappedBy = "applicableCreditAccountTypes")
    @EqualsAndHashCode.Exclude
    private Set<AccountTransactionType> applicableCreditTransactionTypes;

    @ManyToMany
    @JoinTable
    @EqualsAndHashCode.Exclude
    private Set<AccountOwnerType> applicableForAccountOwnerTypes;

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

    // --------------------------------------------------------------------------

    @JsonIgnore

    public AccountType copy(Boolean cascade) {
        return this.withRetrievedFromDb(BaseEntity.calculateRetrievedFromDb(retrievedFromDb))
                .setApplicableForAccountOwnerTypes(BaseEntity.cascadeSet(cascade, AccountOwnerType.class, applicableForAccountOwnerTypes))
                .setApplicableDebitTransactionTypes(BaseEntity.cascadeSet(cascade, AccountTransactionType.class, applicableDebitTransactionTypes))
                .setApplicableCreditTransactionTypes(BaseEntity.cascadeSet(cascade, AccountTransactionType.class, applicableCreditTransactionTypes))
                ;
    }

    @JsonIgnore
    public AccountType copy(@NotNull List<String> relFields) {
        return this.withRetrievedFromDb(BaseEntity.calculateRetrievedFromDb(retrievedFromDb))
                .setApplicableForAccountOwnerTypes(BaseEntity.cascadeSet("applicableForAccountOwnerTypes", relFields, AccountOwnerType.class, applicableForAccountOwnerTypes))
                .setApplicableDebitTransactionTypes(BaseEntity.cascadeSet("applicableDebitTransactionTypes", relFields, AccountTransactionType.class, applicableDebitTransactionTypes))
                .setApplicableCreditTransactionTypes(BaseEntity.cascadeSet("applicableCreditTransactionTypes", relFields, AccountTransactionType.class, applicableCreditTransactionTypes))
                ;
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
        if (!nonNullOnly || null != setValuesFromThisInstance.applicableDebitTransactionTypes)
            this.applicableDebitTransactionTypes = setValuesFromThisInstance.applicableDebitTransactionTypes;
        if (!nonNullOnly || null != setValuesFromThisInstance.applicableCreditTransactionTypes)
            this.applicableCreditTransactionTypes = setValuesFromThisInstance.applicableCreditTransactionTypes;
        if (!nonNullOnly || null != setValuesFromThisInstance.applicableForAccountOwnerTypes)
            this.applicableForAccountOwnerTypes = setValuesFromThisInstance.applicableForAccountOwnerTypes;
        return this;
    }
}
