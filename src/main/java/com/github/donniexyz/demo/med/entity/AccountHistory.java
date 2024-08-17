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
import org.jetbrains.annotations.NotNull;

import javax.money.MonetaryAmount;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;


@WithBy
@With
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Accessors(chain = true)
@JsonInclude(value = JsonInclude.Include.CUSTOM, valueFilter = LazyFieldsFilter.class)
@FieldNameConstants
public class AccountHistory implements IBaseEntity<AccountHistory>, IHasCopy<AccountHistory>, Serializable {

    @Serial
    private static final long serialVersionUID = 3618940602969096296L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * <ul>
     * History types:
     * <li>TRANSACTION, inserted when transaction occurs</li>
     * <li>END_OF_DAY, inserted when EOD balance closure</li>
     * <li>END_OF_MONTH, inserted when EOM balance closure</li>
     * <li>END_OF_YEAR, inserted when EOY balance closure</li>
     * </ul>
     */
    private String historyType;
    private String transactionType; // e.g., "Deposit," "Withdrawal"

    @AttributeOverride(name = "amount", column = @Column(name = "acc_balance"))
    @AttributeOverride(name = "currency", column = @Column(name = "acc_ccy"))
    @CompositeType(MonetaryAmountType.class)
    private MonetaryAmount balance;

    private LocalDateTime transactionDate;
    private String description;

    @ManyToOne
    @JoinColumn(name = "account_id", foreignKey = @ForeignKey(name = "fk_AccHist_acc"))
    @JsonBackReference
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private CashAccount account;

    // ==============================================================
    // BaseEntity fields
    //---------------------------------------------------------------

    @Formula("true")
    @JsonIgnore
    @Transient
    @org.springframework.data.annotation.Transient
    @FieldNameConstants.Exclude
    private transient Boolean retrievedFromDb;

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

    // -------------------------------------------------------------------

    @JsonIgnore
    public AccountHistory copy(Boolean cascade) {
        return this.withRetrievedFromDb(BaseEntity.calculateRetrievedFromDb(retrievedFromDb))
                .setAccount(BaseEntity.cascade(cascade, CashAccount.class, account))
                ;
    }

    @JsonIgnore
    public AccountHistory copy(@NotNull List<String> relFields) {
        return this.withRetrievedFromDb(BaseEntity.calculateRetrievedFromDb(retrievedFromDb))
                .setAccount(BaseEntity.cascade(Fields.account, relFields, CashAccount.class, account))
                ;
    }

}
