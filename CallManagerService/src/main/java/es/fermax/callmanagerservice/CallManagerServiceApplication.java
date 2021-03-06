package es.fermax.callmanagerservice;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;

import es.fermax.fermaxsecurity.CorsFilter;
import es.fermax.fermaxsecurity.MethodSecurityConfiguration;
import es.fermax.fermaxsecurity.OAuth2ResourceServerConfiguration;
import es.fermax.fermaxsecurity.UserIDEncryptorService;
import es.fermax.logging.config.LoggingAspectConfiguration;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@EnableSwagger2
@SpringBootApplication
@Import({LoggingAspectConfiguration.class,
	MethodSecurityConfiguration.class,
	CorsFilter.class,
	OAuth2ResourceServerConfiguration.class,
	UserIDEncryptorService.class})
public class CallManagerServiceApplication {

	private static final Logger log = LoggerFactory.getLogger(CallManagerServiceApplication.class);

	public static void main(String[] args) throws UnknownHostException {
		SpringApplication app = new SpringApplication(CallManagerServiceApplication.class);
		Environment env = app.run(args).getEnvironment();
		log.info("\n----------------------------------------------------------\n\t"
						+ "Application '{}' is running! Access URLs:\n\t" 
						+ "Local: \t\thttp://localhost:{}\n\t"
						+ "External: \thttp://{}:{}\n\t" 
						+ "Version: {}\n\t"
						+ "Profile(s): \t{}\n----------------------------------------------------------",
				env.getProperty("spring.application.name"), 
				env.getProperty("server.port"),
				InetAddress.getLocalHost().getHostAddress(), 
				env.getProperty("server.port"),
				env.getProperty("artifact.version"), 
				env.getActiveProfiles());
	}

}
