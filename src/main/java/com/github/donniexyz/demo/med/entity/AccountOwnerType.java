package com.github.donniexyz.demo.med.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.github.donniexyz.demo.med.entity.ref.BaseEntity;
import com.github.donniexyz.demo.med.entity.ref.IBaseEntity;
import com.github.donniexyz.demo.med.entity.ref.IHasCopy;
import com.github.donniexyz.demo.med.enums.IndividualGroupEnum;
import com.github.donniexyz.demo.med.lib.fieldsfilter.LazyFieldsFilter;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;
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

/**
 * <ul>
 * AccountOwnerType examples:
 * <li>BANK_SELF, the bank itself</li>
 * <li>STAFF, staff of the bank, might entitled to special rates, transaction fees discount, etc</li>
 * <li>INDI, individual customer</li>
 * <li>CORP, corporate or organization customer</li>
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
@JsonInclude(value = JsonInclude.Include.CUSTOM, valueFilter = LazyFieldsFilter.class)
@Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class AccountOwnerType implements IBaseEntity<AccountOwnerType>, IHasCopy<AccountOwnerType>, Serializable {

    @Serial
    private static final long serialVersionUID = -5832102092513250717L;

    @Id
    private String typeCode;
    private String name;
    private String notes;

    /**
     * TRUE only if this owner is the bank/organization itself.
     */
    private Boolean self;

    @Enumerated(EnumType.STRING)
    private IndividualGroupEnum individualOrGroup;

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

    // ----------------------------------------------------------------------------------

    @JsonIgnore
    public AccountOwnerType copy(Boolean cascade) {
        return this
                .withRetrievedFromDb(BaseEntity.calculateRetrievedFromDb(retrievedFromDb))
                ;
    }

    @JsonIgnore
    public AccountOwnerType copy(@NotNull List<String> relFields) {
        return this
                .withRetrievedFromDb(BaseEntity.calculateRetrievedFromDb(retrievedFromDb))
                ;
    }

    @JsonIgnore
    public void copyFrom(AccountOwnerType setValuesFromThisInstance, boolean nonNullOnly) {
        if (!nonNullOnly || null != setValuesFromThisInstance.typeCode)
            this.typeCode = setValuesFromThisInstance.typeCode;
        if (!nonNullOnly || null != setValuesFromThisInstance.name)
            this.name = setValuesFromThisInstance.name;
        if (!nonNullOnly || null != setValuesFromThisInstance.notes)
            this.notes = setValuesFromThisInstance.notes;
        if (!nonNullOnly || null != setValuesFromThisInstance.self)
            this.self = setValuesFromThisInstance.self;
        if (!nonNullOnly || null != setValuesFromThisInstance.individualOrGroup)
            this.individualOrGroup = setValuesFromThisInstance.individualOrGroup;
    }
}
