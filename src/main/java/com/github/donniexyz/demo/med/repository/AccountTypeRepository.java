package com.github.donniexyz.demo.med.repository;

import com.github.donniexyz.demo.med.entity.AccountType;
import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

@Repository
@RepositoryRestResource()
public interface AccountTypeRepository extends JpaRepositoryImplementation<AccountType, String> {
}
