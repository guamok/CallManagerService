package es.fermax.callmanagerservice.model;

import es.fermax.callmanagerservice.enums.NotificationStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "#{@environment.getProperty('spring.data.mongodb.prefix')}_callmanager_apn_messages")
public class ApnMessage {

    @Id
    private String id;

    @Field(name = "NOTIFICATION")
    private Notification notification;

    @Field(name = "ROOM_ID")
    private String roomId;

    @Field(name = "DEVICE_ID")
    private String deviceId;

    @Field(name = "CALL_AS")
    private String callAs;

    @NotNull
    @Builder.Default
    @Field(name = "DELIVERY_STATUS")
    private String deliveryStatus = NotificationStatusEnum.DRAFT.status;

    @NotNull
    @Field(name = "TARGET_VALUE")
    private String targetValue;

    @CreatedDate
    @Field(name = "CREATION_DATE")
    private Date creationDate;

    @LastModifiedDate
    @Field(name = "UPDATE_DATE")
    private Date updateDate;
}
