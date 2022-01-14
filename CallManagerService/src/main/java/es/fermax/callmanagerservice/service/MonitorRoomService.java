package es.fermax.callmanagerservice.service;

import es.fermax.callmanagerservice.controller.dto.MonitorDTO;
import es.fermax.callmanagerservice.controller.dto.MonitorParticipantDTO;
import es.fermax.callmanagerservice.controller.dto.MonitorRoomDTO;
import es.fermax.callmanagerservice.enums.StatusEnum;
import es.fermax.callmanagerservice.exception.ValidationException;
import es.fermax.callmanagerservice.model.Call;
import es.fermax.callmanagerservice.model.Participant;
import es.fermax.callmanagerservice.repo.CallRepository;
import es.fermax.callmanagerservice.repo.ParticipantRepository;
import es.fermax.callmanagerservice.util.ParticipantStateValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;


@Service
@Slf4j
public class MonitorRoomService {

    @Autowired
    CallRepository callRepository;

    @Autowired
    ParticipantRepository participantRepository;

    /**
     * List the Calls for monitors
     *
     * @param monitorDTO - the room and  token
     * @param pageNo     - pagination number
     * @param pageSize   - pagination Size
     * @param sortBy     - Sort By
     * @param order      - order (asc/desc)
     * @return MonitorRoomDTO
     * @throws Exception - Exception
     */
    public Page<MonitorRoomDTO> monitorRoomsAndParticipant(MonitorDTO monitorDTO, Integer pageNo, Integer pageSize, String sortBy, String order) throws Exception {

        List<MonitorRoomDTO> monitorRooms = new ArrayList<MonitorRoomDTO>();
        Pageable pageable = PageRequest.of(pageNo, pageSize, order.equals("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending());

        Instant startTime = Instant.now();
        log.info("LOG START QUERY CALLS: " + startTime);
        Page<Call> calls = monitorDTO.getRoomId() != null ? callRepository.findByRoomIdRegex(monitorDTO.getRoomId(), pageable) :
                callRepository.findAll(pageable);

        if (!calls.isEmpty()) {
            log.info("Total de paginas: " + calls.getTotalPages() + "Total de elementos: " + calls.getTotalElements());
            for (Call call : calls) {
                MonitorRoomDTO room = new MonitorRoomDTO();

                room.setId(call.getRoomId());
                //room.setStatus(CallStateValidator.statusCallString(call));
                room.setStatus(StatusEnum.fromStatus((call.getStatus())).toString());
                room.setMasterId(call.getInitDeviceId());
                room.setStart_date(call.getCreationDate());
                room.setCall_type(call.getCallType());

                List<Participant> participantList = participantRepository.findByCallId(call.getId());

                for (Participant participant : participantList) {
                    MonitorParticipantDTO monitorParticipantDTO = new MonitorParticipantDTO();
                    monitorParticipantDTO.setId(participant.getId());
                    monitorParticipantDTO.setAppToken(participant.getAppToken());
                    monitorParticipantDTO.setStatus(ParticipantStateValidator.participantStatus(participant));
                    monitorParticipantDTO.setWeb(ParticipantStateValidator.isWebParticipant(participant.getAppToken()));

                    room.getParticipants().add(monitorParticipantDTO);
                }
                monitorRooms.add(room);
            }
        } else {
            throw new ValidationException("Not rooms found");
        }
        Instant endTime = Instant.now();
        log.info("LOG END QUERY CALLS: " + endTime);
        log.info("LOG DIFFERENCE QUERY CALLS: " + Duration.between(startTime, endTime).toString());

        //Agregar la lista al Page
        Page<MonitorRoomDTO> monitorRoomDTOPage = new PageImpl<MonitorRoomDTO>(monitorRooms, pageable, calls.getTotalElements());

        return monitorRoomDTOPage;
    }
}
