package es.fermax.callmanagerservice.repo.ext;

import es.fermax.callmanagerservice.controller.dto.SubscriberDTO;
import es.fermax.callmanagerservice.model.Subscriber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.repository.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import javax.annotation.Nullable;

@Repository
public class SubscriberExtRepositoryImpl implements SubscriberExtRepository {

    @Autowired
    MongoTemplate mongoTemplate;

    /**
     * Filters for userId and deviceId
     *
     * @param userId
     * @param deviceId
     * @param pageable
     * @param question
     * @return
     */
    @Override
    public Page<Subscriber> getPaginatedAndFiltered(Integer userId, String deviceId, Pageable pageable, @Nullable String question) {
        Query query = new Query().with(pageable);
        Criteria criteria = new Criteria();
        SubscriberDTO subscriberDTO = new SubscriberDTO();
        SubscriberDTO subscriberDTOQ = new SubscriberDTO();
        subscriberDTO.setUserId(userId);
        subscriberDTO.setDeviceId(deviceId);
        if (question == null || question.isEmpty()) {
            criteria = getSubscriberCriteria(subscriberDTO, true);
        } else {
            subscriberDTOQ.setDeviceId(deviceId);
            criteria.andOperator(getSubscriberCriteria(subscriberDTOQ, false), getSubscriberCriteria(subscriberDTO, true));
        }

        // Sonar shows issue if don't check null
        if (criteria != null) {
            query.addCriteria(criteria);
        }

        return PageableExecutionUtils.getPage(mongoTemplate.find(query, Subscriber.class), pageable,
                () -> mongoTemplate.count(query.skip(0).limit(0), Subscriber.class));
    }

    /**
     * Creates and return a Criteria.
     *
     * @param subscriberDTO
     * @param isConjuntion
     * @return
     */
    private Criteria getSubscriberCriteria(SubscriberDTO subscriberDTO, boolean isConjuntion) {
        Criteria criteria = new Criteria();
        Criteria userIdCriteria = null;
        Criteria deviceIdCriteria = null;

        if (subscriberDTO.getUserId() != null) {
            userIdCriteria = Criteria.where("userId").is(subscriberDTO.getUserId());
        }
        if (subscriberDTO.getDeviceId() != null && !subscriberDTO.getDeviceId().isEmpty()) {
            deviceIdCriteria = Criteria.where("deviceId").regex(subscriberDTO.getDeviceId());
        }

        if (userIdCriteria != null && deviceIdCriteria != null) {

            if (Boolean.TRUE.equals(isConjuntion)) {
                criteria.andOperator(userIdCriteria, deviceIdCriteria);
            } else {
                criteria.orOperator(userIdCriteria, deviceIdCriteria);
            }
        } else if (userIdCriteria != null) {
            criteria = userIdCriteria;
        } else {
            criteria = deviceIdCriteria;
        }

        return criteria;
    }
}
