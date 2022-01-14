package es.fermax.callmanagerservice.controller.dto;


import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MonitorRoomDTO {

    @ApiModelProperty(required = true , example = "ROOM_ID: ci-device-999_1638345916776")
    String id;

    @ApiModelProperty(required = true , example = "Preview")
    String status;

    @ApiModelProperty(required = true , example = "Device_B")
    String masterId;

    @ApiModelProperty(required = true , example = "23/04/2021 13:36")
    LocalDateTime start_date;

    @ApiModelProperty(required = true , example = "Call")
    String call_type;

    @ApiModelProperty(required = true , example = "{id: Peer 1, appToken: APPTokenTestFake , status: Ringing}")
    List<MonitorParticipantDTO> participants = new ArrayList<MonitorParticipantDTO>();


}
