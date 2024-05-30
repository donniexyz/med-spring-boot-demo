package com.github.donniexyz.demo.med.repository;

import com.github.donniexyz.demo.med.entity.AccountHistory;
import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountHistoryRepository extends JpaRepositoryImplementation<AccountHistory, Long> {
}
