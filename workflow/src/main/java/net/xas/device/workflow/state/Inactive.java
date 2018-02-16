package net.xas.device.workflow.state;

import net.xas.device.email.MailSender;
import net.xas.device.workflow.NotifiableDeviceState;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

/**
 * Inactive state.
 */
@Singleton
@Named("INACTIVE")
public final class Inactive extends NotifiableDeviceState {

    @Inject
    Inactive(MailSender mailSender) {
        super(mailSender);
    }

}
