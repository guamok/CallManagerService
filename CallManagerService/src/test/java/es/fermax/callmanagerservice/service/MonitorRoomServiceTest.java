package es.fermax.callmanagerservice.service;


import es.fermax.callmanagerservice.controller.dto.MonitorDTO;
import es.fermax.callmanagerservice.controller.dto.MonitorParticipantDTO;
import es.fermax.callmanagerservice.controller.dto.MonitorRoomDTO;
import es.fermax.callmanagerservice.model.Call;
import es.fermax.callmanagerservice.model.Participant;
import es.fermax.callmanagerservice.repo.CallRepository;
import es.fermax.callmanagerservice.repo.ParticipantRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
public class MonitorRoomServiceTest {

    private static final String ROOM = "69";
    private static final String IDROOM = "";
    private static final  Integer pageNo = 0;
    private static final  Integer pageSize = 10;
    private static final  String sortBy = "id";
    private static final  String order = "desc";
    private static final String token = "webDaniAlvesTokenTestFake";
    private static final String callId = "12345";

    @InjectMocks
    MonitorRoomService monitorRoomService;

    @Mock
    CallRepository repo;
    @Mock
    ParticipantRepository repoParticipant;

    private Call call;
    private Pageable pageable;
    private Page<Call> respuesta;
    private MonitorDTO monitorDTO;
    private MonitorParticipantDTO participantDTO;
    private List<MonitorParticipantDTO> MonitorParticipantParticipantList;
    private MonitorRoomDTO monitorRoomDTO;
    private List<MonitorRoomDTO> monitorRoomDTOList;
    private Page<MonitorRoomDTO> respuestaMonitorRoomDTOPage;
    private List<Participant> participantList;
    private Participant participant;


    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        pageable = PageRequest.of(pageNo, pageSize, order.equals("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending());
        call = new Call();
        call.setRoomId(ROOM);
        List<Call> listCall = new ArrayList<>();
        listCall.add(call);

        respuesta = new PageImpl<Call>(listCall, pageable, listCall.size());

        monitorDTO = new MonitorDTO();
        monitorDTO.setRoomId(ROOM);

        participantDTO = new MonitorParticipantDTO();
        participantDTO.setAppToken(token);

        MonitorParticipantParticipantList = new ArrayList<>();
        MonitorParticipantParticipantList.add(participantDTO);

        monitorRoomDTO = new MonitorRoomDTO();
        monitorRoomDTO.setId(ROOM);
        monitorRoomDTO.setParticipants(MonitorParticipantParticipantList);

        monitorRoomDTOList = new ArrayList<>();
        monitorRoomDTOList.add(monitorRoomDTO);

        respuestaMonitorRoomDTOPage = new PageImpl<>(monitorRoomDTOList, pageable, monitorRoomDTOList.size());

        participant = new Participant();
        participant.setCallId("12345");
        participantList = new ArrayList<>();
        participantList.add(participant);
    }

    @Test
    public void givenMonitorRoomsAndParticipant_ThenReturnMonitorRoomsID() throws Exception {
        // mock
        given(repo.findByRoomIdRegex(ROOM, pageable)).willReturn(respuesta);
        given(repoParticipant.findByCallId(callId)).willReturn(participantList);

        // when
        monitorRoomService.monitorRoomsAndParticipant(monitorDTO,pageNo,pageSize,sortBy,order);

        verify(repo, never()).findAll(pageable);
        verify(repo, atLeast(1)).findByRoomIdRegex(ROOM, pageable);
    }

    @Test
    public void givenMonitorRoomsAndParticipant_ThenReturnMonitorRoomsALL() throws Exception {
        // mock
        given(repo.findAll(pageable)).willReturn(respuesta);
        given(repoParticipant.findByCallId(callId)).willReturn(participantList);

        monitorDTO.setRoomId(null);
        // when
        monitorRoomService.monitorRoomsAndParticipant(monitorDTO,pageNo,pageSize,sortBy,order);

        verify(repo, never()).findByRoomIdRegex(ROOM, pageable);
        verify(repo, atLeast(1)).findAll(pageable);
    }

    @Test
    public void givenMonitorRoomsAndParticipantByRoomId_throwsException() throws Exception {
        // mock
        given(repo.findByRoomIdRegex(ROOM, pageable)).willReturn(Page.empty());

        // when
        Exception exception = assertThrows(Exception.class, () -> {
            monitorRoomService.monitorRoomsAndParticipant(monitorDTO,pageNo,pageSize,sortBy,order);
        });

        assertNotNull(exception.getMessage());
        assertNotEquals("", exception.getMessage());
    }

}
