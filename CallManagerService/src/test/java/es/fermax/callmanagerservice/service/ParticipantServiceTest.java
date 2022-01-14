package es.fermax.callmanagerservice.service;

import es.fermax.callmanagerservice.controller.dto.ParticipantCallDTO;
import es.fermax.callmanagerservice.helper.HelperCallService;
import es.fermax.callmanagerservice.model.Call;
import es.fermax.callmanagerservice.model.Participant;
import es.fermax.callmanagerservice.repo.CallRepository;
import es.fermax.callmanagerservice.repo.ParticipantRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import javax.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
public class ParticipantServiceTest {
    @InjectMocks
    ParticipantService participantService = new ParticipantService();

    @Mock
    private ParticipantRepository repo;

    @Mock
    private CallRepository callRepo;

    @Mock
    private FCMService fcmService;

    private Participant participant;
    private Call call;

    @Before
    public void setup() {

        call = new Call();
        call.setInitDeviceId("deviceId");
        call.setRoomId("roomId");
        call.setId("callId");

        participant = new Participant();
        participant.setAppToken("token");
        participant.setCallId(call.getId());
    }

    @Test
    public void givenCall_whencreateAndSaveParticipant_ThenOk() {
        // mock
        given(repo.insert(any(Participant.class))).willReturn(participant);

        // when
        participantService.createAndSaveParticipant(participant.getAppToken(), call);

        // then
        verify(repo, atLeast(1)).insert(any(Participant.class));
    }

    @Test
    public void givenCall_whencreateAndSaveParticipantNotUnique_ThenWarn() {
        //mock
        given(repo.findByCallId(any())).willReturn(Arrays.asList(participant));

        // when
        participantService.createAndSaveParticipant(participant.getAppToken(), call);

        // then
        verify(repo, never()).save(any());
    }

    @Test
    public void givenCall_whencreateAndSaveParticipantNotUnique_ThenException() {
        // mock
        given(repo.insert(any(Participant.class))).willThrow(new ConstraintViolationException("EXC", null));

        // when
        participantService.createAndSaveParticipant(participant.getAppToken(), call);

        // then
        verify(repo, atLeast(1)).insert(any(Participant.class));
    }

    @Test
    public void updateParticipantRingingTime_OK() throws InterruptedException {
        Assert.assertNull(participant.getRingingTime());

        // mock
        given(callRepo.findByRoomId(anyString())).willReturn(Optional.of(call));
        given(repo.findByAppTokenAndCallId(participant.getAppToken(), call.getId())).willReturn(Optional.of(participant));

        // when
        participantService.updateParticipantRingingTime(call, participant.getAppToken());

        // then
        verify(repo, atLeast(1)).save(any());
        Assert.assertNotNull(participant.getRingingTime());
    }

    @Test
    public void updateParticipantPreviewTime_OK() throws InterruptedException {
        Assert.assertNull(participant.getPreviewTime());

        // mock
        given(callRepo.findByRoomId(anyString())).willReturn(Optional.of(call));
        given(repo.findByAppTokenAndCallId(participant.getAppToken(), call.getId())).willReturn(Optional.of(participant));

        // when
        participantService.updateParticipantPreviewTime(call, participant.getAppToken());

        // then
        verify(repo, atLeast(1)).save(any());
        Assert.assertNotNull(participant.getPreviewTime());
    }

    @Test
    public void updateParticipantPickupTime_OK() {
        Assert.assertNull(participant.getPickupTime());

        // mock
        given(callRepo.findByRoomId(anyString())).willReturn(Optional.of(call));
        given(repo.findByAppTokenAndCallId(participant.getAppToken(), call.getId())).willReturn(Optional.of(participant));

        // when
        participantService.updateParticipantPickupTime(call, participant.getAppToken());

        // then
        verify(repo, atLeast(1)).save(any());
        Assert.assertNotNull(participant.getPickupTime());
    }

    @Test
    public void updateParticipantHangupTime_OK() {
        Assert.assertNull(participant.getHangupTime());

        // mock
        given(callRepo.findByRoomId(anyString())).willReturn(Optional.of(call));
        given(repo.findByAppTokenAndCallId(participant.getAppToken(), call.getId())).willReturn(Optional.of(participant));

        // when
        participantService.updateParticipantHangupTime(call, participant.getAppToken());

        // then
        verify(repo, atLeast(1)).save(any());
        Assert.assertNotNull(participant.getHangupTime());
    }

    @Test
    public void updateParticipantRejectTime_OK() throws InterruptedException {
        Assert.assertNull(participant.getRejectTime());

        // mock
        given(callRepo.findByRoomId(anyString())).willReturn(Optional.of(call));
        given(repo.findByAppTokenAndCallId(participant.getAppToken(), call.getId())).willReturn(Optional.of(participant));

        // when
        participantService.updateParticipantRejectTime(call, participant.getAppToken());

        // then
        verify(repo, atLeast(1)).save(any());
        Assert.assertNotNull(participant.getRejectTime());
    }

    @Test
    public void updateParticipantMissedTime_OK() {
        Assert.assertNull(participant.getMissedCallTime());
        participant.setPickupTime(null);
        participant.setRejectTime(null);

        // mock
        given(callRepo.findByRoomId(anyString())).willReturn(Optional.of(call));
        given(repo.findByCallId(call.getId())).willReturn(Arrays.asList(participant));
        Mockito.doNothing().when(fcmService).sendMissedCallAttendedNotificationMessage(anyString(), anyString(), anyString(), anyString());

        // when
        participantService.updateParticipantMissedTimeAttended(call, participant.getAppToken());

        // then
        verify(repo, atLeast(1)).save(any());
        Assert.assertNotNull(participant.getMissedCallTime());
    }


    @Test
    public void givenHistoricCall_OK() {

        ParticipantCallDTO participantCallDTO = HelperCallService.getParticipantCallDTO(participant.getAppToken());
        Pageable page = PageRequest.of(0, 20);
        List<Participant> lstParticipant = new ArrayList<>();
        lstParticipant.add(participant);
        Page<Participant> participantParge = new PageImpl<>(lstParticipant, page, lstParticipant.size());

        //mock
        given(repo.findByAppTokenOrderBySentInvitationTimeDesc(participant.getAppToken(), page)).willReturn(participantParge);
        given(callRepo.findById(participant.getCallId())).willReturn(Optional.of(call));


        //when
        participantService.findPartincipantHistoricCall(participantCallDTO, page);

        //then
        verify(repo, never()).save(any());
        verify(repo, atLeast(1)).findByAppTokenOrderBySentInvitationTimeDesc(participant.getAppToken(), page);
    }
}
