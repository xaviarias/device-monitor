package net.xas.device.domain;

import javax.inject.Named;

/**
 * An state that can register device activity.
 */
public interface DeviceState {

    default String getName() {
        return getClass().getAnnotation(Named.class).value();
    }

    void registerActivity(Device device);

}
