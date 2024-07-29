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

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.github.donniexyz.demo.med.entity.ref.BaseEntity;
import com.github.donniexyz.demo.med.entity.ref.IBaseEntity;
import com.github.donniexyz.demo.med.entity.ref.IHasCopy;
import com.github.donniexyz.demo.med.lib.PatchMapper;
import com.github.donniexyz.demo.med.lib.PutMapper;
import com.github.donniexyz.demo.med.lib.fieldsfilter.NonNullLazyFieldsFilter;
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
@FieldNameConstants(asEnum = true)
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
    @Transient
    @org.springframework.data.annotation.Transient
    @FieldNameConstants.Exclude
    private transient Boolean retrievedFromDb;

    @Version
    private Integer version;

    @CreationTimestamp
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    private OffsetDateTime createdDateTime;

    @CurrentTimestamp
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
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
                .setApplicableDebitAccountTypes(BaseEntity.cascadeSet(Fields.applicableDebitAccountTypes.name(), relFields, AccountType.class, applicableDebitAccountTypes))
                .setApplicableCreditAccountTypes(BaseEntity.cascadeSet(Fields.applicableCreditAccountTypes.name(), relFields, AccountType.class, applicableCreditAccountTypes))
                ;
    }

    @JsonIgnore
    public AccountTransactionType copyFrom(AccountTransactionType setValuesFromThisInstance, boolean nonNullOnly) {
        return nonNullOnly
                ? PatchMapper.INSTANCE.patch(setValuesFromThisInstance, this)
                : PutMapper.INSTANCE.put(setValuesFromThisInstance, this);
    }
}
