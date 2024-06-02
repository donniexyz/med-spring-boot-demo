package com.github.donniexyz.demo.med.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.github.donniexyz.demo.med.lib.LazyFieldsFilter;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;
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
@Accessors(chain = true)
@JsonInclude(value = JsonInclude.Include.CUSTOM, valueFilter = LazyFieldsFilter.class)
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
