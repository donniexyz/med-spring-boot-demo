package com.github.donniexyz.demo.med.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
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
public class AccountTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private BigDecimal transactionAmount;
    private String transactionType; // e.g., "Deposit," "Withdrawal"
    private LocalDateTime transactionDate;
    private String notes;

    @ManyToOne
    @JoinColumn(name = "type_code")
    private AccountTransactionType type;

    @ManyToOne
    @JoinColumn(name = "from_account_id")
    @JsonBackReference
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private CashAccount fromAccount;

    @ManyToOne
    @JoinColumn(name = "to_account_id")
    @JsonBackReference
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private CashAccount toAccount;

    // --------------------------------------------------------------------------------

    @JsonIgnore
    public AccountTransaction copy() {
        return this.withFromAccount(null == fromAccount ? null : fromAccount.copy())
                .withToAccount(null == toAccount ? null : toAccount.copy());
    }
}
