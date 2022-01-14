package es.fermax.callmanagerservice.service;

import es.fermax.callmanagerservice.controller.dto.CallRegistryDTO;
import es.fermax.callmanagerservice.controller.dto.ParticipantCallDTO;
import es.fermax.callmanagerservice.exception.NotFoundException;
import es.fermax.callmanagerservice.model.Call;
import es.fermax.callmanagerservice.model.CallRegistryTypeEnum;
import es.fermax.callmanagerservice.model.Participant;
import es.fermax.callmanagerservice.repo.CallRepository;
import es.fermax.callmanagerservice.repo.ParticipantRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ParticipantService {

    @Autowired
    ParticipantRepository participantRepository;

    @Autowired
    CallRepository callRepository;

    @Autowired
    FCMService fcmService;

    private static final String REGISTERED_RINGING_OK = "Registered PARTICIPANT RINGING - OK. Room: {}. participant {}";

    /**
     * Creates new participant
     *
     * @param token - mobile app token
     * @param call  - existent Call object
     */
    public void createAndSaveParticipant(String token, Call call) {
        List<Participant> participantList = participantRepository.findByCallId(call.getId());
        if (participantList.stream().filter(participant -> token.equals(participant.getAppToken())).findAny()
                .orElse(null) != null) {
            log.warn("The mobile participant is already in the call. RoomId: {}, AppToken {}", call.getRoomId(), token);
        } else {
            Participant participant = new Participant();
            participant.setAppToken(token);
            participant.setCallId(call.getId());
            participant.setSentInvitationTime(LocalDateTime.now());
            try {
                participantRepository.insert(participant);
            } catch (ConstraintViolationException e) {
                log.error("The mobile participant is already in the call. RoomId: {}, AppToken {}. Exception: {}", call.getRoomId(), token,
                        e.getMessage());
            }
        }
    }

    /**
     * Update participant ringing time
     *
     * @param call  - call
     * @param token - mobile app token
     * @return isUpdated
     */
    public boolean updateParticipantRingingTime(Call call, String token) throws InterruptedException {

        log.info("Find participant. Token: {}, CallId: {}", token, call.getId());
        Optional<Participant> participantOptional = participantRepository.findByAppTokenAndCallId(token, call.getId());

        if (participantOptional.isPresent()) {
            Participant participant = participantOptional.get();
            participant.setRingingTime(LocalDateTime.now());
            participantRepository.save(participant);
            log.info(REGISTERED_RINGING_OK, call.getRoomId(), token);
            return true;
        } else {
            log.info("Second attempt to save participant");
            Thread.sleep(5000);
            Optional<Participant> partError = participantRepository.findByAppTokenAndCallId(token, call.getId());
            if (partError.isPresent()) {
                Participant participant = partError.get();
                participant.setRingingTime(LocalDateTime.now());
                participantRepository.save(participant);
                log.info(REGISTERED_RINGING_OK, call.getRoomId(), token);
                return true;
            } else {
                throw new NotFoundException(Participant.class.getCanonicalName());
            }
        }
    }

    /**
     * Update participant preview time
     *
     * @param call  - call
     * @param token - mobile app token
     * @return isUpdated
     */
    public boolean updateParticipantPreviewTime(Call call, String token) throws InterruptedException {
        Optional<Participant> participantOptional = participantRepository.findByAppTokenAndCallId(token, call.getId());

        if (participantOptional.isPresent()) {
            Participant participant = participantOptional.get();
            participant.setPreviewTime(LocalDateTime.now());
            participantRepository.save(participant);
            log.info("Registered PARTICIPANT PREVIEW - OK. Room: {}. participant {}", call.getRoomId(), token);
            return true;
        } else {
            log.info("Second attempt to save participant participant {}", token);
            Thread.sleep(5000);
            Optional<Participant> partError = participantRepository.findByAppTokenAndCallId(token, call.getId());
            if (partError.isPresent()) {
                Participant participant = partError.get();
                participant.setPreviewTime(LocalDateTime.now());
                participantRepository.save(participant);
                log.info(REGISTERED_RINGING_OK, call.getRoomId(), token);
                return true;
            } else {
                throw new NotFoundException(Participant.class.getCanonicalName());
            }
        }
    }


    /**
     * Update participant pickup time
     *
     * @param call  - call identifier
     * @param token - mobile app token
     * @return isUpdated
     */
    public boolean updateParticipantPickupTime(Call call, String token) {
        Optional<Participant> participantOptional = participantRepository.findByAppTokenAndCallId(token, call.getId());

        if (participantOptional.isPresent()) {
            Participant participant = participantOptional.get();
            participant.setPickupTime(LocalDateTime.now());
            participantRepository.save(participant);
            log.info("Registered PARTICIPANT PICK UP - OK. Room: {}. participant {}", call.getRoomId(), token);

            return true;
        } else {
            throw new NotFoundException(Participant.class.getCanonicalName());
        }
    }

    /**
     * Update participant missed time when has not pickup time and send Notification
     * Event
     *
     * @param call  - room identifier
     * @param token - mobile app token who attended the call.
     */
    public void updateParticipantMissedTimeAttended(Call call, String token) {
        List<Participant> list = participantRepository.findByCallId(call.getId());

        for (Participant participant : list) {
            if (participant.getPickupTime() == null && participant.getRejectTime() == null) {
                participant.setMissedCallTime(LocalDateTime.now());
                participantRepository.save(participant);
                log.info("Registered PARTICIPANT MISSED TIME ATTENDED - OK. Room: {}. participant {}", call.getRoomId(), token);
                // Send notification to join room
                fcmService.sendMissedCallAttendedNotificationMessage(participant.getAppToken(), call.getInitDeviceId(), call.getCallAs(), token);
            }
        }
    }

    /**
     * Update participant missed time when ANYBODY pickup the call. Call not
     * attended.
     *
     * @param call - call identifier
     */
    public void updateParticipantMissedNotAttended(Call call) {
        List<Participant> list = participantRepository.findByCallId(call.getId());
        for (Participant participant : list) {
            if (participant.getRejectTime() == null) {
                participant.setMissedCallTime(LocalDateTime.now());
                participantRepository.save(participant);
                log.info("Registered PARTICIPANT MISSED NOT ATTENDED - OK. Room: {}.", call.getRoomId());
                // Send notification to join room
                fcmService.sendMissedCallNotAttendedNotificationMessage(participant.getAppToken(), call.getInitDeviceId(), call.getCallAs());
            }
        }
    }


    /**
     * Update participant hangup time
     *
     * @param call  - call identifier
     * @param token - mobile app token
     * @return isUpdated
     */
    public boolean updateParticipantHangupTime(Call call, String token) {
        Optional<Participant> participantOptional = participantRepository.findByAppTokenAndCallId(token, call.getId());

        if (participantOptional.isPresent()) {
            Participant participant = participantOptional.get();
            participant.setHangupTime(LocalDateTime.now());
            participantRepository.save(participant);
            log.info("Registered PARTICIPANT HANG UP TIME - OK. Room: {}. participant {}", call.getRoomId(), token);
            return true;
        } else {
            throw new NotFoundException(Participant.class.getCanonicalName());
        }
    }

    /**
     * Update participant reject time
     *
     * @param call  - call identifier
     * @param token - mobile app token
     * @return isUpdated
     */
    public boolean updateParticipantRejectTime(Call call, String token) throws InterruptedException {
        Optional<Participant> participantOptional = participantRepository.findByAppTokenAndCallId(token, call.getId());

        if (participantOptional.isPresent()) {
            Participant participant = participantOptional.get();
            participant.setRejectTime(LocalDateTime.now());
            participantRepository.save(participant);
            return true;
        } else {
            log.info("Second attempt to save participant");
            Thread.sleep(5000);
            Optional<Participant> partError = participantRepository.findByAppTokenAndCallId(token, call.getId());
            if (partError.isPresent()) {
                Participant participant = partError.get();
                participant.setRejectTime(LocalDateTime.now());
                participantRepository.save(participant);
                log.info(REGISTERED_RINGING_OK, call.getRoomId(), token);
                return true;
            } else {
                throw new NotFoundException(Participant.class.getCanonicalName());
            }
        }
    }

    /**
     * update participant manage request time
     *
     * @param call - call identifier
     * @return isUpdated
     */
    public boolean updateParticipantManageRequestTime(Call call) {
        List<Participant> list = participantRepository.findByCallId(call.getId());
        for (Participant participant : list) {
            participant.setManageRequestTime(LocalDateTime.now());
            participantRepository.save(participant);
            log.info("Registered PARTICIPANT Managed Request Time - OK. Room: {}.", call.getRoomId());
        }
        return true;
    }

    /**
     * update participant missed time
     *
     * @param call - call identifier
     * @return isUpdated
     */
    public boolean updateParticipantMissedTime(Call call) {
        List<Participant> list = participantRepository.findByCallId(call.getId());
        for (Participant participant : list) {
            participant.setMissedCallTime(LocalDateTime.now());
            participantRepository.save(participant);
            log.info("Registered PARTICIPANT Missed Time - OK. Room: {}.", call.getRoomId());
        }
        return true;
    }

    /**
     * List of t calls for a participant Pageable
     *
     * @param participantCallDTO
     * @param pageable
     * @return page list of the cparticipans for a app token
     */
    public Page<CallRegistryDTO> findPartincipantHistoricCall(ParticipantCallDTO participantCallDTO, Pageable
            pageable) {
        return participantRepository.findByAppTokenOrderBySentInvitationTimeDesc(participantCallDTO.getAppToken(), pageable).map(value ->
                CallRegistryDTO.parserParticipantToHistoricaCallDto(value, callRepository.findById(value.getCallId()).orElse(null)));
    }

    /**
     * List of las 50 historic calls in the last month
     *
     * @param participantCallDTO
     * @return list of historic call
     */
    public List<CallRegistryDTO> findPartincipantHistoricCallPaginatedOff(ParticipantCallDTO participantCallDTO) {
        List<Participant> lstParticipants = new ArrayList<>();
        switch (CallRegistryTypeEnum.fromCallType(participantCallDTO.getCallRegistryType())) {
            case ALL:
            case UNKOWN:
                lstParticipants = participantRepository.findFirst50ByAppTokenAndSentInvitationTimeBetweenOrderBySentInvitationTimeDesc(participantCallDTO.getAppToken(), LocalDateTime.now().minusDays(30), LocalDateTime.now());
                break;
            case MISSED_CALL:
                lstParticipants = participantRepository.findFirst50ByAppTokenAndMissedCallTimeNotNullAndSentInvitationTimeBetweenOrderBySentInvitationTimeDesc(participantCallDTO.getAppToken(), LocalDateTime.now().minusDays(30), LocalDateTime.now());
                break;
            case AUTOON:
                lstParticipants = participantRepository.findFirst50ByAppTokenAndSentInvitationTimeBetweenOrderBySentInvitationTimeDesc
                                (participantCallDTO.getAppToken(), LocalDateTime.now().minusDays(30), LocalDateTime.now()).stream()
                        .filter(participant -> callRepository.findById(participant.getCallId()).get().isAutoon()).collect(Collectors.toList());
        }

        return lstParticipants.stream().map(value ->
                CallRegistryDTO.parserParticipantToHistoricaCallDto(value, callRepository.findById(value.getCallId()).orElse(null))).collect(Collectors.toList());
    }
}
