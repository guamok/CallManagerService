package es.fermax.callmanagerservice.service;

import com.eatthepath.pushy.apns.ApnsClient;
import com.eatthepath.pushy.apns.PushNotificationResponse;
import com.eatthepath.pushy.apns.util.SimpleApnsPushNotification;
import com.eatthepath.pushy.apns.util.concurrent.PushNotificationFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Service
public class ApnMessagingService {

    private static final Logger log = LoggerFactory.getLogger(ApnMessagingService.class);

    @Autowired
    @Qualifier("sandbox")
    public ApnsClient sandboxAPNInstanceConfig;

    @Autowired
    @Qualifier("production")
    public ApnsClient productionAPNInstanceConfig;

    /**
     * For iOS apn server.
     *
     * @param notification notificatio for sending
     * @return notitifcation
     */
    public PushNotificationFuture sendMessageAPN (SimpleApnsPushNotification notification, Boolean sandbox) throws IOException, NoSuchAlgorithmException, InvalidKeyException {
        log.info("Send notification from start call. " );

        PushNotificationFuture<SimpleApnsPushNotification, PushNotificationResponse<SimpleApnsPushNotification>> apns = null;
        // For testing from production environment from apps
        if(sandbox){
            apns = sandboxAPNInstanceConfig.sendNotification(notification);
            log.info("Notification sended to sandbox." );
        }else{
            apns = productionAPNInstanceConfig.sendNotification(notification);
            log.info("Notification sended to production." );
        }

        return  apns;
    }
}
