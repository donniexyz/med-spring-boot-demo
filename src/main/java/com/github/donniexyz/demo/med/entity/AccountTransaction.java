package com.github.donniexyz.demo.med.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.github.donniexyz.demo.med.lib.fieldsfilter.LazyFieldsFilter;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;
import lombok.experimental.WithBy;

import java.math.BigDecimal;
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
    private BigDecimal transactionAmount;
    private String label; // e.g., "Deposit," "Withdrawal"
    private LocalDateTime transactionDate;
    private String notes;

    @ManyToOne
    @JoinColumn(name = "type_code")
    private AccountTransactionType type;

    @ManyToOne
    @JoinColumn(name = "from_account_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private CashAccount fromAccount;

    @ManyToOne
    @JoinColumn(name = "to_account_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private CashAccount toAccount;

    // --------------------------------------------------------------------------------

    @JsonIgnore
    public AccountTransaction copy() {
        return copy(null);
    }

    @JsonIgnore
    public AccountTransaction copy(Boolean cascade) {
        return this
                .withType(null == type || Boolean.FALSE.equals(cascade) ? null : type.copy(false))
                .setFromAccount(null == fromAccount || Boolean.FALSE.equals(cascade) ? null : fromAccount.copy(false))
                .setToAccount(null == toAccount || Boolean.FALSE.equals(cascade) ? null : toAccount.copy(false));
    }
}
