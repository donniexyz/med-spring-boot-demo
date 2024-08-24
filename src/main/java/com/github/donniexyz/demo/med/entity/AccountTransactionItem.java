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
import com.github.donniexyz.demo.med.entity.ref.BaseEntity;
import com.github.donniexyz.demo.med.entity.ref.IBaseEntity;
import com.github.donniexyz.demo.med.entity.ref.IHasCopy;
import com.github.donniexyz.demo.med.entity.ref.IHasOrderNumber;
import com.github.donniexyz.demo.med.enums.DebitCreditEnum;
import com.github.donniexyz.demo.med.lib.PatchMapper;
import com.github.donniexyz.demo.med.lib.PutMapper;
import com.github.donniexyz.demo.med.lib.fieldsfilter.LazyFieldsFilter;
import com.github.donniexyz.demo.med.utils.time.MedJsonFormatForOffsetDateTime;
import io.hypersistence.utils.hibernate.type.money.MonetaryAmountType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.WithBy;
import org.hibernate.annotations.CompositeType;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.CurrentTimestamp;
import org.hibernate.annotations.Formula;

import javax.money.MonetaryAmount;
import java.io.Serial;
import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@WithBy
@With
@Builder(toBuilder = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(indexes = {
        @Index(name = "idx_accTrxItem_accId_trxId", columnList = "acc_id,trx_id")})
@Accessors(chain = true)
@JsonInclude(value = JsonInclude.Include.CUSTOM, valueFilter = LazyFieldsFilter.class)
@FieldNameConstants
public class AccountTransactionItem implements IBaseEntity<AccountTransactionItem>, IHasCopy<AccountTransactionItem>, IHasOrderNumber, Serializable {

    @Serial
    private static final long serialVersionUID = 8065578887369866423L;

//    @Id
//    @Column
//    @GeneratedValue
//    private UUID lineId;

    @Id
    @Column(name = "trx_id")
    private Long transactionId;

    @Column(name = "acc_id")
    private Long accountId;

    @Id
    @Column(name = "order_number", nullable = false)
    private Integer orderNumber;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private DebitCreditEnum debitCredit;

    @AttributeOverride(name = "amount", column = @Column(name = "trx_amount"))
    @AttributeOverride(name = "currency", column = @Column(name = "trx_ccy"))
    @CompositeType(MonetaryAmountType.class)
    private MonetaryAmount transactionAmount;

    private String label; // e.g., "Deposit," "Withdrawal"
    private String notes;

    @Column(name = "trx_tcode")
    private String transactionTypeCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trx_tcode", insertable = false, updatable = false,
            foreignKey = @ForeignKey(name = "fk_AccTrxItem_type"))
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @JsonIgnore
    private AccountTransactionType type;

    @OneToOne
    @JoinColumn(name = "acc_id", insertable = false, updatable = false)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @JsonIgnore
    private CashAccount account;

    @ManyToOne
    @JoinColumn(name = "trx_id", insertable = false, updatable = false)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @JsonIgnore
    private AccountTransaction accountTransaction;

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

    // --------------------------------------------------------------------------------

    @JsonIgnore
    public AccountTransactionItem copy(Boolean cascade) {
        return this.withFlippedRetrievedFromDb()
                .setType(BaseEntity.cascade(cascade, AccountTransactionType.class, type))
                .setAccount(BaseEntity.cascade(cascade, CashAccount.class, account))
                .setAccountTransaction(BaseEntity.cascade(cascade, AccountTransaction.class, accountTransaction))
                ;
    }

    @Override
    public AccountTransactionItem copy(@NonNull List<String> relFields) {
        return this.withFlippedRetrievedFromDb()
                .setType(BaseEntity.cascade(Fields.type, relFields, AccountTransactionType.class, type))
                .setAccount(BaseEntity.cascade(Fields.account, relFields, CashAccount.class, account))
                .setAccountTransaction(BaseEntity.cascade(Fields.accountTransaction, relFields, AccountTransaction.class, accountTransaction))
                ;
    }

    @Override
    public AccountTransactionItem copyFrom(AccountTransactionItem setValuesFromThisInstance, boolean nonNullOnly) {
        return nonNullOnly
                ? PatchMapper.INSTANCE.patch(setValuesFromThisInstance, this)
                : PutMapper.INSTANCE.put(setValuesFromThisInstance, this);
    }

    @JsonIgnore
    public AccountTransactionItem withFlippedRetrievedFromDb() {
        return this.withRetrievedFromDb(BaseEntity.calculateRetrievedFromDb(retrievedFromDb));
    }

// --------------------------------------------------------------------------------

    public AccountTransactionItem setType(AccountTransactionType type) {
        this.type = type;
        if (null != type) transactionTypeCode = type.getTypeCode();
        return this;
    }

    public AccountTransactionItem setAccount(CashAccount account) {
        this.account = account;
        if (null != account) accountId = account.getId();
        return this;
    }

    public AccountTransactionItem setAccountTransaction(AccountTransaction accountTransaction) {
        this.accountTransaction = accountTransaction;
        if (null != accountTransaction) transactionId = accountTransaction.getId();
        return this;
    }

    public Long getTransactionId() {
        return null == accountTransaction ? transactionId : accountTransaction.getId();
    }
}
