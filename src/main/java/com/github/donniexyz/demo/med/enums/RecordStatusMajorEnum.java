package com.github.donniexyz.demo.med.enums;

import lombok.Getter;

@Getter
public enum RecordStatusMajorEnum {

    /**
     * Active, can be used for transactions / operations
     */
    ACTIVE('A'),

    /**
     * Active but cannot do operation. Need to be unblocked first.
     */
    BLOCKED('B'),

    /**
     * Cannot be used for transaction, waiting for activation.
     */
    INACTIVE('I'),

    /**
     * Consider this record does not exists, ready to be deleted by internal process.
     */
    SOFT_DELETE('D'),

    /**
     * Consider this record does not exists, ready to be used by internal process.
     */
    RESERVED('R'),

    /**
     * Being processed by internal archival process. Not available for any other operation / transaction.
     */
    ARCHIVAL('V'),
    ;

    private final Character flag;

    RecordStatusMajorEnum(Character flag) {
        this.flag = flag;
    }

}
