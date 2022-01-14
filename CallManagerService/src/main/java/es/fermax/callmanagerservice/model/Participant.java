package es.fermax.callmanagerservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "#{@environment.getProperty('spring.data.mongodb.prefix')}_callmanager_participants")
public class Participant {

    @Id
    private String id;

    @Field("APP_TOKEN")
    private String appToken;

    @Field("SENT_INVITATION_TIME")
    private LocalDateTime sentInvitationTime;

    @Field("RINGING_TIME")
    private LocalDateTime ringingTime;

    @Field("PREVIEW_TIME")
    private LocalDateTime previewTime;

    @Field("REJECT_TIME")
    private LocalDateTime rejectTime;

    @Field("PICKUP_TIME")
    private LocalDateTime pickupTime;

    @Field("MISSED_CALL_TIME")
    private LocalDateTime missedCallTime;

    @Field("HANGUP_TIME")
    private LocalDateTime hangupTime;

    @Field("MANAGE_REQUEST_TIME")
    private LocalDateTime manageRequestTime;

    @CreatedDate
    @Field("CREATION_DATE")
    private LocalDateTime creationDate;

    @LastModifiedDate
    @Field("UPDATE_DATE")
    private LocalDateTime updateDate;

    @Field("CALL_ID")
    private String callId;
}
