package es.fermax.callmanagerservice.exception;

public class NotFoundException extends RuntimeException {

	private static final long serialVersionUID = -7329808526963275331L;
	private static final String NOT_EXISTS = " not exists in database";

	public NotFoundException(String who) {
		super( who + NOT_EXISTS);
	}
}