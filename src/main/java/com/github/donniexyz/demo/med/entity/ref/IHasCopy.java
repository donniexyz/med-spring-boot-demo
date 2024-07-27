package com.github.donniexyz.demo.med.entity.ref;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.NonNull;

import java.util.List;

public interface IHasCopy<T> {

    @JsonIgnore
    default T copy() {
        return copy((Boolean) null);
    }

    @JsonIgnore
    T copy(Boolean cascade);

    @JsonIgnore
    T copy(@NonNull List<String> relFields);

}
