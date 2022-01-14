package es.fermax.callmanagerservice.controller;

import es.fermax.callmanagerservice.controller.dto.SubscriberDTO;
import es.fermax.callmanagerservice.service.SubscriberService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
public class SubscribersControllerTests {

    @Mock
    private SubscriberService subscribersService;

    @InjectMocks
    SubscribersController subscriberController = new SubscribersController();

    private SubscriberDTO subscriberDTO;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        subscriberDTO = new SubscriberDTO(1, "123456789");
    }

    @Test
    @WithMockUser(username = "K7GZBI52M4FE3YKQCSKL5U3HA24A", roles = {"ADMIN"})
    public void givenException_whenAddSubscriberThenReturnKO() throws Exception {
        //mock
        doThrow(NullPointerException.class).when(subscribersService).saveList(any());
        //when

        ResponseEntity<Object> tokenResponse = subscriberController.addSubscriber(Arrays.asList(subscriberDTO));

        //then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, tokenResponse.getStatusCode());
    }

    @Test
    @WithMockUser(username = "K7GZBI52M4FE3YKQCSKL5U3HA24A", roles = {"ADMIN"})
    public void givenSubscriber_whenAddSubscriberThenReturnOK() throws Exception {
        //mock
        doNothing().when(subscribersService).saveList(any());
        //when

        ResponseEntity<Object> tokenResponse = subscriberController.addSubscriber(Arrays.asList(subscriberDTO));

        //then
        assertEquals(HttpStatus.OK, tokenResponse.getStatusCode());
    }

    @Test
    @WithMockUser(username = "K7GZBI52M4FE3YKQCSKL5U3HA24A", roles = {"ADMIN"})
    public void givenSubscriber_whenDeleteSubscriber_ThenReturnOK() throws Exception {
        //mock
        given(subscribersService.delete(any())).willReturn(true);
        //when
        ResponseEntity<Object> response = subscriberController.deleteSubscriber(subscriberDTO);

        //then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    @WithMockUser(username = "K7GZBI52M4FE3YKQCSKL5U3HA24A", roles = {"ADMIN"})
    public void givenNullSubscriber_whenDeleteSubscriber_ThenReturnNotFound() throws Exception {
        //mock
        given(subscribersService.delete(any())).willReturn(false);
        //when
        ResponseEntity<Object> response = subscriberController.deleteSubscriber(subscriberDTO);

        //then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    public void givenException_whenDeleteSubscriber_ThenReturnKO() throws Exception {
        //mock
        when(subscribersService.delete(any())).thenThrow(NullPointerException.class);
        //when
        ResponseEntity<Object> response = subscriberController.deleteSubscriber(subscriberDTO);

        //then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }
}

