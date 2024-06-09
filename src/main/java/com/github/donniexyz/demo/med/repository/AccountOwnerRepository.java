package com.github.donniexyz.demo.med.repository;

import com.github.donniexyz.demo.med.entity.AccountOwner;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RepositoryRestResource()
public interface AccountOwnerRepository extends JpaRepositoryImplementation<AccountOwner, Long> {

    @Query(value = "UPDATE AccountOwner SET id=:to WHERE id=:from")
    @Transactional
    @Modifying
    int changeId(Long from, Long to);
}
