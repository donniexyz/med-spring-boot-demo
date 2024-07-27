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
