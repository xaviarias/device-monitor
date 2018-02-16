package net.xas.device.domain;

import java.util.Objects;

/**
 * A device from the monitor.
 */
public class Device extends AbstractDevice {

    private DeviceState state;

    public Device(String id, String userId) {
        super(id, userId);
    }

    public DeviceState getState() {
        return state;
    }

    public void setState(DeviceState state) {
        Objects.requireNonNull(state, "State cannot be null");
        this.state = state;
    }

}
