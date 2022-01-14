package es.fermax.callmanagerservice.service;


import com.eatthepath.pushy.apns.ApnsPushNotification;
import com.eatthepath.pushy.apns.DeliveryPriority;
import com.eatthepath.pushy.apns.util.ApnsPayloadBuilder;
import com.eatthepath.pushy.apns.util.SimpleApnsPayloadBuilder;
import com.eatthepath.pushy.apns.util.SimpleApnsPushNotification;
import com.eatthepath.pushy.apns.util.TokenUtil;
import com.eatthepath.pushy.apns.util.concurrent.PushNotificationFuture;
import com.google.firebase.messaging.*;
import com.google.firebase.messaging.ApnsConfig.Builder;
import com.google.gson.Gson;
import es.fermax.callmanagerservice.controller.dto.AppTokenDTO;
import es.fermax.callmanagerservice.enums.*;
import es.fermax.callmanagerservice.exception.NotFoundException;
import es.fermax.callmanagerservice.exception.NotificationNotImplementedException;
import es.fermax.callmanagerservice.exception.TargetNotImplementedException;
import es.fermax.callmanagerservice.model.Notification;
import es.fermax.callmanagerservice.model.*;
import es.fermax.callmanagerservice.repo.ApnMessageRepository;
import es.fermax.callmanagerservice.repo.AppTokenRepository;
import es.fermax.callmanagerservice.repo.FcmMessageRepository;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

@Service
public class FCMService {

    private static final String TITLE = "NotificationTitle";
    private static final String BODY = "NotificationBody";
    private static final String TTL_APNS = "apns-expiration";
    private static final String CALL_DIVERT_TEXT = "CallDivert: ";
    private static final String IOS = "ios";
    private static final String CALL = "Call";
    public static final Duration DEFAULT_EXPIRATION_PERIOD = Duration.ofDays(1L);

    @Value("${firebase-sdk.keys.ttl}")
    public Integer callNotificationTtlNumSecs;

    @Value("${apns.topic}")
    public String topic; // for example, "com.fermax.bluefermax.mediasoup.voip"


    // invalidationTime.
    @Value("${apns.invalidationTime}")
    public String invalidationTime;

    // priority
    @Value("${apns.deliveryPriority}")
    public String deliveryPriority;

    @Autowired
    FcmMessageRepository repo;

    @Autowired
    ApnMessageRepository apnMessageRepository;

    @Autowired
    AppTokenRepository appTokenRepository;

    @Autowired
    AppTokenService appTokenService;

    @Autowired
    FirebaseMessagingService firebaseMessagingService;

    @Autowired
    ApnMessagingService apnMessagingService;

    @Autowired
    CallService callService;

    @Autowired
    ParticipantService participantService;

    private static final Logger log = LoggerFactory.getLogger(FCMService.class);

    public FCMService() {
        super();
    }

    /**
     * Save and send the message to Firebase
     *
     * @param fcmMessage - message to perstis compatible with Firebase
     * @param os         - android or iOS
     */
    private void createAndSendMessage(FcmMessage fcmMessage, String os) {
        createAndSendMessage(fcmMessage, os, null);
    }

    /**
     * Save and send the message to Firebase
     *
     * @param fcmMessage - message to perstis compatible with Firebase
     * @param os         - android or iOS
     * @param ttl        - time to live
     */
    private void createAndSendMessage(FcmMessage fcmMessage, String os, Integer ttl) {
        repo.save(fcmMessage);

        if (os.equalsIgnoreCase(OsEnum.WEB.name())) {
            fcmMessage.setDeliveryStatus(NotificationStatusEnum.SENT.status);
        } else {
            Message messageToSend = createFirebaseMessage(fcmMessage, os, ttl);
            FirebaseSendResponse response = firebaseMessagingService.sendMessage(messageToSend);

            if (response.isSent) {
                fcmMessage.setFcmId(response.getFcmId());
                fcmMessage.setDeliveryStatus(NotificationStatusEnum.SENT.status);
            } else {
                fcmMessage.setDeliveryStatus(NotificationStatusEnum.ERROR.status);
                fcmMessage.setErrorDesc(response.getFailureReason());
                // HANDLE ERRORS
                handleErrorCode(fcmMessage, response);
            }
        }
        repo.save(fcmMessage);
    }

    /**
     * If unregistered the appToken might be inactivated
     *
     * @param fcmMessage - message entity
     * @param response   - firebase response
     */
    private void handleErrorCode(FcmMessage fcmMessage, FirebaseSendResponse response) {
        log.info("Handling error for: {}", response.getFailureReason());
        if (MessagingErrorCode.UNREGISTERED.equals(response.errorCode)) {
            log.info("Error Code: {}", response.getErrorCode());
            if (fcmMessage.getTarget().equals(TargetEnum.TOKEN.target)) {
                // user with token is unregistered (possible uninstalled the app)
                AppToken appToken = appTokenRepository.findByTokenAndActiveTrue(fcmMessage.getTargetValue()).stream().findAny()
                        .orElse(null);
                if (appToken != null) {
                    appToken.setActive(false);
                    appTokenRepository.save(appToken);
                    log.info("User apptoken inactivated. AppTokenId: {}", appToken.getId());
                }
            }
        }
    }

    /**
     * Message to Persist
     *
     * @param appToken         - User and Mobile app representation
     * @param notificationCase - what is the case
     * @return - message to persist
     */
    private FcmMessage createMessageContent(AppTokenDTO appToken, NotificationCaseEnum notificationCase) {
        FcmMessage createdMessage = new FcmMessage();

        createdMessage.setTarget(TargetEnum.TOKEN.target);
        createdMessage.setTargetValue(appToken.getToken());

        Notification notification = new Notification();
        switch (notificationCase) {
            case MISSED_CALL:
                setMessageType(createdMessage, DataKeys.TYPE_CALL_END);
                break;
            case ATTENDED_CALL:
                setMessageType(createdMessage, DataKeys.TYPE_CALL_ATTEND);
                break;
            case START_CALL:
                notification.setTitle("Incomming call...");
                notification.setBody(CALL_DIVERT_TEXT + LocalDate.now());
                setMessageType(createdMessage, DataKeys.TYPE_CALL);
                break;
            case AUTOON:
                notification.setTitle("AutoON");
                notification.setBody(CALL_DIVERT_TEXT + LocalDate.now());
                setMessageType(createdMessage, DataKeys.TYPE_AUTOON);
                break;
            case CHANGE_VIDEO_SOURCE:
                notification.setTitle("ChangeVideoSource");
                notification.setBody(CALL_DIVERT_TEXT + LocalDate.now());
                setMessageType(createdMessage, DataKeys.TYPE_CHANGE_VIDEO_SOURCE);
                break;
            default:
                log.warn("Not implemented message content");
                throw new NotificationNotImplementedException();
        }
        createdMessage.setNotification(notification);
        return createdMessage;
    }

    private void setMessageType(FcmMessage createdMessage, String type) {
        HashMap<String, String> data = new HashMap<>();
        data.put(DataKeys.TYPE, type);
        data.put(DataKeys.ACKNOWLEDGE, Boolean.TRUE.toString());
        createdMessage.setData(data);
    }

    /**
     * Message to deliver through Firebase
     *
     * @param fcmMessage - message to persist compatible with Firebase
     * @param os         - android or iOS
     * @param ttl        - time to live
     * @return message entry
     */
    private Message createFirebaseMessage(FcmMessage fcmMessage, String os, Integer ttl) {

        Message.Builder messageBuilder = Message.builder().putAllData(fcmMessage.getData());

        if (StringUtils.isEmpty(os)) {
            os = OsEnum.DEFAULT.toString();
        }

        switch (OsEnum.valueOf(os.toUpperCase())) {
            case ANDROID:
                addMessageToData(fcmMessage, messageBuilder, ttl);
                break;
            case IOS:
                addMessageToApnsConfig(fcmMessage, messageBuilder, ttl);
                break;
            default:
                addMessageToData(fcmMessage, messageBuilder, ttl);
                addMessageToApnsConfig(fcmMessage, messageBuilder, ttl);
                break;
        }

        if (fcmMessage.getTarget().equals(TargetEnum.TOKEN.target)) {
            messageBuilder.setToken(fcmMessage.getTargetValue());
        } else {
            log.warn("Not implemented message target {}", fcmMessage.getTarget());
            throw new TargetNotImplementedException();
        }

        return messageBuilder.build();
    }

    /**
     * Set configuration on Android Firebase message and set in JSON on FcmMessage
     * entity.
     *
     * @param fcmMessage     - message to persist compatible with Firebase
     * @param messageBuilder - Firebase message builder
     * @param timeToLive     - time to live
     */
    private void addMessageToData(FcmMessage fcmMessage, Message.Builder messageBuilder, Integer timeToLive) {
        Notification notification = fcmMessage.getNotification();

        if (timeToLive != null) {
            int ttl = timeToLive * 1000;
            AndroidConfig androidConfig = AndroidConfig.builder().setTtl(ttl).build();
            String configAsJson = new Gson().toJson(androidConfig);

            fcmMessage.setAndroidConfig(configAsJson);
            messageBuilder.setAndroidConfig(androidConfig);
        }
        messageBuilder.putData(TITLE, notification.getTitle()).putData(BODY, notification.getBody());
    }

    /**
     * Set configuration on Apns Firebase message and set in JSON on FcmMessage
     * entity.
     *
     * @param fcmMessage     - message to persist compatible with Firebase
     * @param messageBuilder - Firebase message builder
     * @param timeToLive     - time to live
     */
    private void addMessageToApnsConfig(FcmMessage fcmMessage, Message.Builder messageBuilder, Integer timeToLive) {
        Notification notification = fcmMessage.getNotification();
        Builder apnsConfigBuilder = ApnsConfig
                .builder().setAps(
                        Aps.builder()
                                .setAlert(ApsAlert.builder().setBody(notification.getBody() != null ? notification.getBody() : "-")
                                        .setTitle(notification.getTitle() != null ? notification.getTitle() : "Notification").build())
                                .build());

        if (timeToLive != null) {
            String ttl = "0";

            if (timeToLive > 0) {
                ZonedDateTime gmt = LocalDateTime.now().atZone(ZoneId.of("GMT"));
                long epochGMT = gmt.toInstant().toEpochMilli() / 1000;
                ttl = String.valueOf(epochGMT + Long.valueOf(timeToLive));
                log.info("Notification details. Title: {}  -  Message: {}  -  Epoch: {}  -  TTL: {}  -  Epoch+TTL: {}",
                        notification.getTitle(), notification.getBody(), epochGMT, timeToLive, ttl);
            }
            apnsConfigBuilder.putHeader(TTL_APNS, ttl);
        }

        ApnsConfig apnsConfig = apnsConfigBuilder.build();
        String configAsJson = new Gson().toJson(apnsConfig);

        fcmMessage.setApnsConfig(configAsJson);
        messageBuilder.setApnsConfig(apnsConfig);
    }


    /**
     * Send a Missed Call notification to token. (Somebody pickup the call)
     *
     * @param appTokenNotify target
     * @param deviceId       associated device
     * @param callAs         call as device paired
     * @param appTokenPickup who pickup
     */
    public void sendMissedCallAttendedNotificationMessage(String appTokenNotify, String deviceId, String callAs, String appTokenPickup) {
        log.info("Sending Missed Call Notification Attended to token {}", appTokenNotify);
        List<AppTokenDTO> appTokenList = appTokenService.getAppTokenDTOsByToken(appTokenNotify);

        if (!appTokenList.isEmpty()) {
            AppTokenDTO appTokenDTO = appTokenList.get(0);

            FcmMessage fcmMessage = createMessageContent(appTokenDTO, NotificationCaseEnum.ATTENDED_CALL);
            fcmMessage.getNotification().setBody("Missed Call Attended by somebody."); // TODO check translation
            fcmMessage.getNotification().setTitle("You've just missed the call from device " + deviceId); // TODO check message
            fcmMessage.getData().put(DataKeys.DEVICE_ID, deviceId);
            fcmMessage.getData().put(DataKeys.CALL_AS, callAs);
            createAndSendMessage(fcmMessage, appTokenDTO.getOs());
            log.info("Missed Call Notification to user {} sent. Picked up by: {}", appTokenDTO.getToken(), appTokenPickup);
        } else {
            log.error("Missed Call Notification could not be sent due to problems with appToken. Check: {}", appTokenNotify);
        }
    }

    /**
     * Send fcmMessage for missed call not attended by nobody.
     *
     * @param appTokenNotify token
     * @param deviceId       device id
     */
    public void sendMissedCallNotAttendedNotificationMessage(String appTokenNotify, String deviceId, String callAs) {
        log.info("Sending Missed Call Notification Not Attended to token {}", appTokenNotify);
        List<AppTokenDTO> appTokenList = appTokenService.getAppTokenDTOsByToken(appTokenNotify);

        if (!appTokenList.isEmpty()) {
            AppTokenDTO appTokenDTO = appTokenList.get(0);

            FcmMessage fcmMessage = createMessageContent(appTokenDTO, NotificationCaseEnum.MISSED_CALL);
            fcmMessage.getNotification().setBody("Missed Call Not Attended by nobody."); // TODO check translation
            fcmMessage.getNotification().setTitle("You've just missed the call from device " + deviceId); // TODO check message
            fcmMessage.getData().put(DataKeys.DEVICE_ID, deviceId);
            fcmMessage.getData().put(DataKeys.CALL_AS, callAs);
            createAndSendMessage(fcmMessage, appTokenDTO.getOs());
            log.info("Missed Call Notification to user {} sent.", appTokenDTO.getToken());
        } else {
            log.error("Missed Call Notification could not be sent due to problems with appToken. Check: {}", appTokenNotify);
        }
    }


    /**
     * Notify each token with the incoming call
     *
     * @param tokenList - subscribed to call divert
     * @param roomId    - roomId to join in
     */
    public void sendStartCallNotificationMessages(List<AppTokenDTO> tokenList, String roomId, String deviceId, String callAs,
                                                  Map<String, String> config) {
        tokenList.forEach(token -> {
            if(IOS.equalsIgnoreCase(token.getOs())){
                /*  for having compatibility with android's tokens. */
                boolean apn = true;
                try{
                     apn = token.getApn();
                }catch (NullPointerException e) {
                    log.error("It seems this token is old. please verify your APN token. <<Tip>>, check apn flag to [true|false] boolean. This apn flag must exists. cause:{} , message: {} ", e.getCause(), e.getMessage());
                }
                if(apn) { // second if, for having compatibility with android's tokens.
                    // ios, message for APNS
                    ApnMessage apnMessage=null;
                    try {
                        apnMessage = createMessageAPNContent(token);
                        apnMessage.setRoomId(roomId);
                        apnMessage.setDeviceId(deviceId);
                        apnMessage.setCallAs(callAs);
                        createAndSendMessageAPN(apnMessage, token.getSandbox());
                    } catch (IOException e) {
                        log.error("I/O exceptions, cause:{} , message: {} ", e.getCause(), e.getMessage());
                    } catch (NoSuchAlgorithmException e) {
                        log.error(" Algorithm exceptions, cause:{} , message: {} ", e.getCause(), e.getMessage());
                    } catch (InvalidKeyException e) {
                        log.error("Invalid key for APN Service, Please check this. cause:{} , message: {} ", e.getCause(), e.getMessage());
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (NotificationStatusEnum.SENT.status.equalsIgnoreCase(apnMessage.getDeliveryStatus())) {
                        Optional<Call> optionalCall = callService.findByRoomId(roomId);
                        if (optionalCall.isPresent()) {
                            Call call = optionalCall.get();
                            call = callService.saveCall(call);
                            participantService.createAndSaveParticipant(token.getToken(), call);
                        } else {
                            throw new NotFoundException(Call.class.getCanonicalName());
                        }
                    }
                    log.info("Notification to start Call to token {} sent.", token.getToken());
                }else {
                    log.debug("Is possible was firebase token, so discard this token. Not is possible startcall funcionality with this token.");
                }
            }else {
                // start_call, android, firebase
                FcmMessage fcmMessage = createMessageContent(token, NotificationCaseEnum.START_CALL);
                fcmMessage.getData().putAll(config);
                fcmMessage.getData().put(DataKeys.ROOM_ID, roomId);
                fcmMessage.getData().put(DataKeys.DEVICE_ID, deviceId);
                fcmMessage.getData().put(DataKeys.CALL_AS, callAs);
                createAndSendMessage(fcmMessage, token.getOs(), callNotificationTtlNumSecs);

                if (fcmMessage.getData().get(DataKeys.TYPE).equalsIgnoreCase(DataKeys.TYPE_CALL)
                        && fcmMessage.getDeliveryStatus().equals(NotificationStatusEnum.SENT.status)) {
                    Optional<Call> optionalCall = callService.findByRoomId(roomId);
                    if (optionalCall.isPresent()) {
                        Call call = optionalCall.get();
                        call = callService.saveCall(call);
                        participantService.createAndSaveParticipant(token.getToken(), call);
                    } else {
                        throw new NotFoundException(Call.class.getCanonicalName());
                    }
                }
                log.info("Notification to start Call to token {} sent.", token.getToken());
            }

        });
    }


    public void sendAutoonNotificationMessage(AppTokenDTO token, String roomId, String deviceId, String callAs,
                                              Map<String, String> config) {
        FcmMessage fcmMessage = createMessageContent(token, NotificationCaseEnum.AUTOON);
        fcmMessage.getData().putAll(config);
        fcmMessage.getData().put(DataKeys.ROOM_ID, roomId);
        fcmMessage.getData().put(DataKeys.DEVICE_ID, deviceId);
        fcmMessage.getData().put(DataKeys.CALL_AS, callAs);
        createAndSendMessage(fcmMessage, token.getOs());
        log.info("Notification for AUTOON to token {} sent.", token.getToken());
    }

    public void sendChangeVideoNotificationMessage(AppTokenDTO token, String roomId, String deviceId, String callAs,
                                                   Map<String, String> config) {
        FcmMessage fcmMessage = createMessageContent(token, NotificationCaseEnum.CHANGE_VIDEO_SOURCE);
        fcmMessage.getData().putAll(config);
        fcmMessage.getData().put(DataKeys.ROOM_ID, roomId);
        fcmMessage.getData().put(DataKeys.DEVICE_ID, deviceId);
        fcmMessage.getData().put(DataKeys.CALL_AS, callAs);
        createAndSendMessage(fcmMessage, token.getOs());
        log.info("Notification for CHANGE_VIDEO_SOURCE to token {} sent.", token.getToken());
    }


    /**
     * @param fcmMessageId
     * @param userId
     * @param attended
     * @throws InterruptedException
     */
    public void receiveAckMessage(String fcmMessageId, Integer userId, Boolean attended) throws InterruptedException {
        Optional<FcmMessage> fcmMessageOptional = repo.findByFcmId(fcmMessageId);

        if (fcmMessageOptional.isPresent()) {
            FcmMessage fcmMessage = fcmMessageOptional.get();
            if ((Boolean.TRUE.equals(attended))) {
                fcmMessage.setDeliveryStatus(NotificationStatusEnum.ATTENDED.status);
            }

            repo.save(fcmMessage);

            sendStartRingingMessage(fcmMessageId, fcmMessage);

        } else {
            log.error("Not found the message to acknowledge {} for user {}", fcmMessageId, userId);
        }

    }

    /**
     * @param fcmMessageId
     * @param fcmMessage
     * @throws InterruptedException
     */
    private void sendStartRingingMessage(String fcmMessageId, FcmMessage fcmMessage) throws InterruptedException {
        if (fcmMessage.getData() != null &&
                fcmMessage.getData().get(DataKeys.TYPE) != null &&
                fcmMessage.getData().get(DataKeys.TYPE).equals(DataKeys.TYPE_CALL)) {
            // start ringing
            if (fcmMessage.getData().get(DataKeys.ROOM_ID) != null &&
                    fcmMessage.getTarget().equals(TargetEnum.TOKEN.target) &&
                    fcmMessage.getTargetValue() != null) {
                callService.registerStartRinging(fcmMessage.getData().get(DataKeys.ROOM_ID),
                        fcmMessage.getTargetValue());
                log.info("The Start Ringing message sent due to ACK message of incoming call. RoomId: {}, AppToken: {}",
                        fcmMessage.getData().get(DataKeys.ROOM_ID), fcmMessage.getTargetValue());
            }
        } else {
            log.error("The ACK Message refers to instance that is incorrect for Start Ringing. FcmID: {}", fcmMessageId);
        }

    }

    /**
     * Message APN iOS, to Persist
     *
     * @param appToken - User and Mobile app representation
     * @return - message to persist
     */
    private ApnMessage createMessageAPNContent(AppTokenDTO appToken) {
        ApnMessage createdMessage = new ApnMessage();

        createdMessage.setTargetValue(appToken.getToken());

        Notification notification = new Notification();
        notification.setTitle("Incomming call...");
        notification.setBody("CallDivert: " + LocalDate.now());
        //setMessageType(createdMessage, DataKeys.TYPE_CALL);


        createdMessage.setNotification(notification);

        return createdMessage;
    }

    /**
     * Create and send Message to APN
     *
     * @param apnMessage - message to persist compatible with APN
     */
    private void createAndSendMessageAPN(ApnMessage apnMessage, Boolean sandbox) throws IOException, NoSuchAlgorithmException, InvalidKeyException, ExecutionException, InterruptedException {
        apnMessageRepository.save(apnMessage);

        SimpleApnsPushNotification messageToSend = createApnMessage(apnMessage);
        PushNotificationFuture response          = apnMessagingService.sendMessageAPN(messageToSend, sandbox);
        response.isDone();

        apnMessage.setDeliveryStatus(NotificationStatusEnum.SENT.status);
        // TODO: Update document, wait for mongoDB

        /*
        if(response.isDone()){
            apnMessage.setDeliveryStatus(NotificationStatusEnum.SENT.status);
        }else{
            apnMessage.setDeliveryStatus(NotificationStatusEnum.ERROR.status);
        }
        */



        apnMessageRepository.save(apnMessage);
    }

    /**
     * Message to deliver through apple APN
     *
     * @param apnMessage - message to persist compatible with Apn
     * @return message entry
     */
    private SimpleApnsPushNotification createApnMessage(ApnMessage apnMessage) {

        //Message.Builder messageBuilder = Message.builder().putAllData(fcmMessage.getData());
        ApnsPushNotification apnsPushNotification;
        final SimpleApnsPushNotification pushNotification;

        final ApnsPayloadBuilder payloadBuilder = new SimpleApnsPayloadBuilder();

        // keep alive
        Instant iTime = null;
        if(invalidationTime != null){
            iTime = Instant.now().plus(Duration.ofDays(Long.valueOf(invalidationTime)));
        }else{
            iTime = Instant.now().plus(DEFAULT_EXPIRATION_PERIOD);
        }
        // Priority of message
        DeliveryPriority dp = null;
        if( deliveryPriority != null ){
            dp = DeliveryPriority.valueOf(deliveryPriority);
        }else{
            dp = DeliveryPriority.IMMEDIATE;
        }

        //The title of the notification. Apple Watch displays this string in the short look notification interface. Specify a string that’s quickly understood by the user.
        payloadBuilder.setAlertTitle("Call Fermax");

        //The content of the alert message.
        payloadBuilder.setAlertBody("raul!");

        //custom properties
        payloadBuilder.addCustomProperty("Method", CALL );
        payloadBuilder.addCustomProperty("FermaxNotificationType", CALL);
        payloadBuilder.addCustomProperty("DeviceId", apnMessage.getDeviceId());
        payloadBuilder.addCustomProperty("CallAs", apnMessage.getCallAs());
        payloadBuilder.addCustomProperty("RoomId", apnMessage.getRoomId());

        final String payload = payloadBuilder.build();

        //final String token = TokenUtil.sanitizeTokenString("<3672be8490e852cf0eadd52774191135760c7b1a5547d963a1cb3d33f9a06a0f>");
        //token abel
        //final String token = TokenUtil.sanitizeTokenString("<2a4077a9bbfc5eec5f8bb7a4af750adce2b14f5089067ea963b40b75dc4fcd3a>");
        final String token = TokenUtil.sanitizeTokenString(apnMessage.getTargetValue());

        pushNotification = new SimpleApnsPushNotification(token, topic, payload, iTime, dp);

        return pushNotification;
    }


}
