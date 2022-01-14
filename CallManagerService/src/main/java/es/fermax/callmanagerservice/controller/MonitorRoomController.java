package es.fermax.callmanagerservice.controller;


import es.fermax.callmanagerservice.controller.dto.MonitorDTO;
import es.fermax.callmanagerservice.controller.dto.MonitorRoomDTO;
import es.fermax.callmanagerservice.exception.ValidationException;
import es.fermax.callmanagerservice.service.MonitorRoomService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = {"api/v1/"})
@Slf4j
public class MonitorRoomController extends AController{

    @Autowired
    MonitorRoomService monitorRoomService;

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @ApiOperation(value = "GetMonitorRooms - Get the room and their participants")
    @GetMapping(value = "/monitor/rooms")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = OK, response = MonitorRoomDTO.class, responseContainer = "List"),
                    @ApiResponse(code = 500, message = INTERNAL_SERVER_ERROR)
            })
    public ResponseEntity<Object> monitorRoomsAndParticipant(
            @RequestParam(defaultValue = "0") Integer pageNo,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String order,
            MonitorDTO monitorDTO) {
        try {
            log.info("Registry Call for RoomID: " + monitorDTO.getRoomId());
            return new ResponseEntity<>(monitorRoomService.monitorRoomsAndParticipant(monitorDTO, pageNo, pageSize, sortBy, order), HttpStatus.OK);
        } catch (ValidationException e) {
            log.error(VALIDATION_ERROR, e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            log.error(INTERNAL_SERVER_ERROR, e);
            return new ResponseEntity<>(INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
