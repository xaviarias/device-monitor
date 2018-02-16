package net.xas.device.workflow.state;

import net.xas.device.domain.Device;
import net.xas.device.email.MailSender;
import net.xas.device.workflow.NotifiableDeviceState;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

/**
 * Active state.
 */
@Singleton
@Named("ACTIVE")
public final class Active extends NotifiableDeviceState {

    @Inject
    Active(MailSender mailSender) {
        super(mailSender);
    }

    /**
     * Only notifies if previous state is <code>null</code>.
     *
     * @param device The device to notify of.
     */
    @Override
    public void registerActivity(Device device) {
        if (device.getState() == null) {
            super.registerActivity(device);
        }
    }

}
