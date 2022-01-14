package es.fermax.callmanagerservice.repo.ext;

import es.fermax.callmanagerservice.model.AppToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import javax.annotation.Nullable;

public interface AppTokenExtRepository {

    Page<AppToken> getPaginatedAndFiltered(String token, String os, String osVersion, String appVersion, String locale,
                                           Integer userId, Pageable pageable, @Nullable String question);
}
