package es.fermax.callmanagerservice.repo;

import es.fermax.callmanagerservice.model.Participant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.QueryByExampleExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository("participantsRepository")
public interface ParticipantRepository
        extends MongoRepository<Participant, String>, PagingAndSortingRepository<Participant, String>, QueryByExampleExecutor<Participant> {

    Optional<Participant> findByAppTokenAndCallId(String appToken, String callId);

    Page<Participant> findByAppTokenOrderBySentInvitationTimeDesc(String appToken, Pageable pageable);

    List<Participant> findFirst50ByAppTokenAndMissedCallTimeNotNullAndSentInvitationTimeBetweenOrderBySentInvitationTimeDesc(String appToken, LocalDateTime before, LocalDateTime today);

    List<Participant> findFirst50ByAppTokenAndSentInvitationTimeBetweenOrderBySentInvitationTimeDesc(String appToken, LocalDateTime before, LocalDateTime today);


    List<Participant> findByCallId(String callId);


}
