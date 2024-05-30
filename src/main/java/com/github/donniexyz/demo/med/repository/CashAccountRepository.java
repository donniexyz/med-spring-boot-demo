package com.github.donniexyz.demo.med.repository;

import com.github.donniexyz.demo.med.entity.CashAccount;
import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;
import org.springframework.stereotype.Repository;

@Repository
public interface CashAccountRepository extends JpaRepositoryImplementation<CashAccount, Long> {
}
