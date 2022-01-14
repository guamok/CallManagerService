package es.fermax.callmanagerservice.controller.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterProductionVideoDTO {

    @ApiModelProperty(value = "deviceId", required = true , example = "device1")
    String deviceId;

    @ApiModelProperty(value = "roomId", required = true , example = "device1_1625139143626")
    String roomId;
}
