package net.xas.device.workflow;

import net.xas.device.domain.Device;
import net.xas.device.domain.TrackedDevice;

/**
 * Manages devices workflow.
 */
public interface DeviceWorkflowManager {

    /**
     * Manage the device's states.
     *
     * @param currentDevice The device containing the current status.
     * @param trackedDevice The device containing activity information.
     * @return A device with its status updated according to some decision rules,
     * or <code>null</code> if the device could not be managed.
     */
    Device manage(Device currentDevice, TrackedDevice trackedDevice);

}
