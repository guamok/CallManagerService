package es.fermax.callmanagerservice.repo.ext;

import es.fermax.callmanagerservice.model.Subscriber;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import javax.annotation.Nullable;

public interface SubscriberExtRepository {

    public Page<Subscriber> getPaginatedAndFiltered(Integer userId, String deviceId, Pageable pageable, @Nullable String question);
}
