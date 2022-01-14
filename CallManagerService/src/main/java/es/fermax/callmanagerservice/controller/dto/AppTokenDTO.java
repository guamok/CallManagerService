package es.fermax.callmanagerservice.controller.dto;

import es.fermax.callmanagerservice.model.AppToken;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppTokenDTO implements Serializable {

    private static final long serialVersionUID = -6332310442227376992L;

    @ApiModelProperty(value = "TOKEN", required = true)
    private String token;

    @ApiModelProperty(value = "USER_ID", example = "0")
    private Integer userId;

    @ApiModelProperty(value = "LOCALE", example = "ES")
    private String locale;

    @ApiModelProperty(value = "APP_VERSION")
    private String appVersion;

    @ApiModelProperty(value = "OS")
    private String os;

    @ApiModelProperty(value = "OS_VERSION")
    private String osVersion;

    @ApiModelProperty(value = "ACTIVE")
    private Boolean active;

    @ApiModelProperty(value = "APN")
    private Boolean apn;

    @ApiModelProperty(value = "SANDBOX")
    private Boolean sandbox;

    public AppTokenDTO(AppToken token) {
        this.token = token.getToken();
        this.userId = token.getUserId();
        this.locale = token.getLocale();
        this.appVersion = token.getAppVersion();
        this.os = token.getOs();
        this.osVersion = token.getOsVersion();
        this.active = token.getActive();
        this.apn = token.getApn();
        this.sandbox = token.getSandbox();
    }
    
}
