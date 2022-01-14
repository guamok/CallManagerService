package es.fermax.callmanagerservice.repo;


import es.fermax.callmanagerservice.model.FcmMessage;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.QueryByExampleExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository("fcmMessageRepository")
public interface FcmMessageRepository
        extends MongoRepository<FcmMessage, String>, PagingAndSortingRepository<FcmMessage, String>, QueryByExampleExecutor<FcmMessage> {

    Optional<FcmMessage> findByFcmId(String fcmId);

}
