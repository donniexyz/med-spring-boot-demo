/*
 * MIT License
 *
 * Copyright (c) 2024 (https://github.com/donniexyz)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.github.donniexyz.demo.med.entity.ref;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Transient;
import jakarta.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.experimental.Accessors;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.CurrentTimestamp;
import org.hibernate.annotations.Formula;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * This class is available for reference only.
 * Because it is not convenient for Entity class to "extends BaseEntity",
 * we will use interface generated from this class and copy the fields here into the real Entity class.
 */
@Data
@SuperBuilder
@MappedSuperclass
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@FieldNameConstants(asEnum = true)
public class BaseEntity implements IBaseEntity {

    // ==============================================================
    // BaseEntity fields
    //---------------------------------------------------------------

    @Formula("true")
    @JsonIgnore
    @Transient
    @org.springframework.data.annotation.Transient
    @FieldNameConstants.Exclude
    private transient Boolean retrievedFromDb;

    @Version
    private Integer version;

    @CreationTimestamp
    private OffsetDateTime createdDateTime;

    @CurrentTimestamp
    private OffsetDateTime lastModifiedDate;

    /**
     * Explaining the status of this record:
     * A: Active
     * I: Inactive
     * D: Soft Deleted (will be hidden from .findAll() because entities has @Where(statusMajor not in ['D', 'R', 'V'])
     * R: Reserved (on case bulk creation of records, but the records actually not yet in use)
     * V: Marked for archival
     */
    private Character recordStatusMajor;

    /**
     * Further explaining the record status. Not handled by common libs. To be handled by individual lib.
     */
    private Character statusMinor;


    // =================================================================================================

    @Nullable
    public static Boolean calculateRetrievedFromDb(Boolean retrievedFromDb) {
        return null != retrievedFromDb ? false : null;
    }

    // =================================================================================================
    // cascade copy() handlers

    @NotNull
    public static List<String> filterRelFields(String prefix, List<String> relFields) {
        String prefixWithDot = prefix + ".";
        return relFields.stream().filter(rel -> rel.startsWith(prefixWithDot)).map(ao -> ao.substring(prefixWithDot.length())).toList();
    }

    /**
     * ManyToOne cascade handler
     *
     * @param fieldName
     * @param relFields
     * @param tClass
     * @param objectThatHasCopy
     * @param <T>
     * @return
     */
    @Nullable
    public static <T extends IHasCopy<T>> T cascade(String fieldName, List<String> relFields, @NonNull Class<T> tClass, @Nullable T objectThatHasCopy) {
        return null == objectThatHasCopy || !relFields.contains(fieldName) ? null : objectThatHasCopy.copy(BaseEntity.filterRelFields(fieldName, relFields));
    }

    /**
     * OneToMany cascade handler
     *
     * @param fieldName
     * @param relFields
     * @param tClass
     * @param listOfObjectThatHasCopy
     * @param <T>
     * @return
     */
    @Nullable
    public static <T extends IHasCopy<T>> List<T> cascade(String fieldName, List<String> relFields, @NonNull Class<T> tClass, @Nullable List<T> listOfObjectThatHasCopy) {
        return null == listOfObjectThatHasCopy || !relFields.contains(fieldName) ? null : listOfObjectThatHasCopy.stream().map(objectThatHasCopy -> objectThatHasCopy.copy(BaseEntity.filterRelFields(fieldName, relFields))).collect(Collectors.toList());
    }

    /**
     * OneToMany cascade handler
     *
     * @param fieldName
     * @param relFields
     * @param tClass
     * @param setOfObjectThatHasCopy
     * @param <T>
     * @return
     */
    @Nullable
    public static <T extends IHasCopy<T>> Set<T> cascadeSet(String fieldName, List<String> relFields, @NonNull Class<T> tClass, @Nullable Set<T> setOfObjectThatHasCopy) {
        return null == setOfObjectThatHasCopy || !relFields.contains(fieldName) ? null : setOfObjectThatHasCopy.stream().map(objectThatHasCopy -> objectThatHasCopy.copy(BaseEntity.filterRelFields(fieldName, relFields))).collect(Collectors.toSet());
    }

    /**
     * ManyToOne cascade handler
     *
     * @param cascade
     * @param tClass
     * @param objectThatHasCopy
     * @param <T>
     * @return
     */
    @Nullable
    public static <T extends IHasCopy<T>> T cascade(Boolean cascade, @NonNull Class<T> tClass, T objectThatHasCopy) {
        return null == objectThatHasCopy || Boolean.FALSE.equals(cascade) ? null : objectThatHasCopy.copy(false);
    }

    /**
     * OneToMany cascade handler
     *
     * @param cascade
     * @param tClass
     * @param listOfObjectThatHasCopy
     * @param <T>
     * @return
     */
    @Nullable
    public static <T extends IHasCopy<T>> List<T> cascade(Boolean cascade, @NonNull Class<T> tClass, List<T> listOfObjectThatHasCopy) {
        return null == listOfObjectThatHasCopy || !Boolean.TRUE.equals(cascade) ? null : listOfObjectThatHasCopy.stream().map(objectThatHasCopy -> objectThatHasCopy.copy(false)).collect(Collectors.toList());
    }

    /**
     * OneToMany cascade handler
     *
     * @param cascade
     * @param tClass
     * @param setOfObjectThatHasCopy
     * @param <T>
     * @return
     */
    @Nullable
    public static <T extends IHasCopy<T>> Set<T> cascadeSet(Boolean cascade, @NonNull Class<T> tClass, Set<T> setOfObjectThatHasCopy) {
        return null == setOfObjectThatHasCopy || !Boolean.TRUE.equals(cascade) ? null : setOfObjectThatHasCopy.stream().map(objectThatHasCopy -> objectThatHasCopy.copy(false)).collect(Collectors.toSet());
    }
}
