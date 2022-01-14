package es.fermax.callmanagerservice.enums;

/**
 * Call registry use to indicate call status for particular participant
 */
public enum CallStatusEnum {

    PICKED_UP("P"),
    MISSED("M"),
    REJECTED("R"),
    UNKOWN("U");

    public final String status;

    CallStatusEnum(String status) {
        this.status = status;
    }

}
