package com.github.donniexyz.demo.med.lib;

import jakarta.persistence.Persistence;

public class LazyFieldsFilter {

    @Override
    public boolean equals(Object obj) {
        return !Persistence.getPersistenceUtil().isLoaded(obj);
    }

}
