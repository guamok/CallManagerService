package es.fermax.callmanagerservice.controller;

import es.fermax.callmanagerservice.controller.dto.MessageAckDTO;
import es.fermax.callmanagerservice.service.FCMService;
import es.fermax.fermaxsecurity.UserIDEncryptorService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping(value = {"api/v1/"})
public class MessageController extends AController {


    private static final Logger log = LoggerFactory.getLogger(MessageController.class);

    @Autowired
    UserIDEncryptorService userIDEncryptorService;

    @Autowired
    FCMService fcmService;

    @ApiOperation(value = "Firebase Messaging ACK")
    @PostMapping(value = "/message/ack")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = OK, response = String.class),
            @ApiResponse(code = 500, message = INTERNAL_SERVER_ERROR)
    })
    public ResponseEntity<Object> messageAttend(@Valid @RequestBody MessageAckDTO messageDTO) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Integer userId = Integer.valueOf(userIDEncryptorService.decrypt(authentication.getName()));
            log.info("Acknowldege FCM Message with ID {}", messageDTO.getFcmMessageId());
            fcmService.receiveAckMessage(messageDTO.getFcmMessageId(), userId, messageDTO.getAttended());
            return new ResponseEntity<>("Message Acknowledged", HttpStatus.OK);
        } catch (Exception e) {
            log.error(INTERNAL_SERVER_ERROR, e);
            return new ResponseEntity<>(ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
