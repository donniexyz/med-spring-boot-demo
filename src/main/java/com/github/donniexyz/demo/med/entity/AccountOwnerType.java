package com.github.donniexyz.demo.med.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.github.donniexyz.demo.med.enums.IndividualGroupEnum;
import com.github.donniexyz.demo.med.lib.fieldsfilter.LazyFieldsFilter;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import lombok.*;
import lombok.experimental.Accessors;
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
@Accessors(chain = true)
@JsonInclude(value = JsonInclude.Include.CUSTOM, valueFilter = LazyFieldsFilter.class)
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

    // ----------------------------------------------------------------------------------

    @JsonIgnore
    public AccountOwnerType copy() {
        return copy(null);
    }

    @JsonIgnore
    public AccountOwnerType copy(Boolean cascade) {
        return this.withTypeCode(this.typeCode);
    }
}
