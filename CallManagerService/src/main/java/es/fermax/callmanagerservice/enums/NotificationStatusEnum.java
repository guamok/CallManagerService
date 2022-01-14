package es.fermax.callmanagerservice.enums;

public enum NotificationStatusEnum {

    DRAFT("D"),
    SENT("S"),
    ERROR("E"),
    ATTENDED("A");

    public final String status;

    NotificationStatusEnum(String status) {
        this.status = status;
    }

}
