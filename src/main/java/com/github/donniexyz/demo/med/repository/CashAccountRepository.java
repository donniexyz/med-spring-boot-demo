package com.github.donniexyz.demo.med.repository;

import com.github.donniexyz.demo.med.entity.CashAccount;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface CashAccountRepository extends JpaRepositoryImplementation<CashAccount, Long> {

    @Query(value = "UPDATE AccountType SET id=:to WHERE id=:from")
    @Transactional
    @Modifying
    int changeId(Long from, Long to);
}
