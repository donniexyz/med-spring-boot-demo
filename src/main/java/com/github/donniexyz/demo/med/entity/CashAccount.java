package com.github.donniexyz.demo.med.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.WithBy;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@WithBy
@With
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
public class CashAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private BigDecimal balance;
    private LocalDateTime lastTransactionDate;

    @OneToMany(mappedBy = "account")
    @JsonManagedReference
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<AccountHistory> accountHistories;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private AccountOwner accountOwner;

    // ------------------------------------------------------

    @JsonIgnore
    public CashAccount copy() {
        return this.withAccountOwner(null == accountOwner ? null : accountOwner.copy())
                .withAccountHistories(null == accountHistories ? null : accountHistories.stream().map(AccountHistory::copy).collect(Collectors.toList()));
    }
}
