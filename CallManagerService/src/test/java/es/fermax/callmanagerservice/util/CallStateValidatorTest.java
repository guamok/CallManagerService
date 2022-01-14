package es.fermax.callmanagerservice.util;

import es.fermax.callmanagerservice.enums.StatusEnum;
import es.fermax.callmanagerservice.model.Call;
import org.junit.Assert;
import org.junit.Test;

public class CallStateValidatorTest {

    @Test
    public void statesValidatorTest() {

        Call aCall = new Call();

        // INIT ->
        Assert.assertEquals(StatusEnum.INIT.status, CallStateValidator.validateStatusCall(aCall, "unkown"));
        Assert.assertEquals(StatusEnum.INIT.status, CallStateValidator.validateStatusCall(aCall, StatusEnum.INIT.status));
        //Assert.assertEquals(StatusEnum.INIT.status, CallStateValidator.validateStatusCall(aCall, StatusEnum.PREVIEW.status));
        Assert.assertEquals(StatusEnum.INIT.status, CallStateValidator.validateStatusCall(aCall, StatusEnum.MISSED.status));
        Assert.assertEquals(StatusEnum.INIT.status, CallStateValidator.validateStatusCall(aCall, StatusEnum.CONVERSATION.status));
        Assert.assertEquals(StatusEnum.FINISHED.status, CallStateValidator.validateStatusCall(aCall, StatusEnum.FINISHED.status));
        Assert.assertEquals(StatusEnum.RING.status, CallStateValidator.validateStatusCall(aCall, StatusEnum.RING.status));
        Assert.assertEquals(StatusEnum.PREVIEW.status, CallStateValidator.validateStatusCall(aCall, StatusEnum.PREVIEW.status));
    }

    @Test
    public void state_RINGING_ValidatorTest() {
        Call aCall = new Call();
        // RING
        aCall.setStatus(StatusEnum.RING.status);
        Assert.assertEquals(StatusEnum.RING.status, CallStateValidator.validateStatusCall(aCall, StatusEnum.INIT.status));
        Assert.assertEquals(StatusEnum.RING.status, CallStateValidator.validateStatusCall(aCall, StatusEnum.RING.status));
        Assert.assertEquals(StatusEnum.MISSED.status, CallStateValidator.validateStatusCall(aCall, StatusEnum.MISSED.status));
        Assert.assertEquals(StatusEnum.PREVIEW.status, CallStateValidator.validateStatusCall(aCall, StatusEnum.PREVIEW.status));
        Assert.assertEquals(StatusEnum.FINISHED.status, CallStateValidator.validateStatusCall(aCall, StatusEnum.FINISHED.status));
        Assert.assertEquals(StatusEnum.ERROR.status, CallStateValidator.validateStatusCall(aCall, StatusEnum.ERROR.status));
    }

    @Test
    public void state_PREVIEW_ValidatorTest() {
        Call aCall = new Call();

        aCall.setStatus(StatusEnum.RING.status);

        // PREVIEW ->
        aCall.setStatus(StatusEnum.PREVIEW.status);
        Assert.assertEquals(StatusEnum.PREVIEW.status, CallStateValidator.validateStatusCall(aCall, StatusEnum.PREVIEW.status));
        Assert.assertEquals(StatusEnum.CONVERSATION.status, CallStateValidator.validateStatusCall(aCall, StatusEnum.CONVERSATION.status));
        Assert.assertEquals(StatusEnum.MISSED.status, CallStateValidator.validateStatusCall(aCall, StatusEnum.MISSED.status));
        Assert.assertEquals(StatusEnum.ERROR.status, CallStateValidator.validateStatusCall(aCall, StatusEnum.ERROR.status));
        Assert.assertEquals(StatusEnum.PREVIEW.status, CallStateValidator.validateStatusCall(aCall, StatusEnum.RING.status));
        Assert.assertEquals(StatusEnum.PREVIEW.status, CallStateValidator.validateStatusCall(aCall, StatusEnum.INIT.status));

    }

    @Test
    public void state_CONVERSATION_ValidatorTest() {
        Call aCall = new Call();

        aCall.setStatus(StatusEnum.RING.status);
        aCall.setStatus(StatusEnum.PREVIEW.status);

        // CONVERSATION ->
        aCall.setStatus(StatusEnum.CONVERSATION.status);
        Assert.assertEquals(StatusEnum.CONVERSATION.status, CallStateValidator.validateStatusCall(aCall, StatusEnum.CONVERSATION.status));
        Assert.assertEquals(StatusEnum.CONVERSATION.status, CallStateValidator.validateStatusCall(aCall, StatusEnum.PREVIEW.status));
        Assert.assertEquals(StatusEnum.CONVERSATION.status, CallStateValidator.validateStatusCall(aCall, StatusEnum.MISSED.status));
        Assert.assertEquals(StatusEnum.CONVERSATION.status, CallStateValidator.validateStatusCall(aCall, StatusEnum.RING.status));
        Assert.assertEquals(StatusEnum.FINISHED.status, CallStateValidator.validateStatusCall(aCall, StatusEnum.FINISHED.status));
        Assert.assertEquals(StatusEnum.ERROR.status, CallStateValidator.validateStatusCall(aCall, StatusEnum.ERROR.status));
    }

    @Test
    public void state_FINISHED_ValidatorTest() {
        Call aCall = new Call();

        aCall.setStatus(StatusEnum.RING.status);
        aCall.setStatus(StatusEnum.PREVIEW.status);
        aCall.setStatus(StatusEnum.CONVERSATION.status);

        // FINISHED ->
        aCall.setStatus(StatusEnum.FINISHED.status);
        Assert.assertEquals(StatusEnum.FINISHED.status, CallStateValidator.validateStatusCall(aCall, StatusEnum.CONVERSATION.status));
        Assert.assertEquals(StatusEnum.FINISHED.status, CallStateValidator.validateStatusCall(aCall, StatusEnum.FINISHED.status));
        Assert.assertEquals(StatusEnum.FINISHED.status, CallStateValidator.validateStatusCall(aCall, StatusEnum.PREVIEW.status));
        Assert.assertEquals(StatusEnum.FINISHED.status, CallStateValidator.validateStatusCall(aCall, StatusEnum.MISSED.status));
        Assert.assertEquals(StatusEnum.FINISHED.status, CallStateValidator.validateStatusCall(aCall, StatusEnum.RING.status));
        Assert.assertEquals(StatusEnum.FINISHED.status, CallStateValidator.validateStatusCall(aCall, StatusEnum.FINISHED.status));
        Assert.assertEquals(StatusEnum.FINISHED.status, CallStateValidator.validateStatusCall(aCall, StatusEnum.ERROR.status));
        Assert.assertEquals(StatusEnum.FINISHED.status, CallStateValidator.validateStatusCall(aCall, StatusEnum.INIT.status));

    }

    @Test
    public void state_MISSED_ValidatorTest() {
        Call missedCall = new Call();

        missedCall.setStatus(StatusEnum.RING.status);
        missedCall.setStatus(StatusEnum.MISSED.status);

        // MISSED
        Assert.assertEquals(StatusEnum.MISSED.status, CallStateValidator.validateStatusCall(missedCall, StatusEnum.MISSED.status));
        Assert.assertEquals(StatusEnum.MISSED.status, CallStateValidator.validateStatusCall(missedCall, StatusEnum.CONVERSATION.status));
        Assert.assertEquals(StatusEnum.MISSED.status, CallStateValidator.validateStatusCall(missedCall, StatusEnum.PREVIEW.status));
        Assert.assertEquals(StatusEnum.MISSED.status, CallStateValidator.validateStatusCall(missedCall, StatusEnum.RING.status));
        Assert.assertEquals(StatusEnum.MISSED.status, CallStateValidator.validateStatusCall(missedCall, StatusEnum.FINISHED.status));
        Assert.assertEquals(StatusEnum.MISSED.status, CallStateValidator.validateStatusCall(missedCall, StatusEnum.ERROR.status));
        Assert.assertEquals(StatusEnum.MISSED.status, CallStateValidator.validateStatusCall(missedCall, StatusEnum.INIT.status));

    }

    @Test
    public void state_ERROR_ValidatorTest() {
        Call errorCall = new Call();
        errorCall.setStatus(StatusEnum.RING.status);
        errorCall.setStatus(StatusEnum.ERROR.status);

        // ERROR
        Assert.assertEquals(StatusEnum.ERROR.status, CallStateValidator.validateStatusCall(errorCall, StatusEnum.ERROR.status));
        Assert.assertEquals(StatusEnum.ERROR.status, CallStateValidator.validateStatusCall(errorCall, StatusEnum.MISSED.status));
        Assert.assertEquals(StatusEnum.ERROR.status, CallStateValidator.validateStatusCall(errorCall, StatusEnum.CONVERSATION.status));
        Assert.assertEquals(StatusEnum.ERROR.status, CallStateValidator.validateStatusCall(errorCall, StatusEnum.PREVIEW.status));
        Assert.assertEquals(StatusEnum.ERROR.status, CallStateValidator.validateStatusCall(errorCall, StatusEnum.RING.status));
        Assert.assertEquals(StatusEnum.ERROR.status, CallStateValidator.validateStatusCall(errorCall, StatusEnum.FINISHED.status));
        Assert.assertEquals(StatusEnum.ERROR.status, CallStateValidator.validateStatusCall(errorCall, StatusEnum.INIT.status));

    }
}
