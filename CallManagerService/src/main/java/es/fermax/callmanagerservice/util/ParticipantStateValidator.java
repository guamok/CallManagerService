package es.fermax.callmanagerservice.util;


import es.fermax.callmanagerservice.model.Participant;
import es.fermax.callmanagerservice.model.ParticipantStatusEnum;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class ParticipantStateValidator {

    private ParticipantStateValidator() {
    }

    /**
     * State participant
     *
     * @param participant - participant room
     * @return - final state
     */
    public static String participantStatus(Participant participant) {
        String status = null;

        if (participant.getMissedCallTime() != null || participant.getHangupTime() != null) {
            status = participant.getMissedCallTime() != null ? ParticipantStatusEnum.MISSED.status : ParticipantStatusEnum.FINISHED.status;
        } else if (participant.getPickupTime() != null || participant.getPreviewTime() != null) {
            status = participant.getPickupTime() != null ? ParticipantStatusEnum.PICKED_UP.status : ParticipantStatusEnum.PREVIEW.status;
        } else if (participant.getRingingTime() != null) {
            status = ParticipantStatusEnum.RINGING.status;
        } else if (participant.getRejectTime() != null || participant.getCreationDate() != null) {
            status = participant.getRejectTime() != null ? ParticipantStatusEnum.REJECT.status : ParticipantStatusEnum.INIT.status;
        }

        return status;
    }

    /**
     * iaApp participant
     *
     * @param token - participant Token String
     * @return - final boolean
     */
    public static Boolean isWebParticipant(String token) {

        return  token.substring(0,3).equalsIgnoreCase("web") ? true : false;
    }


}
