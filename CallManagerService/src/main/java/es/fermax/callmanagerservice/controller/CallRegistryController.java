package es.fermax.callmanagerservice.controller;

import es.fermax.callmanagerservice.controller.dto.CallRegistryDTO;
import es.fermax.callmanagerservice.controller.dto.ParticipantCallDTO;
import es.fermax.callmanagerservice.service.ParticipantService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping(value = {"api/v1/callregistry/"})
@Slf4j
public class CallRegistryController extends AController {

    @Autowired
    ParticipantService participantService;

    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_MANAGEMENT','ROLE_MANAGER', 'ROLE_USER')")
    @ApiOperation(value = "Registry - Get the registry call for a participant")
    @GetMapping(value = "/participant")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = OK, response = CallRegistryDTO.class, responseContainer = "List"),
                    @ApiResponse(code = 500, message = INTERNAL_SERVER_ERROR)
            })
    public ResponseEntity<Object> callRegistryForParticipant(@Valid ParticipantCallDTO participantCallDTO) {
        log.info("Registry Call for AppToken={}", participantCallDTO.getAppToken());

        return new ResponseEntity<>(participantService.findPartincipantHistoricCallPaginatedOff(participantCallDTO), HttpStatus.OK);
    }
}
