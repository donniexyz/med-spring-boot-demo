package com.github.donniexyz.demo.med.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;
import lombok.experimental.WithBy;

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

    // Other relevant fields (e.g., address, phone number, etc.)

    // --------------------------------------------------------------------------

    @JsonIgnore
    public AccountOwner copy() {
        return this.withId(this.id);
    }
}
