package com.github.donniexyz.demo.med.entity;

import com.github.donniexyz.demo.med.enums.IndividualGroupEnum;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import lombok.*;
import lombok.experimental.WithBy;

/**
 * <ul>
 * AccountOwnerType examples:
 * <li>BANK_SELF, the bank itself</li>
 * <li>STAFF, staff of the bank, might entitled to special rates, transaction fees discount, etc</li>
 * <li>INDI, individual customer</li>
 * <li>CORP, corporate or organization customer</li>
 * </ul>
 */
@WithBy
@With
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
public class AccountOwnerType {
    @Id
    private String typeCode;
    private String name;
    private String notes;

    /**
     * TRUE only if this owner is the bank/organization itself.
     */
    private Boolean self;

    @Enumerated(EnumType.STRING)
    private IndividualGroupEnum individualOrGroup;
}
