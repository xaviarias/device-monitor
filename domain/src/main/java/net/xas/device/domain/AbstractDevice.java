package net.xas.device.domain;

import java.util.Objects;

/**
 * An abstract device containing common attributes.
 */
public abstract class AbstractDevice {

    private final String id;
    private final String userId;

    protected AbstractDevice(String id, String userId) {
        Objects.requireNonNull(id, "Device id cannot be null");
        Objects.requireNonNull(userId, "UserId cannot be null");

        this.id = id;
        this.userId = userId;
    }

    public String getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

}
