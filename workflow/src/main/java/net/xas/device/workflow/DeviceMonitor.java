package net.xas.device.workflow;

import javax.inject.Qualifier;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Qualifies an injection point for the Device Monitor application.
 */
@Qualifier
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface DeviceMonitor {
}
