package es.fermax.callmanagerservice.service;


import brave.internal.Nullable;
import es.fermax.callmanagerservice.controller.dto.SubscriberDTO;
import es.fermax.callmanagerservice.model.Subscriber;
import es.fermax.callmanagerservice.repo.SubscriberRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
public class SubscriberService {

    @Autowired
    SubscriberRepository subscriberRepository;

    private static final Logger log = LoggerFactory.getLogger(SubscriberService.class);

    public void saveList(List<SubscriberDTO> subscriberDTOList) {
        List<Subscriber> subscriberList = subscriberDTOList.stream().map(SubscriberDTO::parse).collect(Collectors.toList());
        for (Subscriber subscriber : subscriberList) {
            if (!subscriberRepository.findByDeviceIdAndUserId(subscriber.getDeviceId(), subscriber.getUserId()).isPresent()) {
                log.info("New Subscriber: {} - {}", subscriber.getDeviceId(), subscriber.getUserId());
                save(subscriber);
            }
        }
    }

    public List<Subscriber> getSubscribersByUserId(Integer userId) {
        return subscriberRepository.findByUserId(userId);
    }

    public void save(Subscriber subscriber) {
        subscriberRepository.save(subscriber);
    }

    public boolean delete(SubscriberDTO subscriberDTO) {
        Optional<Subscriber> subscriber = subscriberRepository.findByDeviceIdAndUserId(subscriberDTO.getDeviceId(),
                subscriberDTO.getUserId());
        if (subscriber.isPresent()) {
            subscriberRepository.delete(subscriber.get());
            return true;
        }
        return false;
    }

    /**
     * If callAs is present, then find the subscriber for it. The call is being
     * started by DeviceId, because it is connected device. But the User is paired
     * with the not connected device represented by CallAss
     *
     * @param deviceId - connected device
     * @param callAs   - not connected device paired with user
     * @return subscribers list
     */
    public List<SubscriberDTO> getSubscriberDTOsByDeviceId(String deviceId, String callAs) {
        List<Subscriber> subscribers;
        if (callAs != null && !callAs.trim().isEmpty()) {
            subscribers = subscriberRepository.findByUserId(Integer.valueOf(callAs));
        } else {
            subscribers = subscriberRepository.findByDeviceId(deviceId);
        }
        return subscribers.stream().map(SubscriberDTO::new).collect(Collectors.toList());
    }

    public Object getPaginatedAndFiltered(Integer userId, String deviceId, Pageable pageable, @Nullable String question) {
        return subscriberRepository.getPaginatedAndFiltered(userId, deviceId, pageable, question);
    }
}