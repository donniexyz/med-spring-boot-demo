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

import com.github.donniexyz.demo.med.utils.time.MedJsonFormatForOffsetDateTime;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.github.donniexyz.demo.med.entity.ref.BaseEntity;
import com.github.donniexyz.demo.med.entity.ref.IBaseEntity;
import com.github.donniexyz.demo.med.entity.ref.IHasCopy;
import com.github.donniexyz.demo.med.lib.CashAccountUtilities;
import com.github.donniexyz.demo.med.lib.PatchMapper;
import com.github.donniexyz.demo.med.lib.PutMapper;
import com.github.donniexyz.demo.med.lib.fieldsfilter.LazyFieldsFilter;
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
import org.jetbrains.annotations.NotNull;

import javax.money.MonetaryAmount;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;

@WithBy
@With
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Accessors(chain = true)
@JsonInclude(value = JsonInclude.Include.CUSTOM, valueFilter = LazyFieldsFilter.class)
@FieldNameConstants
public class CashAccount implements IBaseEntity<CashAccount>, IHasCopy<CashAccount>, Serializable {

    @Serial
    private static final long serialVersionUID = 1968894442408558988L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;

    @AttributeOverride(name = "amount", column = @Column(name = "acc_bal"))
    @AttributeOverride(name = "currency", column = @Column(name = "acc_ccy"))
    @CompositeType(MonetaryAmountType.class)
    private MonetaryAmount accountBalance;

    private LocalDateTime lastTransactionDate;
    private String notes;

    /**
     * This will be used to insert history upon updates of cashAccount (if enabled).
     * This is not to be used to retrieve history! Retrieving history must be done via API with Pagination support.
     */
    @OneToMany(mappedBy = "account", cascade = CascadeType.MERGE)
    @JsonManagedReference
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<AccountHistory> accountHistories;

    @ManyToOne
    @JoinColumn(name = "owner_id", foreignKey = @ForeignKey(name = "fk_CashAcc_owner"))
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private AccountOwner accountOwner;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "type_code", foreignKey = @ForeignKey(name = "fk_CashAcc_type"))
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
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

    // ------------------------------------------------------

    @JsonIgnore
    public CashAccount copy(Boolean cascade) {
        return this
                .withRetrievedFromDb(BaseEntity.calculateRetrievedFromDb(retrievedFromDb))
                .setAccountOwner(BaseEntity.cascade(cascade, AccountOwner.class, accountOwner))
                .setAccountType(BaseEntity.cascade(cascade, AccountType.class, accountType))
                .setAccountHistories(BaseEntity.cascade(cascade, AccountHistory.class, accountHistories))
                ;
    }

    @JsonIgnore
    public CashAccount copy(@NotNull List<String> relFields) {
        return this
                .withRetrievedFromDb(BaseEntity.calculateRetrievedFromDb(retrievedFromDb))
                .setAccountOwner(BaseEntity.cascade(Fields.accountOwner, relFields, AccountOwner.class, accountOwner))
                .setAccountType(BaseEntity.cascade(Fields.accountType, relFields, AccountType.class, accountType))
                .setAccountHistories(BaseEntity.cascade(Fields.accountHistories, relFields, AccountHistory.class, accountHistories))
                ;
    }

    @JsonIgnore
    public CashAccount copyFrom(CashAccount setValuesFromThisInstance, boolean nonNullOnly) {
        return nonNullOnly
                ? PatchMapper.INSTANCE.patch(setValuesFromThisInstance, this)
                : PutMapper.INSTANCE.put(setValuesFromThisInstance, this);
    }

    // ---------------------------------------------------------------------------------------------

    @JsonIgnore
    public CashAccount debit(MonetaryAmount amount) {
        accountBalance = CashAccountUtilities.debit(accountBalance, amount, accountType.getBalanceSheetEntry());
        return this;
    }

    @JsonIgnore
    public CashAccount credit(MonetaryAmount amount) {
        accountBalance = CashAccountUtilities.credit(accountBalance, amount, accountType.getBalanceSheetEntry());
        return this;
    }
}
