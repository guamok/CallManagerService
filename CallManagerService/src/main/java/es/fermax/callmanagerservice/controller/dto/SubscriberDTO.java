package es.fermax.callmanagerservice.controller.dto;

import es.fermax.callmanagerservice.model.Subscriber;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubscriberDTO implements Serializable {

    private static final long serialVersionUID = -3920795467025564479L;

    @ApiModelProperty(value = "USER_ID", example = "0")
    Integer userId;

    @ApiModelProperty(value = "DEVICE_ID")
    String deviceId;

    public SubscriberDTO(Subscriber subscriber) {
        this.userId = subscriber.getUserId();
        this.deviceId = subscriber.getDeviceId();
    }

    public static Subscriber parse(SubscriberDTO subscriberDTO) {
        Subscriber subscriber = new Subscriber();
        subscriber.setDeviceId(subscriberDTO.getDeviceId());
        subscriber.setUserId(subscriberDTO.getUserId());
        return subscriber;
    }
}
