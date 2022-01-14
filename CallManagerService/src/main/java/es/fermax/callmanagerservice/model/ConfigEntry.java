package es.fermax.callmanagerservice.model;

import org.springframework.data.mongodb.core.mapping.Field;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ConfigEntry {

	@Field("PROPERTY")
	private String property;

	@Field("VALUE")
	private String value;
}
