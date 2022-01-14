package es.fermax.callmanagerservice.service;

import es.fermax.callmanagerservice.controller.dto.*;
import es.fermax.callmanagerservice.enums.ReasonEnum;
import es.fermax.callmanagerservice.enums.StatusEnum;
import es.fermax.callmanagerservice.exception.NotFoundException;
import es.fermax.callmanagerservice.helper.HelperCallService;
import es.fermax.callmanagerservice.model.Call;
import es.fermax.callmanagerservice.repo.CallRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.verify;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
public class CallServiceTest {

    private static final String DEVICEID = "device123";
    private static final String ROOM = "room123";
    private static final String CALL_ID = "id123";


    @InjectMocks
    CallService callService;

    @Mock
    CallRepository repo;

    @Mock
    ParticipantService participantService;
    
    private Call call;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        call = new Call();
        call.setInitDeviceId(DEVICEID);
        call.setRoomId(ROOM);
    }

//    @Test
//    public void givenInitializeCall_happyPath_ReturnOK() throws Exception {
//
//        StartCallDTO startCallDTO = HelperCallService.getStartCallDto(DEVICEID);
//        // mock
//        Mockito.doNothing().when(startCallSender).sendMessage(anyString(), anyString(), anyString(), any());
//
//        // when
//        String room = callService.startCall(startCallDTO);
//
//        // then
//        assertNotNull(room);
//        assertTrue(room.contains(DEVICEID));
//    }

//    @Test
//    public void givenInitializeAutoonCall_happyPath_ReturnOK() throws Exception {
//
//        StartCallDTO startCallDTO = HelperCallService.getAutoonStartCallDto(DEVICEID);
//        // mock
//        Mockito.doNothing().when(startCallSender).sendMessage(anyString(), anyString(), anyString(), any(), anyString(), any());
//
//        // when
//        String room = callService.startCall(startCallDTO);
//
//        // then
//        assertNotNull(room);
//        assertTrue(room.contains(DEVICEID));
//
//    }

    @Test
    public void givenCall_whenGetCallbyRoomId_ThenReturnCall() {
        // mock
        given(repo.findByRoomId(anyString())).willReturn(Optional.of(call));

        // when
        Optional<Call> callOptional = repo.findByRoomId(call.getRoomId());

        // then
        assertEquals(callOptional, Optional.of(call));
    }

    @Test
    public void givenRegisterStart_happyPath_ThenReturnRoomId() throws Exception {
        RegisterProductionVideoDTO registerProductionVideoDTO = new RegisterProductionVideoDTO();

        registerProductionVideoDTO.setRoomId(ROOM);

        // mock
        given(repo.findByRoomId(anyString())).willReturn(Optional.ofNullable(call));

        // when
        callService.registerVideoProduce(registerProductionVideoDTO);

        // then
        verify(repo, atLeast(1)).findByRoomId(anyString());
        verify(repo, atLeast(1)).save(any());
    }

    @Test
    public void givenRegisterStart_throwsException() {
        RegisterProductionVideoDTO registerProductionVideoDTO = new RegisterProductionVideoDTO();
        registerProductionVideoDTO.setRoomId(ROOM);

        given(repo.findByRoomId(anyString())).willReturn(Optional.ofNullable(null));

        // when
        Exception exception = assertThrows(Exception.class, () -> {
            callService.registerVideoProduce(registerProductionVideoDTO);
        });

        assertNotNull(exception.getMessage());
        assertNotEquals("", exception.getMessage());
    }

    @Test
    public void givenRingToPreview_happyPath_ThenOK() throws Exception {

        RingToPreviewDTO ringToPreviewDTO = HelperCallService.getRingToPreviewDTO("room", "device");

        // mock
        given(repo.findByRoomId(anyString())).willReturn(Optional.ofNullable(call));
        given(repo.save(any())).willReturn(call);

        given(participantService.updateParticipantPreviewTime(any(), anyString())).willReturn(true);

        // when
        callService.ringToPreview(ringToPreviewDTO);

        // then
        verify(repo, atLeast(1)).findByRoomId(anyString());
        verify(repo, atLeast(1)).save(any());
    }

    @Test
    public void givenRingToPreview_throwsException() throws Exception {
        RingToPreviewDTO ringToPreviewDTO = HelperCallService.getRingToPreviewDTO("room", "device");

        given(repo.findByRoomId(anyString())).willReturn(Optional.ofNullable(null));

        // when
        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            callService.ringToPreview(ringToPreviewDTO);
        });

        assertNotNull(exception.getMessage());
        assertNotEquals("", exception.getMessage());
    }

    @Test
    public void givenRingToPreview_noParticipant_throwsException() {
        RingToPreviewDTO ringToPreviewDTO = HelperCallService.getRingToPreviewDTO("room", "device");

        given(repo.findByRoomId(anyString())).willReturn(Optional.ofNullable(null));

        // when
        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            callService.ringToPreview(ringToPreviewDTO);
        });

        assertNotNull(exception.getMessage());
        assertNotEquals("", exception.getMessage());
    }

    @Test
    public void givenPickup_happyPath_ThenOK() throws Exception {
        PickupDTO pickupDTO = HelperCallService.getPickupDTO("room", "device");

        // mock
        given(repo.findByRoomId(anyString())).willReturn(Optional.ofNullable(call));
        given(participantService.updateParticipantPickupTime(any(), anyString())).willReturn(true);

        // when
        callService.pickUp(pickupDTO);

        // then
        verify(repo, atLeast(1)).findByRoomId(anyString());
        verify(repo, atLeast(1)).save(any());
    }

    @Test
    public void givenPickup_throwsException() throws Exception {
        PickupDTO pickupDTO = HelperCallService.getPickupDTO("room", "device");

        given(repo.findByRoomId(anyString())).willReturn(Optional.ofNullable(null));

        // when
        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            callService.pickUp(pickupDTO);
        });

        assertNotNull(exception.getMessage());
        assertNotEquals("", exception.getMessage());
    }

    @Test
    public void givenPickup_noParticipant_throwsException() throws Exception {
        PickupDTO pickupDTO = HelperCallService.getPickupDTO("room", "device");

        given(repo.findByRoomId(anyString())).willReturn(Optional.ofNullable(null));
        given(participantService.updateParticipantPreviewTime(any(), anyString())).willReturn(false);

        // when
        Exception exception = assertThrows(Exception.class, () -> {
            callService.pickUp(pickupDTO);
        });

        assertNotNull(exception.getMessage());
        assertNotEquals("", exception.getMessage());
    }

    @Test
    public void givenStartRinging_NoCallExist() throws Exception {
        given(repo.findByRoomId(anyString())).willReturn(Optional.ofNullable(null));

        // when
        Exception exception = assertThrows(Exception.class, () -> {
            callService.registerStartRinging(anyString(), anyString());
        });

        assertNotNull(exception.getMessage());
        assertNotEquals("", exception.getMessage());
    }

    @Test
    public void givenStartRinging_UpdatedCallAndParticipant_OK() throws Exception {
        String APPTOKEN = "appToken1";

        // mock
        given(repo.findByRoomId(ROOM)).willReturn(Optional.ofNullable(call));
        given(participantService.updateParticipantRingingTime(any(), anyString())).willReturn(true);

        callService.registerStartRinging(ROOM, APPTOKEN);
        verify(repo, atLeast(1)).findByRoomId(ROOM);
        verify(repo, atLeast(1)).save(any());
    }

    @Test
    public void givenStartRinging_throwsException() throws Exception {
        String APPTOKEN = "appToken1";

        given(repo.findByRoomId(ROOM)).willReturn(Optional.ofNullable(null));

        // when
        Exception exception = assertThrows(Exception.class, () -> {
            callService.registerStartRinging(ROOM, APPTOKEN);
        });

        assertNotNull(exception.getMessage());
        assertNotEquals("", exception.getMessage());
    }

    @Test
    public void givenEndCallWithPreviewStatus_UpdatedCallAndParticipant_OK() throws Exception {
        Call myCall = new Call();
        myCall.setRoomId(ROOM);
        myCall.setStatus(StatusEnum.PREVIEW.status);
        myCall.setFinishedTime(null);

        EndcallDTO endcallDTO = HelperCallService.getEndCallDTO(ROOM, ReasonEnum.END_PARTICIPANT_CONVERSATION.value);

        // mock
        given(repo.findByRoomId(ROOM)).willReturn(Optional.of(myCall));

        // then
        callService.endCall(endcallDTO);
        verify(repo, atLeast(1)).findByRoomId(ROOM);
        verify(repo, atLeast(1)).save(any());
    }

    @Test
    public void givenEndCallWithConversationStatus_UpdatedCallAndParticipant_OK() throws Exception {
        Call myCall = new Call();
        myCall.setRoomId(ROOM);
        myCall.setStatus(StatusEnum.PREVIEW.status);
        myCall.setStatus(StatusEnum.CONVERSATION.status);
        myCall.setFinishedTime(null);

        EndcallDTO endcallDTO = HelperCallService.getEndCallDTO(ROOM, ReasonEnum.END_PARTICIPANT_CONVERSATION.value);

        // mock
        given(repo.findByRoomId(ROOM)).willReturn(Optional.of(myCall));

        // then
        callService.endCall(endcallDTO);
        verify(repo, atLeast(1)).findByRoomId(ROOM);
        verify(repo, atLeast(1)).save(any());
    }

    @Test
    public void givenEndCallWithLastParticipantRejectedReason() throws Exception {

        Call myCall = new Call();
        myCall.setRoomId(ROOM);
        myCall.setId(CALL_ID);

        EndcallDTO endcallDTO = HelperCallService.getEndCallDTO(ROOM, ReasonEnum.LAST_PARTICIPANT_REJECTED.value);

        // mock
        given(repo.findByRoomId(ROOM)).willReturn(Optional.of(myCall));
        given(repo.findById(CALL_ID)).willReturn(Optional.of(myCall));

        // then
        callService.endCall(endcallDTO);

        verify(repo, atLeast(1)).findByRoomId(ROOM);
        verify(repo, atLeast(1)).save(any());
    }

    @Test
    public void givenEndCallWithEndParticipantConversationReason() throws Exception {
        // given
        Call myCall = new Call();
        myCall.setRoomId(ROOM);
        myCall.setId(CALL_ID);

        EndcallDTO endcallDTO = HelperCallService.getEndCallDTO(ROOM, ReasonEnum.END_PARTICIPANT_CONVERSATION.value);

        // mock
        given(repo.findByRoomId(ROOM)).willReturn(Optional.of(myCall));
        given(repo.findById(CALL_ID)).willReturn(Optional.of(myCall));

        // then
        callService.endCall(endcallDTO);

        // verify
        verify(repo, atLeast(1)).findByRoomId(ROOM);
        verify(repo, atLeast(1)).save(any());
    }

    @Test
    public void givenEndCallWithManagedCallReason() {
        // given
        Call myCall = new Call();
        myCall.setInitDeviceId("device1");
        myCall.setRoomId(ROOM);
        myCall.setId(CALL_ID);

        EndcallDTO endcallDTO = HelperCallService.getEndCallDTO(ROOM, ReasonEnum.MANAGED_CALL.value);

        // mock
        given(repo.findByRoomId(ROOM)).willReturn(Optional.of(myCall));
        given(repo.save(any())).willReturn(myCall);

        // then
        callService.endCall(endcallDTO);

        // verify
        verify(repo, atLeast(1)).findByRoomId(ROOM);
        verify(repo, atLeast(1)).save(any());
    }


    @Test
    public void givenEndCallAutoonWithMissedCallReason() throws Exception {
        // given
        Call myCall = new Call();
        myCall.setRoomId(ROOM);
        myCall.setId(CALL_ID);
        myCall.setAutoon(true);

        EndcallDTO endcallDTO = HelperCallService.getEndCallDTO(ROOM, ReasonEnum.MISSED_CALL.value);

        // mock
        given(repo.findByRoomId(anyString())).willReturn(Optional.of(myCall));

        // then
        callService.endCall(endcallDTO);

        // verify
        verify(repo, atLeast(1)).findByRoomId(ROOM);
        verify(repo, atLeast(1)).save(any());
        verify(participantService, atLeast(1)).updateParticipantMissedTime(any());
    }

    @Test
    public void givenEndCallWithMissedCallReason() throws Exception {
        // given
        Call myCall = new Call();
        myCall.setRoomId(ROOM);
        myCall.setId(CALL_ID);

        EndcallDTO endcallDTO = HelperCallService.getEndCallDTO(ROOM, ReasonEnum.MISSED_CALL.value);

        // mock
        given(repo.findByRoomId(anyString())).willReturn(Optional.of(myCall));

        // then
        callService.endCall(endcallDTO);

        // verify
        verify(repo, atLeast(1)).findByRoomId(ROOM);
        verify(repo, atLeast(1)).save(any());
        verify(participantService, atLeast(1)).updateParticipantMissedNotAttended(any());
    }


    @Test
    public void givenEndCall_throwsException() {
        EndcallDTO endcallDTO = HelperCallService.getEndCallDTO(ROOM, ReasonEnum.UNKOWN.value);

        // mock
        given(repo.findByRoomId(ROOM)).willReturn(Optional.ofNullable(null));

        // when
        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            callService.endCall(endcallDTO);
        });

        assertNotNull(exception.getMessage());
        assertNotEquals("", exception.getMessage());
    }

    @Test
    public void givenConversationToFinished_happyPath_ThenOK() {

        HangupDTO hangupDTO = HelperCallService.getHangupDTO("room", "device");

        // mock
        given(participantService.updateParticipantHangupTime(any(), anyString())).willReturn(true);
        given(repo.findByRoomId(anyString())).willReturn(Optional.of(call));

        // when
        callService.hangUp(hangupDTO);

        verify(participantService, atLeast(1)).updateParticipantHangupTime(call, hangupDTO.getAppToken());
    }

    @Test
    public void givenConversationReject_happyPath_ThenOK() throws Exception {
        RejectDTO rejectDTO = HelperCallService.getRejectDTO("room", "device");

        // mock
        given(participantService.updateParticipantRejectTime(call, rejectDTO.getAppToken())).willReturn(true);
        given(repo.findByRoomId(anyString())).willReturn(Optional.of(call));


        // when
        callService.reject(rejectDTO);

        verify(participantService, atLeast(1)).updateParticipantRejectTime(call, rejectDTO.getAppToken());
    }

    @Test
    public void givenConversationReject_happyPath_ThenKO() {
        RejectDTO rejectDTO = HelperCallService.getRejectDTO("room", "device");

        // mock
        given(participantService.updateParticipantHangupTime(any(), anyString())).willReturn(false);

        // when
        Exception exception = assertThrows(Exception.class, () -> {
            callService.reject(rejectDTO);
        });

        assertNotNull(exception.getMessage());
        assertNotEquals("", exception.getMessage());
    }
}
