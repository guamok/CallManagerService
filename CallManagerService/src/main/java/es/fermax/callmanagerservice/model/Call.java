package es.fermax.callmanagerservice.model;

import es.fermax.callmanagerservice.enums.StatusEnum;
import es.fermax.callmanagerservice.util.CallStateValidator;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.index.IndexDirection;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "#{@environment.getProperty('spring.data.mongodb.prefix')}_callmanager_calls")
@TypeAlias("call")
public class Call {

    @Id
    private String id;

    @Indexed(unique = true)
    @Field("ROOM_ID")
    private String roomId;

    @Field("INIT_DEVICE_ID")
    @Indexed(name = "idx_init_device_id", direction = IndexDirection.ASCENDING)
    private String initDeviceId;

    @Field("NO_CONN_DEVICE_ID")
    private String callAs;

    @Field("INIT_DEVICE_START_PRODUCE")
    private LocalDateTime initDeviceStartProduceTime;

    @Field("STATUS")
    @Indexed(name = "idx_status", direction = IndexDirection.ASCENDING)
    private String status = StatusEnum.INIT.status;

    @Field("CALL_TYPE")
    @Indexed(name = "idx_call_type", direction = IndexDirection.ASCENDING)
    private String callType;

    @Field("ERROR_CODE")
    private String errorCode;

    @Field("ERROR_REASON")
    private String errorReason;

    @Field("ENTER_RINGING_TIME")
    private LocalDateTime enterRingingTime;

    @Field("ENTER_PREVIEW_TIME")
    private LocalDateTime enterPreviewTime;

    @Field("ENTER_CONVERSATION_TIME")
    private LocalDateTime enterConversationTime;

    @Field("ENTER_MISSED_TIME")
    private LocalDateTime missedCallTime;

    @Field("ENTER_FINISHED_TIME")
    private LocalDateTime finishedTime;

    @Field("IS_AUTOON")
    private boolean isAutoon = false;

    @Field("IS_MANAGED")
    private boolean isManaged = false;

    @Field("CALLING_TO")
    private String callingTo;

    @CreatedDate
    @Field("CREATION_DATE")
    @Indexed(name = "idx_creation_date", direction = IndexDirection.ASCENDING)
    private LocalDateTime creationDate;

    @LastModifiedDate
    @Field("UPDATE_DATE")
    private LocalDateTime updateDate;

    @Field("CONFIG_ENTRY")
    private Map<String, String> config = new HashMap<>();

    public void setStatus(String status) {
        this.status = CallStateValidator.validateStatusCall(this, status);
    }

    @Override
    public String toString() {
        return "Call [id=" + id + ", roomId=" + roomId + ", initDeviceId=" + initDeviceId + ", callAs=" + callAs + ", status=" + status
                + ", callType=" + callType + ", errorCode=" + errorCode + ", errorReason=" + errorReason + ", enterRingingTime="
                + enterRingingTime + ", enterPreviewTime=" + enterPreviewTime + ", enterConversationTime=" + enterConversationTime
                + ", missedCallTime=" + missedCallTime + ", finishedTime=" + finishedTime + ", isAutoon=" + isAutoon + ", isManaged="
                + isManaged + ", creationDate=" + creationDate + ", updateDate=" + updateDate + "]";
    }
}
