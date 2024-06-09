package com.github.donniexyz.demo.med.repository;

import com.github.donniexyz.demo.med.entity.AccountType;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RepositoryRestResource()
public interface AccountTypeRepository extends JpaRepositoryImplementation<AccountType, String> {

    @Query(value = "UPDATE AccountType SET id=:to WHERE id=:from")
    @Transactional
    @Modifying
    int changeId(String from, String to);
}
