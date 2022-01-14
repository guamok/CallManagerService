//package es.fermax.callmanagerservice.rabbit;//package es.fermax.callmanagerservice.rabbit;
//
//import es.fermax.callmanagerservice.model.Call;
//import es.fermax.callmanagerservice.rabbit.messages.StartRingingMessage;
//import es.fermax.callmanagerservice.repo.CallRepository;
//import es.fermax.callmanagerservice.service.CallService;
//import es.fermax.callmanagerservice.service.ParticipantService;
//import org.junit.jupiter.api.Test;
//import org.junit.runner.RunWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.Mockito;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.test.context.junit4.SpringRunner;
//
//import java.util.Optional;
//
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.anyString;
//import static org.mockito.BDDMockito.given;
//import static org.mockito.Mockito.atLeast;
//import static org.mockito.Mockito.verify;
//
//@RunWith(SpringRunner.class)
//@ActiveProfiles("test")
//public class StartRingingListenerTest {
//
//    @Mock
//    CallService callService;
//
//    @Mock
//    CallRepository callRepository;
//
//    @Mock
//    ParticipantService participantService;
//
//    @InjectMocks
//    StartRingingListener startRingingListener;
//
//    @Test
//    public void givenStartRingingMessage_whenReceiveMessage_ThenSaveOK() throws Exception {
//        Call call = new Call();
//        call.setRoomId("room-123");
//        call.setInitDeviceId("device123");
//
//        // mock
//        given(callRepository.findByRoomId(anyString())).willReturn(Optional.of(call));
//        Mockito.doNothing().when(participantService).updateParticipantRingingTime(any(), anyString());
//
//        StartRingingMessage message = new StartRingingMessage();
//        message.setRoomId(call.getRoomId());
//        message.setAppToken("appToken1");
//
//        // when
//        startRingingListener.receiveStartRingingMessage(message);
//
//        // then
//        verify(callService, atLeast(1)).registerStartRinging("room-123", "appToken1");
//    }
//
//}
