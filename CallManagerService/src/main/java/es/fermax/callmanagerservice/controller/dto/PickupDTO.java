package es.fermax.callmanagerservice.controller.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PickupDTO {

    @ApiModelProperty(value = "APPTOKEN", required = true )
    String appToken;

    @ApiModelProperty(value = "ROOM_ID", required = true , example = "room1_1625139143626")
    String roomId;
}
