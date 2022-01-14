package es.fermax.callmanagerservice.model;

public enum ParticipantStatusEnum {


    PICKED_UP("CONVERSATION"),
    MISSED("MISSED"),
    FINISHED("FINISHED"),
    PREVIEW("PREVIEW"),
    RINGING("RINGING"),
    REJECT("REJECT"),
    INIT("INIT"),
    UNKOWN("U");

    public final String status;


    ParticipantStatusEnum(String status) {
        this.status = status;
    }
}
