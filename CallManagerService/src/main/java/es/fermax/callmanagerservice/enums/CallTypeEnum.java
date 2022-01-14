package es.fermax.callmanagerservice.enums;

import java.util.Arrays;

public enum CallTypeEnum {

    AUTOON("auto_on"),
    CHANGE_VIDEO("change_video"),
    INCOMING_CALL("incoming_call"),
    UNKOWN("unkown");

    public final String value;

    CallTypeEnum(String callType) {
        this.value = callType;
    }

    public static CallTypeEnum fromCallType(String value) {
        return Arrays.stream(values())
                .filter(bl -> bl.value.equalsIgnoreCase(value))
                .findFirst().orElse(UNKOWN);
    }

    public static boolean isValid(String value) {
        return !(CallTypeEnum.fromCallType(value).equals(CallTypeEnum.UNKOWN));
    }
}
