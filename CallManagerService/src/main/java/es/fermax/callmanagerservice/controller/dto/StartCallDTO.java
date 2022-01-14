package es.fermax.callmanagerservice.controller.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StartCallDTO {

    @ApiModelProperty(required = true, example = "iothub.deviceid", notes = "Connected Device ID")
    String deviceId;

    @ApiModelProperty(example = "iothub.deviceid", notes = "No wifi device ID")
    String callAs;

    @ApiModelProperty(required = true, example = "auto_on, change_video, incoming_call", notes = "Type of call")
    String callType;

    @ApiModelProperty(example = "AppToken.fcmToken", notes = "FCM Token of user mobile APP")
    String callingTo;

    @ApiModelProperty(example = "{'conversationTimeout':90,'previewTimeout':30,'announcedIp':'0.0.0.0'}", notes = "Call configuration", reference = "Map")
    Map<String, Object> config = new HashMap<>();

    /**
     * Get configuration map parsed to set into call object
     *
     * @return
     */
    @ApiModelProperty(hidden = true)
    public Map<String, String> getParsedConfig() {
        Map<String, String> map = new HashMap<>();

        for (Map.Entry<String, Object> entry : config.entrySet()) {
            map.put(StringUtils.capitalize(entry.getKey()), entry.getValue().toString());
        }

        return map;
    }

}
