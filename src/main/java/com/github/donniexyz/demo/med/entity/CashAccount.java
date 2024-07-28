package com.github.donniexyz.demo.med.entity;

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
import org.jetbrains.annotations.Nullable;

import javax.money.MonetaryAmount;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

@WithBy
@With
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Accessors(chain = true)
@JsonInclude(value = JsonInclude.Include.CUSTOM, valueFilter = LazyFieldsFilter.class)
@FieldNameConstants(asEnum = true)
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

    @OneToMany(mappedBy = "account")
    @JsonManagedReference
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<AccountHistory> accountHistories;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private AccountOwner accountOwner;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "type_code")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
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
                .setAccountOwner(BaseEntity.cascade(Fields.accountOwner.name(), relFields, AccountOwner.class, accountOwner))
                .setAccountType(BaseEntity.cascade(Fields.accountType.name(), relFields, AccountType.class, accountType))
                .setAccountHistories(BaseEntity.cascade(Fields.accountHistories.name(), relFields, AccountHistory.class, accountHistories))
                ;
    }

    @JsonIgnore
    public CashAccount copyFrom(CashAccount setValuesFromThisInstance, boolean nonNullOnly) {
        return nonNullOnly
                ? PatchMapper.INSTANCE.patch(setValuesFromThisInstance, this)
                : PutMapper.INSTANCE.put(setValuesFromThisInstance, this);
    }

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
