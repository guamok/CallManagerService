package es.fermax.callmanagerservice.repo;

import es.fermax.callmanagerservice.model.Subscriber;
import es.fermax.callmanagerservice.repo.ext.SubscriberExtRepository;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.QueryByExampleExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository("subscriberRepository")
public interface SubscriberRepository extends SubscriberExtRepository, MongoRepository<Subscriber, String>,
         QueryByExampleExecutor<Subscriber> {

    List<Subscriber> findByDeviceId(String deviceId);

    List<Subscriber> findByUserId(Integer userId);

    Optional<Subscriber> findByDeviceIdAndUserId(String deviceId, Integer userId);

}
