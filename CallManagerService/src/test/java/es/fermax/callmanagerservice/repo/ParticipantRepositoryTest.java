package es.fermax.callmanagerservice.repo;

import es.fermax.callmanagerservice.model.Call;
import es.fermax.callmanagerservice.model.Participant;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;

@RunWith(SpringRunner.class)
public class ParticipantRepositoryTest {

    public static final String TOKEN_1 = "TOKEN1";
    public static final String ROOM_1 = "room-1";

    //@Autowired
    //ParticipantRepository subject;

    // @Autowired
    // CallRepository callRepository;

    @Test
    public void saveTest() {

        Call aCall = new Call();

        aCall.setRoomId("someRoomId1");
        aCall.setId("someCallId1");


        Participant participant1 = new Participant();
        participant1.setAppToken(TOKEN_1);
        participant1.setSentInvitationTime(LocalDateTime.now());

        participant1.setCallId(aCall.getId());

        Assert.assertNull(participant1.getId());

        //  subject.save(participant1);

        //Assert.assertNotNull(participant1.getId());

        // Participant savedParticipant1 = subject.findById(participant1.getId()).get();

        // Assert.assertNotNull(savedParticipant1);

    }

    @Test
    public void findByRoomIdAndTokenTest() {
        Call aCall = new Call();
        aCall.setRoomId(ROOM_1);

        Participant participant1 = new Participant();
        participant1.setAppToken(TOKEN_1);
        participant1.setSentInvitationTime(LocalDateTime.now());
        participant1.setCallId(aCall.getId());

        Assert.assertNull(participant1.getId());

        // aCall.getMobileParticipants().put(TOKEN_1, participant1);

        // Call savedCall = callRepository.save(aCall);
        // Assert.assertNotNull(savedCall);

        // Assert.assertNotNull( subject.findByAppTokenAndCallRoomId(TOKEN_1,
        // ROOM_1).get() );

    }

}
