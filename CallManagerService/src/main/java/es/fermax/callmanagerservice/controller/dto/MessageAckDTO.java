package es.fermax.callmanagerservice.controller.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageAckDTO {

    @ApiModelProperty(value = "fcmMessageId", required = true)
    String fcmMessageId;

    @ApiModelProperty(value = "attended", required = true)
    Boolean attended;

}
