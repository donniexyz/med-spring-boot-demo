package com.github.donniexyz.demo.med.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;

@Embeddable
public class AccountOwnerTypeApplicableToAccountTypeCompositeKey implements Serializable {

    @Column(name = "acc_tcode")
    private String accountTypeCode;

    @Column(name = "own_tcode")
    private String ownerTypeCode;

}
