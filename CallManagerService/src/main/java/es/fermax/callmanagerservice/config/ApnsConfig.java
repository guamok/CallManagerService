package es.fermax.callmanagerservice.config;

import com.eatthepath.pushy.apns.ApnsClient;
import com.eatthepath.pushy.apns.ApnsClientBuilder;
import com.eatthepath.pushy.apns.auth.ApnsSigningKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;

@Configuration
public class ApnsConfig {

    private static final Logger log = LoggerFactory.getLogger(ApnsConfig.class);
    public static final String PATH_KEY = "/home/fermaxuser/AuthKey_APN.p8";

    // teamId identificador de cuenta de apple
    @Value("${apns.teamId}")
    public String teamId;

    // keyId identificador de la clave para enviar notificacion voip
    @Value("${apns.keyId}")
    public String keyId;

    @Value("${apns.host.production}")
    public String productionApnHost;

    @Value("${apns.host.development}")
    public String developmentApnHost;

    @Value("${apns.apnkey}")
    public String apnkey;

    /**
     *  SANDBOX APN
     *
     * @return client sandbox pushy
     * @throws IOException Input/Output exception
     * @throws NoSuchAlgorithmException algorithm exception
     * @throws InvalidKeyException invalid key exception
     */
   @Bean(name = "sandbox")
    public ApnsClient TokenAPNInstanceConfig() throws IOException, NoSuchAlgorithmException, InvalidKeyException {
        File fCertificate = this.writeFile();

        final ApnsClient apnsClient = new ApnsClientBuilder()
                .setApnsServer(developmentApnHost)
                .setSigningKey(ApnsSigningKey.loadFromPkcs8File(fCertificate,
                        teamId, keyId))
                .build();

       log.info("Apns SANDBOX has been initialized successfully." );
       return apnsClient;
    }

    /**
     *  PRODUCTION APN
     *
     * @return production sandbox pushy
     * @throws IOException Input/Output exception
     * @throws NoSuchAlgorithmException algorithm exception
     * @throws InvalidKeyException invalid key exception
     */
    @Bean(name = "production")
    public ApnsClient ProdAPNInstanceConfig() throws IOException, NoSuchAlgorithmException, InvalidKeyException {
        File fCertificate = this.writeFile();

        final ApnsClient apnsClient = new ApnsClientBuilder()
                .setApnsServer(productionApnHost)
                .setSigningKey(ApnsSigningKey.loadFromPkcs8File(fCertificate,
                        teamId, keyId))
                .build();

        log.info("Apns PROD has been initialized successfully." );
        return apnsClient;
    }

    private File writeFile() throws IOException {

        String path = PATH_KEY;

        File file = new File(path);
        if (!file.exists()) {
            file.createNewFile();
        }

        FileWriter fw = new FileWriter(file);
        BufferedWriter bw = new BufferedWriter(fw);
        bw.write(apnkey);
        bw.close();
        return file;
    }
}
