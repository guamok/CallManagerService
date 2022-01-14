package es.fermax.callmanagerservice.repo;


import es.fermax.callmanagerservice.model.ApnMessage;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.QueryByExampleExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository("apnMessageRepository")
public interface ApnMessageRepository
        extends MongoRepository<ApnMessage, String>, PagingAndSortingRepository<ApnMessage, String>, QueryByExampleExecutor<ApnMessage> {

    Optional<ApnMessage> findById(String id);

}
