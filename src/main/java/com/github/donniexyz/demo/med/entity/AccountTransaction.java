package com.github.donniexyz.demo.med.entity;

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
@FieldNameConstants(asEnum = true)
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
    @JoinColumn(name = "type_code")
    private AccountTransactionType type;

    @ManyToOne
    @JoinColumn(name = "dr_account_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private CashAccount debitAccount;

    @ManyToOne
    @JoinColumn(name = "cr_account_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private CashAccount creditAccount;

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

    // --------------------------------------------------------------------------------

    @JsonIgnore
    public AccountTransaction copy(Boolean cascade) {
        return this.withRetrievedFromDb(BaseEntity.calculateRetrievedFromDb(retrievedFromDb))
                .setType(BaseEntity.cascade(cascade, AccountTransactionType.class, type))
                .setDebitAccount(BaseEntity.cascade(cascade, CashAccount.class, debitAccount))
                .setCreditAccount(BaseEntity.cascade(cascade, CashAccount.class, creditAccount))
                ;
    }

    @Override
    public AccountTransaction copy(@NonNull List<String> relFields) {
        return this.withRetrievedFromDb(BaseEntity.calculateRetrievedFromDb(retrievedFromDb))
                .setType(BaseEntity.cascade(Fields.type.name(), relFields, AccountTransactionType.class, type))
                .setDebitAccount(BaseEntity.cascade(Fields.debitAccount.name(), relFields, CashAccount.class, debitAccount))
                .setCreditAccount(BaseEntity.cascade(Fields.creditAccount.name(), relFields, CashAccount.class, creditAccount))
                ;
    }
}
