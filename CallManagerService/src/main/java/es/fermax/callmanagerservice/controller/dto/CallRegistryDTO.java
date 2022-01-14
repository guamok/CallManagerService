package es.fermax.callmanagerservice.controller.dto;

import es.fermax.callmanagerservice.enums.CallStatusEnum;
import es.fermax.callmanagerservice.model.Call;
import es.fermax.callmanagerservice.model.Participant;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZoneId;
import java.time.ZonedDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CallRegistryDTO {


    @ApiModelProperty(required = true, example = "ci-device-999", notes = "Connected Device ID")
    String deviceId;

    @ApiModelProperty(required = true, example = "2021-11-15", notes = "Date and time of call")
    ZonedDateTime callDate;

    @ApiModelProperty(required = true, example = "R", notes = "Register Call - P -> Picked Up, M -> Missed, R -> Rejected, U -> Unknown")
    String registerCall;

    @ApiModelProperty(required = true, example = "false", notes = "AutoOn")
    Boolean isAutoon;


    public CallRegistryDTO(Participant participant, Call call) {
        if (null != call) {
            this.deviceId = call.getInitDeviceId();
            this.isAutoon = call.isAutoon();
        }

        if (null != participant.getSentInvitationTime()) {
            this.callDate = ZonedDateTime.of(participant.getSentInvitationTime(), ZoneId.of("Europe/Madrid"));
        }

        if (null != participant.getPickupTime()) {
            this.registerCall = CallStatusEnum.PICKED_UP.status;
        } else if (null != participant.getRejectTime()) {
            this.registerCall = CallStatusEnum.REJECTED.status;
        } else if (null != participant.getMissedCallTime()) {
            this.registerCall = CallStatusEnum.MISSED.status;
        } else {
            this.registerCall = CallStatusEnum.UNKOWN.status;
        }
    }


    public static CallRegistryDTO parserParticipantToHistoricaCallDto(Participant participant, Call call) {
        return new CallRegistryDTO(participant, call);
    }

}
