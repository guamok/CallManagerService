package es.fermax.callmanagerservice.controller.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ParticipantCallDTO {

    @ApiModelProperty(value = "Firebase App Token", required = true )
    String appToken;

    @ApiModelProperty(value = "Call Registry Type", required = true, example = "missed_call", notes = "all, auto_on, missed_call")
    String callRegistryType;

}
