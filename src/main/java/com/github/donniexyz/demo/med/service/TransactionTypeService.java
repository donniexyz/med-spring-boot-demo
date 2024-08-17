package com.github.donniexyz.demo.med.service;

import com.github.donniexyz.demo.med.entity.AccountType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

//@Service
public class TransactionTypeService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<AccountType> findByTypeCodeIn(Collection<String> accountTypeCodes) {
        return null;
    }
}
