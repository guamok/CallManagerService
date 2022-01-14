package es.fermax.callmanagerservice.controller;

/**
 * Common constants
 */
public abstract class AController {

    protected static final String INTERNAL_SERVER_ERROR = "INTERNAL_SERVER_ERROR";
    protected static final String VALIDATION_ERROR = "VALIDATION_ERROR";
    protected static final String ROOM_ERROR = "Room not found.";
    protected static final String OK = "OK";
    protected static final String SUBSCRIBER_ERROR = "Subscriber not found";
    protected static final String ERROR = "Fermax Notification Service Error";
    protected static final String BAD_PARAMETERS = "Wrong Parameters";


    protected AController() {
    }
}