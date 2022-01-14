package es.fermax.callmanagerservice.model;

import java.util.Arrays;

public enum CallRegistryTypeEnum {

    ALL("all"),
    AUTOON("auto_on"),
    MISSED_CALL("missed_call"),
    UNKOWN("unkown");

    public final String value;

    CallRegistryTypeEnum(String callRegistryType) {
        this.value = callRegistryType;
    }

    public static CallRegistryTypeEnum fromCallType(String value) {
        return Arrays.stream(values())
                .filter(bl -> bl.value.equalsIgnoreCase(value))
                .findFirst().orElse(UNKOWN);
    }

}
