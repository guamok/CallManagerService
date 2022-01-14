package es.fermax.callmanagerservice.controller;

import es.fermax.callmanagerservice.controller.dto.SubscriberDTO;
import es.fermax.callmanagerservice.service.SubscriberService;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(value = {"api/v1/"})
public class SubscribersController extends AController {

    private static final Logger log = LoggerFactory.getLogger(SubscribersController.class);

    @Autowired
    SubscriberService subscriberService;

    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_MANAGEMENT','ROLE_MANAGER')")
    @ApiOperation(value = "List all the subscribers (by filter)")
    @GetMapping(value = "/subscriber")
    @ApiResponses(value = {@ApiResponse(code = 200, message = OK, response = SubscriberDTO.class, responseContainer = "List"),
            @ApiResponse(code = 500, message = INTERNAL_SERVER_ERROR)})
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", dataType = "integer", paramType = "query", value = "Results page you want to retrieve (0..N)"),
            @ApiImplicitParam(name = "size", dataType = "integer", paramType = "query", value = "Number of records per page."),
            @ApiImplicitParam(name = "sort", allowMultiple = true, dataType = "string", paramType = "query", value = "Sorting criteria in the format: property(,asc|desc). "
                    + "Default sort order is ascending. " + "Multiple sort criteria are supported.")})
    public ResponseEntity<Object> getSubscribers(@RequestParam(required = false) @ApiParam(value = "userId", example = "0") Integer userId,
                                                 @RequestParam(required = false) @ApiParam(value = "deviceId") String deviceId, Pageable pageable,
                                                 @ApiParam(value = "Query for all elements") @Valid @RequestParam(value = "question", required = false) String question) {
        try {
            return new ResponseEntity<>(subscriberService.getPaginatedAndFiltered(userId, deviceId, pageable, question), HttpStatus.OK);
        } catch (Exception e) {
            log.error(INTERNAL_SERVER_ERROR, e);
            return new ResponseEntity<>(INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);

        }
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_MANAGEMENT','ROLE_MANAGER')")
    @ApiOperation(value = "Add Subscriber to attend Device Call Divert")
    @PostMapping(value = "/subscriber")
    @ApiResponses(value = {@ApiResponse(code = 200, message = OK, response = String.class),
            @ApiResponse(code = 500, message = INTERNAL_SERVER_ERROR)})
    public ResponseEntity<Object> addSubscriber(@Valid @RequestBody List<SubscriberDTO> subscriberDTO) {

        try {
            subscriberService.saveList(subscriberDTO);
            return new ResponseEntity<>("Subscribers Added", HttpStatus.OK);
        } catch (Exception e) {
            log.error(INTERNAL_SERVER_ERROR, e);
            return new ResponseEntity<>(INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);

        }
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_MANAGEMENT','ROLE_MANAGER')")
    @ApiOperation(value = "Remove Subscriber to attend Device Call Divert")
    @DeleteMapping(value = "/subscriber")
    @ApiResponses(value = {@ApiResponse(code = 200, message = OK, response = String.class),
            @ApiResponse(code = 404, message = SUBSCRIBER_ERROR, response = String.class),
            @ApiResponse(code = 500, message = INTERNAL_SERVER_ERROR)})
    public ResponseEntity<Object> deleteSubscriber(@Valid @RequestBody SubscriberDTO subscriberDTO) {

        try {
            if (subscriberService.delete(subscriberDTO)) {
                log.info("Subscriber deleted {}-{}", subscriberDTO.getDeviceId(), subscriberDTO.getUserId());
                return new ResponseEntity<>("Subscriber Deleted", HttpStatus.OK);
            }
            log.warn("Subscriber had already been deleted or does not exist {}-{}", subscriberDTO.getDeviceId(), subscriberDTO.getUserId());
            return new ResponseEntity<>("Subscriber had already been deleted or does not exist", HttpStatus.OK);
        } catch (Exception e) {
            log.error(INTERNAL_SERVER_ERROR, e);
            return new ResponseEntity<>(INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);

        }
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_MANAGEMENT','ROLE_MANAGER')")
    @ApiOperation(value = "Find By Device Id")
    @GetMapping(value = "/subscriber/{id}")
    @ApiResponses(value = {@ApiResponse(code = 200, message = OK, response = String.class),
            @ApiResponse(code = 404, message = SUBSCRIBER_ERROR, response = String.class),
            @ApiResponse(code = 500, message = INTERNAL_SERVER_ERROR)})
    public ResponseEntity<Object> getSubById(@ApiParam(value="User id",required=true) @PathVariable String id) {
        return new ResponseEntity<>(subscriberService.getSubscriberDTOsByDeviceId(id, id), HttpStatus.OK);
    }
}
