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
import com.github.donniexyz.demo.med.entity.ref.BaseEntity;
import com.github.donniexyz.demo.med.entity.ref.IBaseEntity;
import com.github.donniexyz.demo.med.entity.ref.IHasCopy;
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

import javax.money.MonetaryAmount;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;

@WithBy
@With
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Accessors(chain = true)
@JsonInclude(value = JsonInclude.Include.CUSTOM, valueFilter = LazyFieldsFilter.class)
@FieldNameConstants
public class AccountTransaction implements IBaseEntity<AccountTransaction>, IHasCopy<AccountTransaction>, Serializable {

    @Serial
    private static final long serialVersionUID = -4943817908220810093L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @AttributeOverride(name = "amount", column = @Column(name = "trx_amount"))
    @AttributeOverride(name = "currency", column = @Column(name = "trx_ccy"))
    @CompositeType(MonetaryAmountType.class)
    private MonetaryAmount transactionAmount;

    private String label; // e.g., "Deposit," "Withdrawal"
    private LocalDateTime transactionDate;
    private String notes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "type_code", foreignKey = @ForeignKey(name = "fk_AccTrx_type"))
    private AccountTransactionType type;

//    @ManyToOne
//    @JoinColumn(name = "dr_account_id")
//    @ToString.Exclude
//    @EqualsAndHashCode.Exclude
//    private CashAccount debitAccount;
//
//    @ManyToOne
//    @JoinColumn(name = "cr_account_id")
//    @ToString.Exclude
//    @EqualsAndHashCode.Exclude
//    private CashAccount creditAccount;

    @OneToMany(mappedBy = "accountTransaction", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<AccountTransactionItem> items;

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
    public AccountTransaction copy(Boolean cascade) {
        return this.withRetrievedFromDb(BaseEntity.calculateRetrievedFromDb(retrievedFromDb))
                .setType(BaseEntity.cascade(cascade, AccountTransactionType.class, type))
                .setItems(BaseEntity.cascade(cascade, AccountTransactionItem.class, items))
                ;
    }

    @Override
    public AccountTransaction copy(@NonNull List<String> relFields) {
        return this.withRetrievedFromDb(BaseEntity.calculateRetrievedFromDb(retrievedFromDb))
                .setType(BaseEntity.cascade(Fields.type, relFields, AccountTransactionType.class, type))
                .setItems(BaseEntity.cascade(Fields.items, relFields, AccountTransactionItem.class, items))
                ;
    }
}
