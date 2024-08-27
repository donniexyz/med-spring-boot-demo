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

public interface IBaseEntity<T> {
    Boolean getRetrievedFromDb();

    Integer getVersion();

    java.time.OffsetDateTime getCreatedDateTime();

    java.time.OffsetDateTime getLastModifiedDate();

    T setRetrievedFromDb(Boolean retrievedFromDb);

    T setVersion(Integer version);

    T setCreatedDateTime(java.time.OffsetDateTime createdDateTime);

    T setLastModifiedDate(java.time.OffsetDateTime lastModifiedDate);

    Character getRecordStatusMajor();

    Character getStatusMinor();

    /**
     * Explaining the status of this record:
     * A: Active
     * I: Inactive
     * D: Soft Deleted (will be hidden from .findAll() because entities has @Where(statusMajor not in ['D', 'R', 'V'])
     * R: Reserved (on case bulk creation of records, but the records actually not yet in use)
     * V: Marked for archival
     */
    T setRecordStatusMajor(Character recordStatusMajor);

    /**
     * Further explaining the record status. Not handled by common libs. To be handled by individual lib.
     */
    T setStatusMinor(Character statusMinor);
}
