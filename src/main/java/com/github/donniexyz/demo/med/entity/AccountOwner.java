package com.github.donniexyz.demo.med.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.WithBy;

/**
 * <ul>
 * Example of AccountOwner:
 * <li>Finance Dept, id=0, type=BANK_SELF, notes=the bank itself</li>
 * <li>Mr Clerk, type=STAFF, notes=example of individual customer</li>
 * <li>Individual customer A, type=INDI, notes=example of individual customer</li>
 * <li>ACME corp, type=CORP, notes=example of corporate customer</li>
 * </ul>
 */
@WithBy
@With
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class AccountOwner {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;

    private String notes;

    // Other relevant fields (e.g., address, phone number, etc.)

    @ManyToOne
    @JoinColumn(name = "type_code")
    private AccountOwnerType type;

    // --------------------------------------------------------------------------

    @JsonIgnore
    public AccountOwner copy() {
        return this.withId(this.id);
    }
}
