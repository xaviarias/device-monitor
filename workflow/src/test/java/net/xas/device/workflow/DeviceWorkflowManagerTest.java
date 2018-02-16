package net.xas.device.workflow;

import net.xas.device.domain.Device;
import net.xas.device.domain.DeviceState;
import net.xas.device.domain.TrackedDevice;
import net.xas.device.email.MailSender;
import net.xas.device.workflow.state.Active;
import net.xas.device.workflow.state.Inactive;
import net.xas.device.workflow.state.Lost;
import org.assertj.core.api.Assertions;
import org.camunda.bpm.model.dmn.Dmn;
import org.camunda.bpm.model.dmn.DmnModelInstance;
import org.jglue.cdiunit.AdditionalClasses;
import org.jglue.cdiunit.CdiRunner;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;

import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.io.InputStream;
import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Workflow manager tests.
 */
@RunWith(CdiRunner.class)
@AdditionalClasses({
        DeviceStateMachine.class,
        Active.class, Inactive.class, Lost.class
})
public class DeviceWorkflowManagerTest {

    private static final String RULES_FILE = "device-rules.dmn";

    @Inject
    private DeviceWorkflowManagerImpl workflowManager;

    @Mock
    @Produces
    private MailSender mailSender;

    @Inject
    @Named("ACTIVE")
    private DeviceState active;

    @Inject
    @Named("INACTIVE")
    private DeviceState inactive;

    @Inject
    @Named("LOST")
    private DeviceState lost;

    @BeforeClass
    public static void setUpClass() throws Exception {
        System.setProperty("org.jboss.logging.provider", "slf4j");
    }

    /**
     * Tests all the possible paths in the state automaton.
     * <p/>
     * Also checks for email notification.
     */
    @Test
    public void testWorkflow() {

        Clock clock = workflowManager.getClock();

        String deviceId = UUID.randomUUID().toString();
        String userId = UUID.randomUUID().toString();

        Device device = new Device(deviceId, userId);
        TrackedDevice trackedDevice = new TrackedDevice(
                deviceId, userId, LocalDateTime.now(clock));

        // -> ACTIVE
        device = assertStateAfter(0, active, true, device, trackedDevice);

        // ACTIVE -> ACTIVE
        device = assertStateAfter(3, active, false, device, trackedDevice);

        // ACTIVE -> INACTIVE
        device = assertStateAfter(8, inactive, true, device, trackedDevice);

        // INACTIVE -> ACTIVE
        device = assertStateAfter(4, active, false, device, trackedDevice);

        // ACTIVE -> INACTIVE
        device = assertStateAfter(10, inactive, true, device, trackedDevice);

        // INACTIVE -> LOST
        device = assertStateAfter(11, lost, true, device, trackedDevice);

        // LOST -> ACTIVE
        assertStateAfter(6, active, false, device, trackedDevice);
    }

    @Produces
    @Singleton
    @DeviceMonitor
    private DmnModelInstance loadDecisionRules() {
        InputStream rulesData = getClass().getClassLoader().getResourceAsStream(RULES_FILE);
        return Dmn.readModelFromStream(rulesData);
    }

    /**
     * Given a number of days, a state, a device and a tracked device,
     * registers activity in the tracked device, manages the devices and asserts that the
     * returning device state has changed after the number of days.
     * <p/>
     * If the notification flag is <code>true</code>, also verifies that the mail sender
     * has been called to send an email.
     *
     * @param numDays       The number of days to let pass.
     * @param state         The expected state after managing the device.
     * @param notify        Whether if the client should have been notified.
     * @param device        The device to manage.
     * @param trackedDevice The tracked device to manage.
     * @return The managed device.
     */
    private Device assertStateAfter(int numDays, DeviceState state, boolean notify,
                                    Device device, TrackedDevice trackedDevice) {

        Clock clock = workflowManager.getClock();
        trackedDevice.setLastActive(LocalDateTime.now(clock));

        clock = Clock.offset(clock, Duration.ofDays(numDays));
        workflowManager.setClock(clock);

        device = workflowManager.manage(device, trackedDevice);
        Assertions.assertThat(device.getState()).isEqualTo(state);

        if (notify) {
            Mockito.verify(mailSender).send(Mockito.any());
            Mockito.reset(mailSender);
        }

        return device;
    }

}
