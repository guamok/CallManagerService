
ALTER  TABLE participants
    DROP COLUMN RINGING_ATTEND_TIME;

ALTER  TABLE participants
    ADD RINGING_TIME		TIMESTAMP NULL;

ALTER  TABLE participants
    ADD PREVIEW_TIME        TIMESTAMP NULL;
