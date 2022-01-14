package es.fermax.callmanagerservice.repo;

import es.fermax.callmanagerservice.model.AppToken;
import es.fermax.callmanagerservice.repo.ext.AppTokenExtRepository;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.QueryByExampleExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository("appTokenRepository")
public interface AppTokenRepository extends AppTokenExtRepository, MongoRepository<AppToken, Integer>,
        PagingAndSortingRepository<AppToken, Integer>, QueryByExampleExecutor<AppToken> {

    List<AppToken> findByUserId(Integer userId);

    Optional<AppToken> findByTokenAndUserId(String token, Integer userId);

    List<AppToken> findByUserIdAndActiveTrue(Integer userId);

    List<AppToken> findByTokenAndActiveTrue(String token);
}
