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
import com.github.donniexyz.demo.med.entity.ref.BaseEntity;
import com.github.donniexyz.demo.med.entity.ref.IBaseEntity;
import com.github.donniexyz.demo.med.entity.ref.IHasCopy;
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
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table
@Accessors(chain = true)
@JsonInclude(value = JsonInclude.Include.CUSTOM, valueFilter = NonNullLazyFieldsFilter.class)
@Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@FieldNameConstants
public class AccountOwnerTypeApplicableToAccountType implements IBaseEntity<AccountOwnerTypeApplicableToAccountType>, IHasCopy<AccountOwnerTypeApplicableToAccountType>, Serializable {

    @Serial
    private static final long serialVersionUID = 3128913381381916069L;

    @Id
    @Column(name = "acc_tcode")
    private String accountTypeCode;

    @Id
    @Column(name = "own_tcode")
    private String ownerTypeCode;

    private String notes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "own_tcode", insertable = false, updatable = false,
            foreignKey = @ForeignKey(name = "fk_AccOwnTypeApplToAccType_ownType"))
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @JsonBackReference("ownerType")
    private AccountOwnerType ownerType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "acc_tcode", insertable = false, updatable = false,
            foreignKey = @ForeignKey(name = "fk_AccOwnTypeApplToAccType_accType"))
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @JsonBackReference("accountTypeToOwnerType")
    private AccountType accountType;

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

    // -------------------------------------------------------------------------------------------------

    @JsonIgnore
    public AccountOwnerTypeApplicableToAccountType copy(Boolean cascade) {
        return this.withRetrievedFromDb(BaseEntity.calculateRetrievedFromDb(retrievedFromDb))
                .setAccountType(BaseEntity.cascade(cascade, AccountType.class, accountType))
                .setOwnerType(BaseEntity.cascade(cascade, AccountOwnerType.class, ownerType))
                ;
    }

    @JsonIgnore
    public AccountOwnerTypeApplicableToAccountType copy(@NotNull List<String> relFields) {
        return this.withRetrievedFromDb(BaseEntity.calculateRetrievedFromDb(retrievedFromDb))
                .setAccountType(BaseEntity.cascade(Fields.accountType, relFields, AccountType.class, accountType))
                .setOwnerType(BaseEntity.cascade(Fields.ownerType, relFields, AccountOwnerType.class, ownerType))
                ;
    }

    public AccountOwnerTypeApplicableToAccountType setOwnerType(AccountOwnerType ownerType) {
        this.ownerType = ownerType;
        if (null != ownerType) this.ownerTypeCode = ownerType.getTypeCode();
        return this;
    }

    public AccountOwnerTypeApplicableToAccountType setAccountType(AccountType accountType) {
        this.accountType = accountType;
        if (null != accountType) this.accountTypeCode = accountType.getTypeCode();
        return this;
    }
}
