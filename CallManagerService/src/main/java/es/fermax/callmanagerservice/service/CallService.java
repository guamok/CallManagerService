package es.fermax.callmanagerservice.service;

import es.fermax.callmanagerservice.controller.dto.*;
import es.fermax.callmanagerservice.enums.CallTypeEnum;
import es.fermax.callmanagerservice.enums.ReasonEnum;
import es.fermax.callmanagerservice.enums.StatusEnum;
import es.fermax.callmanagerservice.exception.NotFoundException;
import es.fermax.callmanagerservice.exception.ValidationException;
import es.fermax.callmanagerservice.model.Call;
import es.fermax.callmanagerservice.repo.CallRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class CallService {

    @Autowired
    CallRepository callRepository;

    @Autowired
    ParticipantService participantService;

    @Autowired
    SubscriberService subscriberService;

    @Autowired
    FCMService fcmService;

    @Autowired
    AppTokenService appTokenService;

    /**
     * Init service for initialize call.
     *
     * @param startCallDTO where is Id device from Divert Signaling server.
     * @return room, (deviceId + now)
     */
    public String startCall(StartCallDTO startCallDTO) throws Exception {
        log.info("Initialize call...");

        if (!CallTypeEnum.isValid(startCallDTO.getCallType())) {
            throw new ValidationException("Not supported type of call.");
        }

        String roomId = startCallDTO.getDeviceId() + "_" + LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli();

        CallTypeEnum callType = CallTypeEnum.fromCallType(startCallDTO.getCallType());
        boolean isAutoon = callType.equals(CallTypeEnum.AUTOON);

        Call call = new Call();
        call.setInitDeviceId(startCallDTO.getDeviceId());
        call.setCallAs(startCallDTO.getCallAs());
        call.setCallType(callType.value);
        call.setConfig(startCallDTO.getParsedConfig());
        call.setRoomId(roomId);
        call.setAutoon(isAutoon);
        call = callRepository.insert(call);
        //Copy of call to send in the Async Method
        Call finalCall = call;
        //Calling Async Method
        CompletableFuture.runAsync(() -> startCallNotifications(startCallDTO, roomId, isAutoon, finalCall));
        return call.getRoomId();
    }

    private void startCallNotifications(StartCallDTO startCallDTO, String roomId, Boolean isAutoon, Call call) {
        log.info("Starting Async Call method with DeviceID: {}", startCallDTO.getDeviceId());
        if (isAutoon) {
            log.info("AUTOON: from app {}", startCallDTO.getCallingTo());

            if (startCallDTO.getCallingTo() != null) {
                participantService.createAndSaveParticipant(startCallDTO.getCallingTo(), call);
            }

            List<AppTokenDTO> tokenList = appTokenService.getAppTokenDTOsByToken(startCallDTO.getCallingTo());

            // if app token is registered and there is a subscription for the user
            if (!tokenList.isEmpty() && !subscriberService.getSubscribersByUserId(tokenList.get(0).getUserId()).isEmpty()) {
                if (containsVideoSourceMethod(startCallDTO.getParsedConfig())) { // this is change video call
                    fcmService.sendChangeVideoNotificationMessage(tokenList.get(0), roomId, startCallDTO.getDeviceId(), startCallDTO.getCallAs(), startCallDTO.getParsedConfig());
                } else { // this is normal autoon
                    fcmService.sendAutoonNotificationMessage(tokenList.get(0), roomId, startCallDTO.getDeviceId(), startCallDTO.getCallAs(), startCallDTO.getParsedConfig());
                }
            } else {
                log.error("No active APP TOKEN found for AUTOON or the subscription is invalid. Check DeviceID: {}. AppToken: {}", startCallDTO.getDeviceId(), startCallDTO.getCallingTo());
            }

        } else {
            List<SubscriberDTO> subscribers = subscriberService.getSubscriberDTOsByDeviceId(startCallDTO.getDeviceId(), startCallDTO.getCallAs());

            List<AppTokenDTO> tokenList = new ArrayList<>();
            subscribers.forEach(subscriber -> tokenList.addAll(appTokenService.getAppTokenDTOsByUserId(subscriber.getUserId())));

            //TODO: Change name service FCMService to MessageService (Common)??
            fcmService.sendStartCallNotificationMessages(tokenList, roomId, startCallDTO.getDeviceId(), startCallDTO.getCallAs(), startCallDTO.getParsedConfig());
        }
        log.info("Call initalized. Ok");
    }

    /**
     * If Config contains method for changevideo source, then change type of message
     *
     * @param config - map of properties an values
     * @return true if contains
     */
    private boolean containsVideoSourceMethod(Map<String, String> config) {
        final String propName = "Method";
        final String changeVideoMethodValue = "changevideosource";

        return config.get(propName) != null && config.get(propName).equals(changeVideoMethodValue);

    }

    /**
     * Service for registering production video.
     *
     * @param registerProductionVideoDTO device that starts produce
     * @throws NotFoundException
     */
    public void registerVideoProduce(RegisterProductionVideoDTO registerProductionVideoDTO) throws NotFoundException {
        log.info("Register start video produce...");

        Optional<Call> call = callRepository.findByRoomId(registerProductionVideoDTO.getRoomId());

        if (call.isPresent()) {
            call.get().setInitDeviceStartProduceTime(LocalDateTime.now());
            callRepository.save(call.get());

            log.info("Start video produce registered. OK");
        } else {
            log.error(callRepository.findByRoomId(registerProductionVideoDTO.getRoomId()).toString());

            log.error("Start video produce could not be registered. Call do not exist. Room: {}", registerProductionVideoDTO.getRoomId());
            throw new NotFoundException(Call.class.getCanonicalName());
        }
    }

    /**
     * Updating Room Status to RINGING and the participant that caused it.
     *
     * @param roomId   - room id
     * @param appToken - participant token
     * @throws NotFoundException
     */
    public void registerStartRinging(String roomId, String appToken) throws NotFoundException, InterruptedException {
        log.info("Registering  RINGING. Room: {}. participant {}", roomId, appToken);

        Optional<Call> callOptional = callRepository.findByRoomId(roomId);

        if (callOptional.isPresent()) {
            Call call = callOptional.get();
            if (call.getEnterRingingTime() == null) {
                call.setStatus(StatusEnum.RING.status);
                call.setEnterRingingTime(LocalDateTime.now());
                call = callRepository.save(call);
                log.info("Registered CALL RINGING - OK. Room: {}", roomId);
            }

            if (participantService.updateParticipantRingingTime(call, appToken)) {
                log.info("Registered PARTICIPANT RINGING - OK. Room: {}. participant {}", roomId, appToken);
            }
        } else {
            throw new NotFoundException(Call.class.getCanonicalName());
        }

    }

    /**
     * Updates the status call to Preview and times.
     * <p>
     * Participant registered - Preview Time
     *
     * @param ringToPreviewDTO - the room and participant token
     * @return updated call
     * @throws NotFoundException
     */
    public Optional<Call> ringToPreview(RingToPreviewDTO ringToPreviewDTO) throws InterruptedException {
        log.info("Registering  PREVIEW. Room: {}. participant {}", ringToPreviewDTO.getRoomId(), ringToPreviewDTO.getAppToken());

        Optional<Call> callOptional = callRepository.findByRoomId(ringToPreviewDTO.getRoomId());

        if (callOptional.isPresent()) {
            Call call = callOptional.get();
            if (call.getEnterPreviewTime() == null) {
                call.setEnterPreviewTime(LocalDateTime.now());
                call.setStatus(StatusEnum.PREVIEW.status);
                call = callRepository.save(call);
                log.info("Registered  PREVIEW - OK. Room: {}. participant {}", ringToPreviewDTO.getRoomId(),
                        ringToPreviewDTO.getAppToken());
            }

            if (participantService.updateParticipantPreviewTime(call, ringToPreviewDTO.getAppToken())) {
                log.info("Registered PARTICIPANT RINGING - OK. Room: {}. participant {}", ringToPreviewDTO.getRoomId(), ringToPreviewDTO.getAppToken());
            }
            return callOptional;
        } else {
            throw new NotFoundException(Call.class.getCanonicalName());
        }
    }

    /**
     * Updates the status call to Conversation and times. Participant that pickup -
     * updated - Conversation Time Rest of participants if any - updated -
     * MissedCall Time
     * <p>
     * Notification sent to participants that missed call
     *
     * @param pickupDTO - the room and participant token
     * @throws Exception
     */
    public void pickUp(PickupDTO pickupDTO) {
        log.info("Registering  PICKUP. Room: {}. participant {}", pickupDTO.getRoomId(), pickupDTO.getAppToken());

        Optional<Call> callOptional = callRepository.findByRoomId(pickupDTO.getRoomId());

        if (callOptional.isPresent()) {
            Call call = callOptional.get();
            if (call.getEnterConversationTime() == null) {
                call.setEnterConversationTime(LocalDateTime.now());
                call.setStatus(StatusEnum.CONVERSATION.status);
                call = callRepository.save(call);

            }
            if (participantService.updateParticipantPickupTime(call, pickupDTO.getAppToken())) {
                log.info("Registered PARTICIPANT RINGING - OK. Room: {}. participant {}", pickupDTO.getRoomId(), pickupDTO.getAppToken());
            }

            // All not pickup participants of the room will be considered missed
            participantService.updateParticipantMissedTimeAttended(call, pickupDTO.getAppToken());

            log.info("Registered  PICKUP - OK. Room: {}. participant {}", pickupDTO.getRoomId(), pickupDTO.getAppToken());
        } else {
            throw new NotFoundException(Call.class.getCanonicalName());
        }
    }

    /**
     * Updates Participant registered - Hangup Time
     *
     * @param hangupDTO - the room and participant token
     * @throws NotFoundException
     */
    public void hangUp(HangupDTO hangupDTO) throws NotFoundException {
        log.info("Registering  HANGUP. Room: {}. participant {}", hangupDTO.getRoomId(), hangupDTO.getAppToken());
        Optional<Call> callOptional = callRepository.findByRoomId(hangupDTO.getRoomId());
        if (callOptional.isPresent()) {
            if (participantService.updateParticipantHangupTime(callOptional.get(), hangupDTO.getAppToken())) {
                log.info("Registered  HANGUP - OK. Room: {}. Participant {}", hangupDTO.getRoomId(), hangupDTO.getAppToken());
            }
        } else {
            log.error("FAILED  REJECT - KO. - Call NOT FOUND. Room: {}, AppToken: ", hangupDTO.getRoomId(), hangupDTO.getAppToken());
            throw new NotFoundException(Call.class.getCanonicalName());
        }
    }


    /**
     * Updates Participant registered - Reject Time
     *
     * @param rejectDTO - the room and participant token
     * @throws NotFoundException
     */
    public void reject(RejectDTO rejectDTO) throws NotFoundException, InterruptedException {
        log.info("Registering  REJECT. Room: {}. participant {}", rejectDTO.getRoomId(), rejectDTO.getAppToken());
        Optional<Call> callOptional = callRepository.findByRoomId(rejectDTO.getRoomId());
        if (callOptional.isPresent()) {
            if (participantService.updateParticipantRejectTime(callOptional.get(), rejectDTO.getAppToken())) {
                log.info("Registered  REJECT - OK. Room: {}. Participant {}", rejectDTO.getRoomId(), rejectDTO.getAppToken());
            }
        } else {
            log.error("FAILED  REJECT - KO. - Call NOT FOUND. Room: {}, AppToken: ", rejectDTO.getRoomId(), rejectDTO.getAppToken());
            throw new NotFoundException(Call.class.getCanonicalName());
        }
    }

    /**
     * Updates the status call to Finished and times.
     * <p>
     * All registered participants - Finished Time
     *
     * @param endcallDTO - the room
     * @throws NotFoundException
     */
    public void endCall(EndcallDTO endcallDTO) throws NotFoundException {
        log.info("Registering  ENDCALL. Room: {}, Reason: {}.", endcallDTO.getRoomId(),
                ReasonEnum.fromReasonType((endcallDTO.getReason())));
        Optional<Call> callOptional = callRepository.findByRoomId(endcallDTO.getRoomId());
        if (callOptional.isPresent()) {
            Call call = callOptional.get();
            switch (ReasonEnum.fromReasonType((endcallDTO.getReason()))) {
                case LAST_PARTICIPANT_REJECTED:
                case END_PARTICIPANT_CONVERSATION:
                    if (call.getFinishedTime() == null) {
                        call.setStatus(StatusEnum.FINISHED.status);
                        call.setFinishedTime(LocalDateTime.now());
                        callRepository.save(call);
                    }
                    log.info("Registered ENDCALL - OK. Room: {}", endcallDTO.getRoomId());
                    break;
                case MANAGED_CALL:
                    if (call.getFinishedTime() == null) {
                        call.setManaged(true);
                        call.setFinishedTime(LocalDateTime.now());
                        call.setStatus(StatusEnum.FINISHED.status);
                        call = callRepository.save(call);
                    }
                    if (call.isAutoon()) {
                        // Update participant
                        participantService.updateParticipantManageRequestTime(call);
                    } else {
                        // Update participant and send notifications
                        participantService.updateParticipantMissedTimeAttended(call, endcallDTO.getAppToken() != null ? endcallDTO.getAppToken() : call.getInitDeviceId());
                    }
                    log.info("Registered  ENDCALL - OK. Room: {}", endcallDTO.getRoomId());
                    break;
                case MISSED_CALL:
                    if (callOptional.get().getMissedCallTime() == null) {
                        call.setStatus(StatusEnum.MISSED.status);
                        call.setMissedCallTime(LocalDateTime.now());
                        call = callRepository.save(call);
                    }
                    if (callOptional.get().isAutoon()) {
                        // Update participant
                        participantService.updateParticipantMissedTime(call);
                    } else {
                        participantService.updateParticipantMissedNotAttended(call);
                    }
                    log.info("Registered  ENDCALL - OK. Room: {}", endcallDTO.getRoomId());
                    break;
                case UNKOWN:
                    log.error("Some issue had the monitor/main board, is possible were issue about connection. Reason: {}",
                            endcallDTO.getReason());
                    break;
            }
        } else {
            log.error("FAILED  ENDCALL - KO. - Call NOT FOUND. Room: {}, AppToken: {}", endcallDTO.getRoomId(), endcallDTO.getAppToken());
            throw new NotFoundException(Call.class.getCanonicalName());
        }
    }

    public Optional<Call> findByRoomId(String roomId) {
        return callRepository.findByRoomId(roomId);
    }

    public Call saveCall(Call call) {
        return callRepository.save(call);
    }

}

