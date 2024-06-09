package com.github.donniexyz.demo.med.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
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
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Accessors(chain = true)
@JsonInclude(value = JsonInclude.Include.CUSTOM, valueFilter = LazyFieldsFilter.class)
public class AccountHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String transactionType; // e.g., "Deposit," "Withdrawal"

    @AttributeOverride(name = "amount", column = @Column(name = "acc_balance"))
    @AttributeOverride(name = "currency", column = @Column(name = "acc_ccy"))
    @CompositeType(MonetaryAmountType.class)
    private MonetaryAmount balance;

    private LocalDateTime transactionDate;
    private String description;

    @ManyToOne
    @JoinColumn(name = "account_id")
    @JsonBackReference
    private CashAccount account;

    // -------------------------------------------------------------------

    @JsonIgnore
    public AccountHistory copy() {
        return copy(null);
    }

    @JsonIgnore
    public AccountHistory copy(Boolean cascade) {
        return this.withAccount(null == account || Boolean.FALSE.equals(cascade) ? null : account.copy(false));
    }
}
