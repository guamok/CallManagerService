package es.fermax.callmanagerservice.controller;

import es.fermax.callmanagerservice.controller.dto.*;
import es.fermax.callmanagerservice.enums.ReasonEnum;
import es.fermax.callmanagerservice.exception.NotFoundException;
import es.fermax.callmanagerservice.exception.ValidationException;
import es.fermax.callmanagerservice.helper.HelperCallService;
import es.fermax.callmanagerservice.model.Call;
import es.fermax.callmanagerservice.service.CallService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
public class CallControllerTest {

    private static final String ROOM = "69";
    private static final String APPTOKEN = "TOKEN";

    @Mock
    private CallService callService;

    @InjectMocks
    CallController subject;

    @Test
    public void givenInitializeCall_happyPath_ReturnOK() throws Exception {
        StartCallDTO startCallDTO = HelperCallService.getStartCallDto(ROOM);

        // given
        given(callService.startCall(startCallDTO)).willReturn(ROOM);

        //when
        ResponseEntity<Object> response = subject.startCall(startCallDTO);
        Object body = response.getBody();

        //then
        verify(callService, atLeast(1)).startCall(startCallDTO);
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(ROOM, body);
    }

    @Test
    public void givenInitializeAutoonCall_happyPath_ReturnOK() throws Exception {
        StartCallDTO startCallDTO = HelperCallService.getAutoonStartCallDto(ROOM);

        // given
        given(callService.startCall(startCallDTO)).willReturn(ROOM);

        //when
        ResponseEntity<Object> response = subject.startCall(startCallDTO);
        Object body = response.getBody();

        //then
        verify(callService, atLeast(1)).startCall(startCallDTO);
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(ROOM, body);
    }

    @Test
    public void givenInitializeCall_ReturnKO() throws Exception {
        StartCallDTO startCallDTO = HelperCallService.getStartCallDto(ROOM);

        // given
        given(callService.startCall(startCallDTO)).willThrow(new Exception());

        //when
        ResponseEntity<Object> response = subject.startCall(startCallDTO);

        //then
        verify(callService, atLeast(1)).startCall(startCallDTO);
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    public void givenInitializeAutoonCall_validationFailed_ReturnKO() throws Exception {
        String problem = "invalid";
        StartCallDTO startCallDTO = HelperCallService.getAutoonStartCallDto(ROOM);

        // given
        given(callService.startCall(startCallDTO)).willThrow(new ValidationException(problem));

        //when
        ResponseEntity<Object> response = subject.startCall(startCallDTO);
        Object body = response.getBody();

        //then
        verify(callService, atLeast(1)).startCall(startCallDTO);
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(body, problem);
    }

    @Test
    public void givenRegisterVideoProduce_NotFound_ReturnOK() throws Exception {
        RegisterProductionVideoDTO registerProductionVideoDTO = HelperCallService.getRegisterProductionVideoDTO("room", "device");

        //when
        ResponseEntity<Object> response = subject.registerVideoProduce(registerProductionVideoDTO);

        //then
        verify(callService, atLeast(1)).registerVideoProduce(registerProductionVideoDTO);
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void givenRegisterVideoProduce_ReturnKO() throws Exception {
        RegisterProductionVideoDTO registerProductionVideoDTO = HelperCallService.getRegisterProductionVideoDTO("room", "device");

        // given
        doThrow(new NotFoundException("")).when(callService).registerVideoProduce(registerProductionVideoDTO);

        //when
        ResponseEntity<Object> response = subject.registerVideoProduce(registerProductionVideoDTO);

        //then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    public void givenUpdateRingToPreview_NotFound_ReturnOK() throws Exception {
        RingToPreviewDTO ringToPreviewDTO = HelperCallService.getRingToPreviewDTO("room", "device");

        //when
        ResponseEntity<Object> response = subject.ringToPreview(ringToPreviewDTO);

        //then
        verify(callService, atLeast(1)).ringToPreview(ringToPreviewDTO);
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void givenUpdateRingToPreview_ReturnKO() throws Exception {
        RingToPreviewDTO ringToPreviewDTO = HelperCallService.getRingToPreviewDTO("room", "device");

        // given
        doThrow(new NotFoundException(Call.class.getCanonicalName())).when(callService).ringToPreview(ringToPreviewDTO);

        //when
        ResponseEntity<Object> response = subject.ringToPreview(ringToPreviewDTO);

        //then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    public void givenFinishedCall_ReturnOK() throws Exception {
        HangupDTO hangupDTO = HelperCallService.getHangupDTO(ROOM, APPTOKEN);

        //when
        ResponseEntity<Object> response = subject.hangup(hangupDTO);

        //then
        verify(callService, atLeast(1)).hangUp(hangupDTO);
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void givenFinishedCall_ReturnKO() throws Exception {
        HangupDTO hangupDTO = HelperCallService.getHangupDTO(ROOM, APPTOKEN);

        // given
        doThrow(new NotFoundException("")).when(callService).hangUp(hangupDTO);

        //when
        ResponseEntity<Object> response = subject.hangup(hangupDTO);

        //then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    public void givenPickup_ReturnOK() throws Exception {
        PickupDTO dto = new PickupDTO(ROOM, APPTOKEN);

        //when
        ResponseEntity<Object> response = subject.pickup(dto);

        //then
        verify(callService, atLeast(1)).pickUp(dto);
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void givenPickup_ReturnKO() throws Exception {
        PickupDTO dto = new PickupDTO(ROOM, APPTOKEN);

        // given
        doThrow(new NotFoundException("")).when(callService).pickUp(dto);

        //when
        ResponseEntity<Object> response = subject.pickup(dto);

        //then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    public void givenEndCall_ReturnOK() {
        EndcallDTO endcallDTO = HelperCallService.getEndCallDTO(ROOM, ReasonEnum.MANAGED_CALL.value);

        //when
        ResponseEntity<Object> response = subject.endcall(endcallDTO);

        //then
        verify(callService, atLeast(1)).endCall(endcallDTO);
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void givenEndCall_ReturnKO() {
        EndcallDTO endcallDTO = HelperCallService.getEndCallDTO(ROOM, ReasonEnum.MANAGED_CALL.value);

        // given
        doThrow(new NotFoundException("")).when(callService).endCall(endcallDTO);

        //when
        ResponseEntity<Object> response = subject.endcall(endcallDTO);

        //then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    public void givenReject_ReturnOK() throws Exception {
        RejectDTO rejectDTO = HelperCallService.getRejectDTO(ROOM, APPTOKEN);

        //when
        ResponseEntity<Object> response = subject.reject(rejectDTO);

        //then
        verify(callService, atLeast(1)).reject(rejectDTO);
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void givenReject_ReturnKO() throws Exception {
        RejectDTO rejectDTO = HelperCallService.getRejectDTO(ROOM, APPTOKEN);

        // given
        doThrow(new NotFoundException("")).when(callService).reject(rejectDTO);

        //when
        ResponseEntity<Object> response = subject.reject(rejectDTO);

        //then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

}
