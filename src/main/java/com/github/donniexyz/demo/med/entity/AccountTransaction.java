package com.github.donniexyz.demo.med.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.github.donniexyz.demo.med.lib.fieldsfilter.LazyFieldsFilter;
import io.hypersistence.utils.hibernate.type.money.MonetaryAmountType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;
import lombok.experimental.WithBy;
import org.hibernate.annotations.CompositeType;

import javax.money.MonetaryAmount;
import java.time.LocalDateTime;

@WithBy
@With
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Accessors(chain = true)
@JsonInclude(value = JsonInclude.Include.CUSTOM, valueFilter = LazyFieldsFilter.class)
public class AccountTransaction {
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

    @ManyToOne
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

    // --------------------------------------------------------------------------------

    @JsonIgnore
    public AccountTransaction copy() {
        return copy(null);
    }

    @JsonIgnore
    public AccountTransaction copy(Boolean cascade) {
        return this
                .withType(null == type || Boolean.FALSE.equals(cascade) ? null : type.copy(false))
                .setDebitAccount(null == debitAccount || Boolean.FALSE.equals(cascade) ? null : debitAccount.copy(false))
                .setCreditAccount(null == creditAccount || Boolean.FALSE.equals(cascade) ? null : creditAccount.copy(false));
    }
}
