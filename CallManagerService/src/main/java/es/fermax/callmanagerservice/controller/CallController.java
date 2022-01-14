package es.fermax.callmanagerservice.controller;

import es.fermax.callmanagerservice.controller.dto.*;
import es.fermax.callmanagerservice.exception.ValidationException;
import es.fermax.callmanagerservice.service.CallService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping(value = {"api/v1/"})
@Slf4j
public class CallController extends AController {

    @Autowired
    CallService callService;

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @ApiOperation(value = "start call - Initialize call")
    @PostMapping(value = "/call/startcall")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = OK, response = String.class),
            @ApiResponse(code = 500, message = INTERNAL_SERVER_ERROR)
    })
    public ResponseEntity<Object> startCall(@Valid @RequestBody StartCallDTO startCallDTO) {
        try {
            log.info("Device Id {}", startCallDTO.getDeviceId());
            String room = callService.startCall(startCallDTO);
            log.info("return roomId {}", room);
            return new ResponseEntity<>(room, HttpStatus.OK);
        } catch (ValidationException e) {
            log.error(VALIDATION_ERROR, e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            log.error(INTERNAL_SERVER_ERROR, e);
            return new ResponseEntity<>(INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @ApiOperation(value = "Register start production video")
    @PostMapping(value = "/call/registerproduce")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = OK, response = String.class),
            @ApiResponse(code = 500, message = INTERNAL_SERVER_ERROR)
    })
    public ResponseEntity<Object> registerVideoProduce(@Valid @RequestBody RegisterProductionVideoDTO registerProductionVideoDTO) {
        try {
            log.info("Device Id {}", registerProductionVideoDTO.getDeviceId());
            log.info("Room Id {}", registerProductionVideoDTO.getRoomId());
            callService.registerVideoProduce(registerProductionVideoDTO);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            log.error(INTERNAL_SERVER_ERROR, e);
            return new ResponseEntity<>(INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @ApiOperation(value = "ring to preview - Update state call from ring to preview.")
    @PostMapping(value = "/call/ringtopreview")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = OK, response = String.class),
            @ApiResponse(code = 500, message = INTERNAL_SERVER_ERROR)
    })
    public ResponseEntity<Object> ringToPreview(@Valid @RequestBody RingToPreviewDTO ringToPreviewDTO) {
        try {
            log.info("AppToken Id {}", ringToPreviewDTO.getAppToken());
            log.info("Room Id {}", ringToPreviewDTO.getRoomId());
            callService.ringToPreview(ringToPreviewDTO);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            log.error(INTERNAL_SERVER_ERROR, e);
            return new ResponseEntity<>(INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @ApiOperation(value = "pickup - Update state call to Conversation")
    @PostMapping(value = "/call/pickup")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = OK, response = String.class),
            @ApiResponse(code = 500, message = INTERNAL_SERVER_ERROR)
    })
    public ResponseEntity<Object> pickup(@Valid @RequestBody PickupDTO pickupDTO) {
        try {
            log.info("PICKUP for RoomId={} AppToken={}", pickupDTO.getRoomId(), pickupDTO.getAppToken());
            callService.pickUp(pickupDTO);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            log.error(INTERNAL_SERVER_ERROR, e);
            return new ResponseEntity<>(INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @ApiOperation(value = "hangup - update Call participant hangup time")
    @PostMapping(value = "/call/hangup")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = OK, response = String.class),
            @ApiResponse(code = 500, message = INTERNAL_SERVER_ERROR)
    })
    public ResponseEntity<Object> hangup(@Valid @RequestBody HangupDTO hangupDTO) {
        try {
            log.info("HANGUP for RoomId={} AppToken={}", hangupDTO.getRoomId(), hangupDTO.getAppToken());
            callService.hangUp(hangupDTO);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            log.error(INTERNAL_SERVER_ERROR, e);
            return new ResponseEntity<>(INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @ApiOperation(value = "endcall - update Call state from conversation to finished")
    @PostMapping(value = "/call/endcall")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = OK, response = String.class),
            @ApiResponse(code = 500, message = INTERNAL_SERVER_ERROR)
    })
    public ResponseEntity<Object> endcall(@Valid @RequestBody EndcallDTO endcallDTO) {
        try {
            log.info("ENDCALL for RoomId={} AppToken={}", endcallDTO.getRoomId());
            callService.endCall(endcallDTO);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            log.error(INTERNAL_SERVER_ERROR, e);
            return new ResponseEntity<>(INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @ApiOperation(value = "reject - update Call participant reject time")
    @PostMapping(value = "/call/reject")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = OK, response = String.class),
            @ApiResponse(code = 500, message = INTERNAL_SERVER_ERROR)
    })
    public ResponseEntity<Object> reject(@Valid @RequestBody RejectDTO rejectDTO) {
        try {
            log.info("REJECT for RoomId={} AppToken={}", rejectDTO.getRoomId(), rejectDTO.getAppToken());
            callService.reject(rejectDTO);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            log.error(INTERNAL_SERVER_ERROR, e);
            return new ResponseEntity<>(INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
