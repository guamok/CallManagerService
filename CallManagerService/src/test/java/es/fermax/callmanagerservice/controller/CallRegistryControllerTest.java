package es.fermax.callmanagerservice.controller;

import es.fermax.callmanagerservice.controller.dto.ParticipantCallDTO;
import es.fermax.callmanagerservice.helper.HelperCallService;
import es.fermax.callmanagerservice.service.ParticipantService;
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
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.verify;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
public class CallRegistryControllerTest {

    private static final String APPTOKEN = "TOKEN";

    @Mock
    private ParticipantService participantService;

    @InjectMocks
    CallRegistryController subject;

    @Test
    public void givenHistoricCall_ReturnOK() {
        ParticipantCallDTO participantCallDTO = HelperCallService.getParticipantCallDTO(APPTOKEN);

        //when
        ResponseEntity<Object> response = subject.callRegistryForParticipant(participantCallDTO);

        //then
        verify(participantService, atLeast(1)).findPartincipantHistoricCallPaginatedOff(participantCallDTO);
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}
