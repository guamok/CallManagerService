package es.fermax.callmanagerservice.controller;

import es.fermax.callmanagerservice.controller.dto.MessageAckDTO;
import es.fermax.callmanagerservice.service.FCMService;
import es.fermax.fermaxsecurity.UserIDEncryptorService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@RunWith(SpringRunner.class)
public class MessageControllerTest {

    @Mock
    FCMService fcmService;

    @Mock
    UserIDEncryptorService userIDEncryptorService;

    @InjectMocks
    MessageController controller = new MessageController();

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    @WithMockUser(username = "K7GZBI52M4FE3YKQCSKL5U3HA24A")
    public void acknowledgeMessageDTOTest() {

        // mock
        try {
            given(userIDEncryptorService.decrypt(any())).willReturn("100");
        } catch (Exception e) {
            Assert.fail();
            e.printStackTrace();
        }

        // when
        ResponseEntity<Object> ackResponse = controller.messageAttend(new MessageAckDTO("fcmid1", true));

        // then
        assertEquals(HttpStatus.OK, ackResponse.getStatusCode());
        assertNotNull(ackResponse.getBody());

    }


}