package es.fermax.callmanagerservice.repo;

import es.fermax.callmanagerservice.model.Call;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;

@RunWith(SpringRunner.class)
//@DataJpaTest
//@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class CallRepositoryTest {

    public static final String ROOM_1 = "someRoomId1";
    public static final String TOKEN_1 = "TOKEN1";
    public static final String TOKEN_2 = "TOKEN2";

    // @Autowired
    // CallRepository subject;

    @Test
    public void saveWithoutParticipantsTest() {

        Call aCall = new Call();
        aCall.setRoomId(ROOM_1);
        aCall.setCreationDate(LocalDateTime.now());
        aCall.setInitDeviceStartProduceTime(LocalDateTime.now());

        Assert.assertNull(aCall.getId());

        // subject.save(aCall);

        // Assert.assertNotNull(aCall.getId());

        // Optional<Call> savedCall = subject.findByRoomId(ROOM_1);

        // Assert.assertEquals(StatusEnum.INIT.status, savedCall.get().getStatus());

        // Optional<Call> byRoomId = subject.findByRoomId(ROOM_1);

        // Assert.assertNotNull(byRoomId);
        // Assert.assertEquals(ROOM_1, byRoomId.get().getRoomId());
        // Assert.assertNotNull(byRoomId.get().getMobileParticipants());
        // Assert.assertTrue(byRoomId.get().getMobileParticipants().isEmpty());
    }

//    @Test
//    public void saveWithParticipantsTest() {
//
//        Call aCall = new Call();
//        aCall.setRoomId(ROOM_1);
//
//        Participant participant1 = new Participant();
//        participant1.setAppToken(TOKEN_1);
//        participant1.setSentInvitationTime(LocalDateTime.now());
//        aCall.getMobileParticipants().add(participant1);
//
//        Participant participant2 = new Participant();
//        participant2.setAppToken(TOKEN_2);
//        participant2.setSentInvitationTime(LocalDateTime.now());
//        aCall.getMobileParticipants().add(participant2);
//
//        Assert.assertNull(aCall.getId());
//
//        // subject.save(aCall);
//
//        // Assert.assertNotNull(aCall.getId());
//
//        // Optional<Call> savedCall = subject.findByRoomId(ROOM_1);
//
//        // Assert.assertEquals(StatusEnum.INIT.status, savedCall.get().getStatus());
//        // Assert.assertEquals(2, savedCall.get().getMobileParticipants().size());
//
//        // Participant savedParticipant1 =
//        // savedCall.get().getMobileParticipants().get(0);
//        // Assert.assertNotNull(savedParticipant1.getSentInvitationTime());
//    }
}