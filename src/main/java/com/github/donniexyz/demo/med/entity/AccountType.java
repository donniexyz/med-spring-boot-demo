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
package com.github.donniexyz.demo.med.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.github.donniexyz.demo.med.entity.ref.BaseEntity;
import com.github.donniexyz.demo.med.entity.ref.IBaseEntity;
import com.github.donniexyz.demo.med.entity.ref.IHasCopy;
import com.github.donniexyz.demo.med.enums.BalanceSheetComponentEnum;
import com.github.donniexyz.demo.med.lib.PatchMapper;
import com.github.donniexyz.demo.med.lib.PutMapper;
import com.github.donniexyz.demo.med.lib.fieldsfilter.NonNullLazyFieldsFilter;
import com.github.donniexyz.demo.med.utils.time.MedJsonFormatForOffsetDateTime;
import io.hypersistence.utils.hibernate.type.money.MonetaryAmountType;
import jakarta.persistence.CascadeType;
import jakarta.persistence.OrderBy;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.WithBy;
import org.hibernate.annotations.*;
import org.jetbrains.annotations.NotNull;

import javax.money.MonetaryAmount;
import java.io.Serial;
import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.List;

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
@FieldNameConstants
public class AccountType implements IBaseEntity<AccountType>, IHasCopy<AccountType>, Serializable {

    @Serial
    private static final long serialVersionUID = -5832102092513250717L;

    @Id
    @EqualsAndHashCode.Include
    private String typeCode;
    private String name;
    @Enumerated(EnumType.STRING)
    private BalanceSheetComponentEnum balanceSheetEntry;

    @AttributeOverride(name = "amount", column = @Column(name = "min_bal"))
    @AttributeOverride(name = "currency", column = @Column(name = "min_bal_ccy"))
    @CompositeType(MonetaryAmountType.class)
    private MonetaryAmount minimumBalance;

    private String notes;

//    @ManyToMany(mappedBy = "applicableDebitAccountTypes")
//    @EqualsAndHashCode.Exclude
//    @ToString.Exclude
//    private Set<AccountTransactionType> applicableDebitTransactionTypes;
//
//    @ManyToMany(mappedBy = "applicableCreditAccountTypes")
//    @EqualsAndHashCode.Exclude
//    @ToString.Exclude
//    private Set<AccountTransactionType> applicableCreditTransactionTypes;

    @OneToMany(mappedBy = "accountType", cascade = {CascadeType.ALL}, orphanRemoval = true)
    @JsonManagedReference("accountTypeToTransactionType")
    @EqualsAndHashCode.Exclude
    @OrderBy(AccountTypeApplicableToTransactionType.Fields.order)
    private List<AccountTypeApplicableToTransactionType> applicableTransactionTypes;

//    @ManyToMany
//    @JoinTable
//    @EqualsAndHashCode.Exclude
//    private Set<AccountOwnerType> applicableForAccountOwnerTypes;

    @OneToMany(mappedBy = "accountType", cascade = {CascadeType.ALL}, orphanRemoval = true)
    @JsonManagedReference("accountTypeToOwnerType")
    @EqualsAndHashCode.Exclude
    private List<AccountOwnerTypeApplicableToAccountType> applicableOwnerTypes;

    // ==============================================================
    // BaseEntity fields
    //---------------------------------------------------------------

    @Formula("true")
    @JsonIgnore
    @Transient
    @org.springframework.data.annotation.Transient
    @FieldNameConstants.Exclude
    @EqualsAndHashCode.Exclude
    private transient Boolean retrievedFromDb;

    @Version
    private Integer version;

    @CreationTimestamp
    @MedJsonFormatForOffsetDateTime
    @Column(updatable = false)
    @EqualsAndHashCode.Exclude
    private OffsetDateTime createdDateTime;

    @CurrentTimestamp
    @MedJsonFormatForOffsetDateTime
    @EqualsAndHashCode.Exclude
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
    private String statusMinor;

    // --------------------------------------------------------------------------

    @JsonIgnore

    public AccountType copy(Boolean cascade) {
        return this.withRetrievedFromDb(BaseEntity.calculateRetrievedFromDb(retrievedFromDb))
                .setApplicableTransactionTypes(BaseEntity.cascade(cascade, AccountTypeApplicableToTransactionType.class, applicableTransactionTypes))
                .setApplicableOwnerTypes(BaseEntity.cascade(cascade, AccountOwnerTypeApplicableToAccountType.class, applicableOwnerTypes))
//                .setApplicableDebitTransactionTypes(BaseEntity.cascadeSet(cascade, AccountTransactionType.class, applicableDebitTransactionTypes))
//                .setApplicableCreditTransactionTypes(BaseEntity.cascadeSet(cascade, AccountTransactionType.class, applicableCreditTransactionTypes))
                ;
    }

    @JsonIgnore
    public AccountType copy(@NotNull List<String> relFields) {
        return this.withRetrievedFromDb(BaseEntity.calculateRetrievedFromDb(retrievedFromDb))
                .setApplicableTransactionTypes(BaseEntity.cascade(Fields.applicableTransactionTypes, relFields, AccountTypeApplicableToTransactionType.class, applicableTransactionTypes))
                .setApplicableOwnerTypes(BaseEntity.cascade(Fields.applicableOwnerTypes, relFields, AccountOwnerTypeApplicableToAccountType.class, applicableOwnerTypes))
//                .setApplicableDebitTransactionTypes(BaseEntity.cascadeSet(Fields.applicableDebitTransactionTypes, relFields, AccountTransactionType.class, applicableDebitTransactionTypes))
//                .setApplicableCreditTransactionTypes(BaseEntity.cascadeSet(Fields.applicableCreditTransactionTypes, relFields, AccountTransactionType.class, applicableCreditTransactionTypes))
                ;
    }

    @JsonIgnore
    public AccountType copyFrom(AccountType setValuesFromThisInstance, boolean nonNullOnly) {
        return nonNullOnly
                ? PatchMapper.INSTANCE.patch(setValuesFromThisInstance, this)
                : PutMapper.INSTANCE.put(setValuesFromThisInstance, this);
    }
}
