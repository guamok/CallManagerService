package es.fermax.callmanagerservice.repo;

import es.fermax.callmanagerservice.model.Call;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.QueryByExampleExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository("callRepository")
public interface CallRepository
        extends MongoRepository<Call, String>, PagingAndSortingRepository<Call, String>, QueryByExampleExecutor<Call> {

    Optional<Call> findByRoomId(String roomId);

    Page<Call> findByRoomIdRegex(String roomId, Pageable pageable);

    @Override
    Page<Call> findAll(Pageable pageable);
}
