package es.fermax.callmanagerservice.controller.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MonitorParticipantDTO {

    @ApiModelProperty(required = true , example = "Peer 1")
    String id;

    @ApiModelProperty(required = true , example = "APPTokenTestFake")
    String appToken;

    @ApiModelProperty(required = true , example = "Ringing")
    String status;

    @ApiModelProperty(required = true , example = "True")
    Boolean web;

}
