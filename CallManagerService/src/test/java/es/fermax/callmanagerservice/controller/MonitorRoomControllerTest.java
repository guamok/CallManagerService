package es.fermax.callmanagerservice.controller;


import es.fermax.callmanagerservice.controller.dto.MonitorDTO;
import es.fermax.callmanagerservice.helper.HelperMonitorRoomService;
import es.fermax.callmanagerservice.service.MonitorRoomService;
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
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
public class MonitorRoomControllerTest {

    private static final String ROOM = "69";
    private static final  Integer pageNo = 0;
    private static final  Integer pageSize = 10;
    private static final  String sortBy = "id";
    private static final  String order = "desc";


    @InjectMocks
    MonitorRoomController subject;

    @Mock
    private MonitorRoomService monitorRoomService;

    @Test
    public void givenMonitorRoomsAndParticipant_ReturnOK() throws Exception {
        MonitorDTO monitorDTO = HelperMonitorRoomService.getStartMonitorRoom(ROOM);

        //when
        ResponseEntity<Object> response = subject.monitorRoomsAndParticipant(pageNo, pageSize, sortBy, order, monitorDTO);

        //then
        verify(monitorRoomService, atLeast(1)).monitorRoomsAndParticipant(monitorDTO, pageNo, pageSize, sortBy, order);
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }


}
