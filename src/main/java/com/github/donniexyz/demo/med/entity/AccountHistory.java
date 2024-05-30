package com.github.donniexyz.demo.med.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.donniexyz.demo.med.entity.CashAccount;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.WithBy;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@WithBy
@With
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class AccountHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String transactionType; // e.g., "Deposit," "Withdrawal"
    private BigDecimal balance;
    private LocalDateTime transactionDate;
    private String description;

    @ManyToOne
    @JoinColumn(name = "account_id")
    private CashAccount account;

    // -------------------------------------------------------------------

    @JsonIgnore
    public AccountHistory copy() {
        return this.withAccount(null == account ? null : account.copy());
    }
}
