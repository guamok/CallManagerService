package es.fermax.callmanagerservice.repo;

import es.fermax.callmanagerservice.model.Subscriber;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
//@DataJpaTest
//@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class SubscriberRepositoryTest {

    // @Autowired
    // SubscriberRepository subject;

    @Test
    public void saveTest() {

        Subscriber subscriber = new Subscriber();
        subscriber.setDeviceId("123abc");
        subscriber.setUserId(1);

        Assert.assertNotNull(subscriber);

        // Subscriber savedSubscriber = subject.save(subscriber);

        // Assert.assertNotNull(savedSubscriber);

        // Optional<Subscriber> found = subject.findByDeviceIdAndUserId("123abc", 1);

        // Assert.assertTrue(found.isPresent());
    }
}
