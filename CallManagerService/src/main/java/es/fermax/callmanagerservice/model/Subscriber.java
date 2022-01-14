package es.fermax.callmanagerservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "#{@environment.getProperty('spring.data.mongodb.prefix')}_callmanager_subscribers")
public class Subscriber {

    @Id
    private String id;

    @NotNull
    @Field(name = "USER_ID")
    Integer userId;

    @NotNull
    @Field(name = "DEVICE_ID")
    String deviceId;
}
