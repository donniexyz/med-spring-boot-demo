package com.github.donniexyz.demo.med.repository;

import com.github.donniexyz.demo.med.entity.AccountTransactionType;
import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

@Repository
@RepositoryRestResource()
public interface AccountTransactionTypeRepository extends JpaRepositoryImplementation<AccountTransactionType, String> {
}
