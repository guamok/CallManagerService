package es.fermax.callmanagerservice.service;

import es.fermax.callmanagerservice.controller.dto.AppTokenDTO;
import es.fermax.callmanagerservice.repo.AppTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AppTokenService {

    @Autowired
    AppTokenRepository appTokenRepository;

    public List<AppTokenDTO> getAppTokenDTOsByUserId(Integer userId) {
        return appTokenRepository.findByUserIdAndActiveTrue(userId).stream().map(AppTokenDTO::new).collect(Collectors.toList());
    }

    public List<AppTokenDTO> getAppTokenDTOsByToken(String token) {
        return appTokenRepository.findByTokenAndActiveTrue(token).stream().map(AppTokenDTO::new).collect(Collectors.toList());
    }

}