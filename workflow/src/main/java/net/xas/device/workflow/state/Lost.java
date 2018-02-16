package net.xas.device.workflow.state;

import net.xas.device.email.MailSender;
import net.xas.device.workflow.NotifiableDeviceState;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

/**
 * Lost state.
 */
@Singleton
@Named("LOST")
public final class Lost extends NotifiableDeviceState {

    @Inject
    Lost(MailSender mailSender) {
        super(mailSender);
    }

}
