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

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.github.donniexyz.demo.med.entity.ref.*;
import com.github.donniexyz.demo.med.enums.DebitCreditEnum;
import com.github.donniexyz.demo.med.lib.PatchMapper;
import com.github.donniexyz.demo.med.lib.PutMapper;
import com.github.donniexyz.demo.med.lib.fieldsfilter.NonNullLazyFieldsFilter;
import com.github.donniexyz.demo.med.utils.time.MedJsonFormatForOffsetDateTime;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;
import lombok.experimental.FieldNameConstants;
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

@EqualsAndHashCode
@WithBy
@With
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(indexes = {
        @Index(name = "idx_accTypeApplToTrxType_accTCode_order", columnList = "acc_tcode,debit_credit,trx_tcode", unique = true)})
@Accessors(chain = true)
@JsonInclude(value = JsonInclude.Include.CUSTOM, valueFilter = NonNullLazyFieldsFilter.class)
@Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@FieldNameConstants
public class AccountTypeApplicableToTransactionType implements IBaseEntity<AccountTypeApplicableToTransactionType>,
        IHasCopy<AccountTypeApplicableToTransactionType>, IHasOrderNumber, IHasValidate, Serializable {

    @Serial
    private static final long serialVersionUID = -3330006119049351963L;

    @Id
    @Column(name = "trx_tcode")
    private String transactionTypeCode;

    @Column(name = "acc_tcode", nullable = false)
    private String accountTypeCode;

    @Id
    @Column(name = "order_number")
    private Integer orderNumber;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private DebitCreditEnum debitCredit;

    /**
     * 0 means the entry is optional
     */
    @Column(nullable = false)
    private Integer minOccurrences;

    /**
     * null means maxOccurrences matches with minOccurrences.
     * maxOccurrences less than minOccurrences means invalid configuration.
     */
    @Column(nullable = false)
    private Integer maxOccurrences;

    private String notes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trx_tcode", insertable = false, updatable = false,
            foreignKey = @ForeignKey(name = "fk_AccTypeApplToTrxType_trxType"))
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @JsonBackReference("transactionTypeToAccountType")
    private AccountTransactionType transactionType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "acc_tcode", insertable = false, updatable = false,
            foreignKey = @ForeignKey(name = "fk_AccTypeApplToTrxType_accType"))
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @JsonBackReference("accountTypeToTransactionType")
    private AccountType accountType;

    // ==============================================================
    // BaseEntity fields
    //---------------------------------------------------------------

    @Formula("true")
    @JsonIgnore
    @org.springframework.data.annotation.Transient
    @FieldNameConstants.Exclude
    @EqualsAndHashCode.Exclude
    private Boolean retrievedFromDb;

    @Version
    private Integer version;

    @CreationTimestamp
    @MedJsonFormatForOffsetDateTime
    @Column(updatable = false)
    private OffsetDateTime createdDateTime;

    @CurrentTimestamp
    @MedJsonFormatForOffsetDateTime
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

    // -------------------------------------------------------------------------------------------------

    @JsonIgnore
    public AccountTypeApplicableToTransactionType copy(Boolean cascade) {
        return this.withFlippedRetrievedFromDb()
                .setAccountType(BaseEntity.cascade(cascade, AccountType.class, accountType))
                .setTransactionType(BaseEntity.cascade(cascade, AccountTransactionType.class, transactionType))
                ;
    }

    @JsonIgnore
    public AccountTypeApplicableToTransactionType copy(@NotNull List<String> relFields) {
        return this.withFlippedRetrievedFromDb()
                .setAccountType(BaseEntity.cascade(Fields.accountType, relFields, AccountType.class, accountType))
                .setTransactionType(BaseEntity.cascade(Fields.transactionType, relFields, AccountTransactionType.class, transactionType))
                ;
    }

    @JsonIgnore
    public AccountTypeApplicableToTransactionType copyFrom(AccountTypeApplicableToTransactionType setValuesFromThisInstance, boolean nonNullOnly) {
        return nonNullOnly
                ? PatchMapper.INSTANCE.patch(setValuesFromThisInstance, this)
                : PutMapper.INSTANCE.put(setValuesFromThisInstance, this);
    }

    @JsonIgnore
    public AccountTypeApplicableToTransactionType withFlippedRetrievedFromDb() {
        return this.withRetrievedFromDb(BaseEntity.calculateRetrievedFromDb(retrievedFromDb));
    }

    // -------------------------------------------------------------------------------------------------

    public AccountTypeApplicableToTransactionType setTransactionType(AccountTransactionType transactionType) {
        this.transactionType = transactionType;
        if (null != transactionType) this.transactionTypeCode = transactionType.getTypeCode();
        return this;
    }

    public AccountTypeApplicableToTransactionType setAccountType(AccountType accountType) {
        this.accountType = accountType;
        if (null != accountType) this.accountTypeCode = accountType.getTypeCode();
        return this;
    }

    // -------------------------------------------------------------------------------------------------

    @Override
    public InvalidInfo getInvalid() {
        if (null == maxOccurrences && null != minOccurrences)
            maxOccurrences = minOccurrences <= 0 ? 1 : minOccurrences;
        return null != maxOccurrences && maxOccurrences >= minOccurrences ? null :
                InvalidInfo.builder()
                        .fieldName(AccountTypeApplicableToTransactionType.Fields.maxOccurrences)
                        .fieldValue(maxOccurrences).build();
    }
}
