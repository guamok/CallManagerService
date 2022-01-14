package es.fermax.callmanagerservice.enums;

import java.util.Arrays;

/**
 * Call Status to persist
 */
public enum StatusEnum {

    INIT("I"),
    RING("R"),
    PREVIEW("P"),
    CONVERSATION("C"),
    MISSED("M"),
    FINISHED("F"),
    ERROR("ERR"),
    UNKOWN("U");

    public final String status;

    StatusEnum(String status) {
        this.status = status;
    }

    public static StatusEnum fromStatus(String status) {
        return Arrays.stream(values())
                .filter(bl -> bl.status.equalsIgnoreCase(status))
                .findFirst().orElse(UNKOWN);
    }
}
