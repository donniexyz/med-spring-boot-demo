package com.github.donniexyz.demo.med.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.github.donniexyz.demo.med.lib.fieldsfilter.LazyFieldsFilter;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;
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
@Accessors(chain = true)
@JsonInclude(value = JsonInclude.Include.CUSTOM, valueFilter = LazyFieldsFilter.class)
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
        return copy(null);
    }

    @JsonIgnore
    public AccountOwner copy(Boolean cascade) {
        return this.withType(null == type || Boolean.FALSE.equals(cascade) ? null : type.copy(false));
    }

    @JsonIgnore
    public void copyFrom(AccountOwner setValuesFromThisInstance, boolean nonNullOnly) {
        if (!nonNullOnly || null != setValuesFromThisInstance.id)
            this.id = setValuesFromThisInstance.id;
        if (!nonNullOnly || null != setValuesFromThisInstance.firstName)
            this.firstName = setValuesFromThisInstance.firstName;
        if (!nonNullOnly || null != setValuesFromThisInstance.lastName)
            this.lastName = setValuesFromThisInstance.lastName;
        if (!nonNullOnly || null != setValuesFromThisInstance.email)
            this.email = setValuesFromThisInstance.email;
        if (!nonNullOnly || null != setValuesFromThisInstance.phoneNumber)
            this.phoneNumber = setValuesFromThisInstance.phoneNumber;
        if (!nonNullOnly || null != setValuesFromThisInstance.notes)
            this.notes = setValuesFromThisInstance.notes;
        if (!nonNullOnly || null != setValuesFromThisInstance.type)
            this.type = setValuesFromThisInstance.type;
    }
}
