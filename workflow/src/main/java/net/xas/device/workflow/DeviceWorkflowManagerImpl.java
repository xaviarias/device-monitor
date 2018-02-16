package net.xas.device.workflow;

import net.xas.device.domain.Device;
import net.xas.device.domain.DeviceState;
import net.xas.device.domain.TrackedDevice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.time.Clock;
import java.util.Objects;

/**
 * {@link DeviceWorkflowManager} implementation using DMN decision tables.
 */
@Singleton
class DeviceWorkflowManagerImpl implements DeviceWorkflowManager {

    private static final Logger LOG = LoggerFactory.getLogger(DeviceWorkflowManagerImpl.class);

    /**
     * The automaton for device states.
     */
    private final DeviceStateMachine stateMachine;

    /**
     * The clock to provide date and time facilities, giving the option to use alternate clocks.
     */
    private Clock clock = Clock.systemDefaultZone();

    @Inject
    DeviceWorkflowManagerImpl(DeviceStateMachine stateMachine, Instance<Clock> clock) {
        this.stateMachine = stateMachine;

        if (!clock.isUnsatisfied()) {
            this.clock = clock.get();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Device manage(Device currentDevice, TrackedDevice trackedDevice) {
        Objects.requireNonNull(currentDevice, "Current device cannot be null");
        Objects.requireNonNull(trackedDevice, "Tracked device cannot be null");

        DeviceState currentState = currentDevice.getState();

        LOG.info("Managing device [{}] with state [{}] and last active on [{}]",
                currentDevice.getId(), currentState != null ? currentState.getName() : "",
                trackedDevice.getLastActive());

        try {
            DeviceState newState = stateMachine.registerActivity(currentDevice, trackedDevice, clock);

            if (newState != null) {
                LOG.info("New state for device [{}] is [{}]", currentDevice.getId(), newState.getName());

                Device device = new Device(currentDevice.getId(), currentDevice.getUserId());
                device.setState(newState);

                return device;
            }
        } catch (RuntimeException e) {
            LOG.error("Unexpected exception: " + e.getMessage(), e);
        }

        LOG.info("Could no manage device [{}]", currentDevice.getId());
        return null;
    }

    /**
     * Returns the clock used for state management.
     *
     * @return The internal clock instance.
     */
    public Clock getClock() {
        return clock;
    }

    /**
     * Sets the clock used for state management.
     *
     * @param clock A non-null clock instance.
     */
    public void setClock(Clock clock) {
        Objects.requireNonNull(clock, "Clock cannot be null");
        this.clock = clock;
    }


}
