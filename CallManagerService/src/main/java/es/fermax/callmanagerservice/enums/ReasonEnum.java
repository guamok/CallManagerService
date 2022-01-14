package es.fermax.callmanagerservice.enums;

import java.util.Arrays;

public enum ReasonEnum {

    LAST_PARTICIPANT_REJECTED("last_participant_rejected"),
    END_PARTICIPANT_CONVERSATION("end_participant_conversation"),
    MANAGED_CALL("managed_call"),
    MISSED_CALL("missed_call"),
    UNKOWN("unkown");

    public final String value;

    ReasonEnum(String reasonType) {
        this.value = reasonType;
    }

    public static ReasonEnum fromReasonType(String value) {
        return Arrays.stream(values())
                .filter(bl -> bl.value.equalsIgnoreCase(value))
                .findFirst().orElse(UNKOWN);
    }

    public static boolean isValid(String value) {
        return !(ReasonEnum.fromReasonType(value).equals(ReasonEnum.UNKOWN));
    }
}
