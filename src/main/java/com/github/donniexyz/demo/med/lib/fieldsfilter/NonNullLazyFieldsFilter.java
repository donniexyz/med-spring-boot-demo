package com.github.donniexyz.demo.med.lib.fieldsfilter;

import jakarta.persistence.Persistence;

public class NonNullLazyFieldsFilter {

    @Override
    public boolean equals(Object obj) {
        return obj == null || !Persistence.getPersistenceUtil().isLoaded(obj);
    }

}
