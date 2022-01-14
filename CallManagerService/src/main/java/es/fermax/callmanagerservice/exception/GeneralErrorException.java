package es.fermax.callmanagerservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class GeneralErrorException extends RuntimeException {

	private static final long serialVersionUID = -7329808526963275331L;
	private static final String INTERNAL_SERVER_ERROR = "INTERNAL_SERVER_ERROR";

	public GeneralErrorException() {
		super(INTERNAL_SERVER_ERROR);
	}
}