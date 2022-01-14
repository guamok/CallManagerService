package es.fermax.callmanagerservice.util;

import es.fermax.callmanagerservice.enums.StatusEnum;
import es.fermax.callmanagerservice.model.Call;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CallStateValidator {

    private CallStateValidator() {
    }

    /**
     * State machine
     *
     * @param call  - current call
     * @param state - future state
     * @return - final state
     */
    public static String validateStatusCall(Call call, String state) {

        String statusBefore = call.getStatus();

        boolean isInit = StatusEnum.INIT.status.equalsIgnoreCase(statusBefore);
        boolean isRing = StatusEnum.RING.status.equalsIgnoreCase(statusBefore);
        boolean isPreview = StatusEnum.PREVIEW.status.equalsIgnoreCase(statusBefore);
        boolean isConversation = StatusEnum.CONVERSATION.status.equalsIgnoreCase(statusBefore);

        switch (StatusEnum.fromStatus((state))) {
            case INIT:
                if (statusBefore == null) return state;
                break;
            case RING:
                if (isInit) return state;
                break;
            case PREVIEW:
                if (isInit || isRing) return state;
                break;
            case CONVERSATION:
                if (isPreview) return state;
                break;
            case MISSED:
                if (isRing || isPreview) return state;
                break;
            case FINISHED:
            case ERROR:
                if (isInit || isRing || isPreview || isConversation) return state;
                break;
            default:
                break;
        }

        log.error("Transition of CALL state is not allowed. ( RoomId {}  Transition from {} to {}.", call.getRoomId(), statusBefore, state);
        return statusBefore;
    }
}
