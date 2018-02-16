package net.xas.device.workflow;

import net.xas.device.domain.Device;
import net.xas.device.domain.DeviceState;
import net.xas.device.email.MailSender;

/**
 * An abstract state with email notification capabilities.
 */
public abstract class NotifiableDeviceState implements DeviceState {

    /**
     * The sender used to notify users.
     */
    private final MailSender mailSender;

    protected NotifiableDeviceState(MailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void registerActivity(Device device) {

        String subject = String.format("Device [%s] notification", device.getId());
        String body = String.format("Device ID: %s, New state: %s", device.getId(), getName());

        this.mailSender.new Mail()
                .from("Device Monitor").to(device.getUserId())
                .subject(subject).body(body).send();
    }

}
