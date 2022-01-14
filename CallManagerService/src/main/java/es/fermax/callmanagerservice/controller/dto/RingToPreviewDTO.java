package es.fermax.callmanagerservice.controller.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RingToPreviewDTO {

    @ApiModelProperty(value = "appToken", required = true , example = "firebaseToken1")
    String appToken;

    @ApiModelProperty(value = "roomId", required = true , example = "room1_1625139143626")
    String roomId;
}
