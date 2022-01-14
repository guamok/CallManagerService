package es.fermax.callmanagerservice.helper;

import es.fermax.callmanagerservice.controller.dto.*;
import es.fermax.callmanagerservice.enums.CallTypeEnum;

public class HelperCallService {

    public static StartCallDTO getStartCallDto(String id) {
        StartCallDTO startCallDTO = new StartCallDTO();
        startCallDTO.setDeviceId(id);
        startCallDTO.setCallAs("CallAs" + id);
        startCallDTO.setCallType(CallTypeEnum.INCOMING_CALL.value);
        return startCallDTO;
    }

    public static StartCallDTO getAutoonStartCallDto(String id) {
        StartCallDTO startCallDTO = getStartCallDto(id);
        startCallDTO.setCallType(CallTypeEnum.AUTOON.value);
        startCallDTO.setCallingTo("someToken");
        return startCallDTO;
    }

    public static RingToPreviewDTO getRingToPreviewDTO(String room, String appToken) {
        RingToPreviewDTO ringToPreviewDTO = new RingToPreviewDTO();
        ringToPreviewDTO.setRoomId(room);
        ringToPreviewDTO.setAppToken(appToken);

        return ringToPreviewDTO;
    }

    public static RegisterProductionVideoDTO getRegisterProductionVideoDTO(String room, String device) {
        RegisterProductionVideoDTO registerProductionVideoDTO = new RegisterProductionVideoDTO();
        registerProductionVideoDTO.setRoomId(room);
        registerProductionVideoDTO.setDeviceId(device);

        return registerProductionVideoDTO;
    }

    public static PickupDTO getPickupDTO(String room, String appToken) {
        PickupDTO pickupDTO = new PickupDTO();
        pickupDTO.setRoomId(room);
        pickupDTO.setAppToken(appToken);

        return pickupDTO;
    }

    public static HangupDTO getHangupDTO(String room, String appToken) {
        HangupDTO hangupDTO = new HangupDTO();
        hangupDTO.setRoomId(room);
        hangupDTO.setAppToken(appToken);

        return hangupDTO;
    }

    public static RejectDTO getRejectDTO(String room, String appToken) {
        RejectDTO rejectDTO = new RejectDTO();
        rejectDTO.setRoomId(room);
        rejectDTO.setAppToken(appToken);

        return rejectDTO;
    }

    public static EndcallDTO getEndCallDTO(String room, String reason) {
        EndcallDTO endCallDTO = new EndcallDTO();
        endCallDTO.setRoomId(room);
        endCallDTO.setReason(reason);

        return endCallDTO;
    }

    public static ParticipantCallDTO getParticipantCallDTO(String appToken) {
        ParticipantCallDTO participantCallDTO = new ParticipantCallDTO();
        participantCallDTO.setAppToken(appToken);

        return participantCallDTO;
    }

    public static CallRegistryDTO getHistoricCallDTO(String appToken, String deviceId, String status) {
        CallRegistryDTO callRegistryDTO = new CallRegistryDTO();

        return callRegistryDTO;
    }

}
