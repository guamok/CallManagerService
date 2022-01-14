package es.fermax.callmanagerservice.model;

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
@NoArgsConstructor
@Document(collection = "#{@environment.getProperty('spring.data.mongodb.prefix')}_notification_app_tokens")
public class AppToken {

    @Id
    private String id;

    @NotNull
    @Field(name = "TOKEN")
    private String token;

    @NotNull
    @Field(name = "USER_ID")
    private Integer userId;

    @NotNull
    @Field(name = "LOCALE")
    private String locale = "ES";

    @Field(name = "APP_VERSION")
    private String appVersion;

    @Field(name = "OS")
    private String os;

    @Field(name = "OS_VERSION")
    private String osVersion;

    @CreatedDate
    @Field(name = "CREATION_DATE")
    private Date creationDate;

    @LastModifiedDate
    @Field(name = "UPDATE_DATE")
    private Date updateDate;

    @NotNull
    @Field(name = "ACTIVE")
    private Boolean active = Boolean.TRUE;

    @Field(name = "APN")
    private Boolean apn;

    @Field(name = "SANDBOX")
    private Boolean sandbox;

    public AppToken(Integer userId, String token) {
        this.userId = userId;
        this.token = token;
    }
}
