package es.fermax.callmanagerservice.config;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import es.fermax.callmanagerservice.model.Call;
import es.fermax.callmanagerservice.model.FcmMessage;
import es.fermax.callmanagerservice.model.Participant;
import es.fermax.callmanagerservice.model.Subscriber;
import es.fermax.callmanagerservice.repo.CallRepository;
import es.fermax.callmanagerservice.repo.FcmMessageRepository;
import es.fermax.callmanagerservice.repo.ParticipantRepository;
import es.fermax.callmanagerservice.repo.SubscriberRepository;
import org.bson.UuidRepresentation;
import org.bson.codecs.UuidCodecProvider;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;

import javax.annotation.PostConstruct;

@Configuration
@EnableMongoAuditing
@DependsOn("mongoTemplate")
public class MongoDbConfiguration {

    @Value("${spring.data.mongodb.database}")
    String database;
    @Value("${spring.data.mongodb.uri}")
    String uri;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    CallRepository callRepository;

    @Autowired
    ParticipantRepository participantRepository;

    @Autowired
    FcmMessageRepository fcmMessageRepository;

    @Autowired
    SubscriberRepository subscriberRepository;


    public MongoDbConfiguration(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Bean
    public MongoDatabase mongoDatabase() {
        MongoClient mongoClient;
        MongoDatabase mongoDatabase;

        mongoClient = MongoClients.create(buildMongoClientSettings(uri));
        mongoDatabase = mongoClient.getDatabase(database).withCodecRegistry(codecRegistries());
        mongoClient.close();

        return mongoDatabase;
    }


    //Here goes the index generation
    @PostConstruct
    public void initIndexes() {
        mongoTemplate.indexOps(Participant.class)
                .ensureIndex(
                        new Index().on("SENT_INVITATION_TIME", Sort.Direction.ASC).named("idx_sent_invitation_time")
                );
        mongoTemplate.indexOps(Participant.class)
                .ensureIndex(
                        new Index().on("APP_TOKEN", Sort.Direction.ASC).on("CALL_ID", Sort.Direction.ASC).named("idx_app_token_call_id")
                );
        mongoTemplate.indexOps(Call.class)
                .ensureIndex(
                        new Index().on("ROOM_ID", Sort.Direction.ASC).named("idx_room_id")
                );
        mongoTemplate.indexOps(Call.class)
                .ensureIndex(
                        new Index().on("STATUS", Sort.Direction.ASC).named("idx_status")
                );
        mongoTemplate.indexOps(Call.class)
                .ensureIndex(
                        new Index().on("INIT_DEVICE_ID", Sort.Direction.ASC).named("idx_init_device_id")
                );
        mongoTemplate.indexOps(Call.class)
                .ensureIndex(
                        new Index().on("CREATION_DATE", Sort.Direction.ASC).named("idx_creation_date")
                );
        mongoTemplate.indexOps(Call.class)
                .ensureIndex(
                        new Index().on("CALL_TYPE", Sort.Direction.ASC).named("idx_call_type")
                );
    }

    @PostConstruct
    public void initDatabase() {
        Call call = new Call();
        call.setRoomId("roomStartId");
        callRepository.save(call);
        callRepository.delete(call);
        Participant participant = new Participant();
        participant.setAppToken("APP");
        participantRepository.save(participant);
        participantRepository.delete(participant);
        FcmMessage fcmMessage = new FcmMessage();
        fcmMessage.setTarget("2");
        fcmMessageRepository.save(fcmMessage);
        fcmMessageRepository.delete(fcmMessage);
        Subscriber subscriber = new Subscriber();
        subscriber.setDeviceId("DECIVE");
        subscriberRepository.save(subscriber);
        subscriberRepository.delete(subscriber);
    }


    private MongoClientSettings buildMongoClientSettings(String clusterUrl) {
        return MongoClientSettings.builder().applyConnectionString(new ConnectionString(clusterUrl)).build();
    }

    private CodecRegistry codecRegistries() {
        return CodecRegistries.fromRegistries(
                // save uuids as UUID, instead of LUUID
                CodecRegistries.fromProviders(new UuidCodecProvider(UuidRepresentation.STANDARD)),
                MongoClientSettings.getDefaultCodecRegistry());
    }
}
