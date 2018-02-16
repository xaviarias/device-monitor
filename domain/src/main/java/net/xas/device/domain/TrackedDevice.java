package net.xas.device.domain;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * A device containing tracking data.
 */
public class TrackedDevice extends AbstractDevice {

    private LocalDateTime lastActive;

    public TrackedDevice(String id, String userId, LocalDateTime lastActive) {
        super(id, userId);
        setLastActive(lastActive);
    }

    /**
     * Returns the last activity for this device.
     *
     * @return the last registered user activity.
     */
    public LocalDateTime getLastActive() {
        return lastActive;
    }

    /**
     * Sets the last activity for this device.
     *
     * @param lastActive a non-null last registered user activity.
     */
    public void setLastActive(LocalDateTime lastActive) {
        Objects.requireNonNull(lastActive, "Last active cannot be null");
        this.lastActive = lastActive;
    }

}
