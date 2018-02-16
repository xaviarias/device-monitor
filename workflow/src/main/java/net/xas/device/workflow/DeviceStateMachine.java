package net.xas.device.workflow;

import net.xas.device.domain.Device;
import net.xas.device.domain.DeviceState;
import net.xas.device.domain.TrackedDevice;
import org.camunda.bpm.dmn.engine.DmnDecision;
import org.camunda.bpm.dmn.engine.DmnDecisionTableResult;
import org.camunda.bpm.dmn.engine.DmnEngine;
import org.camunda.bpm.dmn.engine.DmnEngineConfiguration;
import org.camunda.bpm.engine.variable.VariableMap;
import org.camunda.bpm.engine.variable.Variables;
import org.camunda.bpm.model.dmn.DmnModelInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * An automaton for device state management.
 */
@Singleton
class DeviceStateMachine {

    private static final Logger LOG = LoggerFactory.getLogger(DeviceStateMachine.class);

    /**
     * The DMN engine to calculate state transitions.
     */
    private final DmnEngine dmnEngine;

    /**
     * The rules for device state transitions.
     */
    private final DmnDecision stateDecision;

    /**
     * The possible states of a device.
     */
    private final Map<String, DeviceState> states;

    @Inject
    DeviceStateMachine(Instance<DeviceState> deviceStates, @DeviceMonitor DmnModelInstance dmnModel) {

        this.dmnEngine = DmnEngineConfiguration.createDefaultDmnEngineConfiguration().buildEngine();
        this.stateDecision = dmnEngine.parseDecision("stateDecision", dmnModel);

        this.states = StreamSupport.stream(deviceStates.spliterator(), false)
                .collect(Collectors.toMap(DeviceState::getName, Function.identity()));
    }

    public DeviceState registerActivity(Device currentDevice, TrackedDevice trackedDevice, Clock clock) {

        DeviceState state = currentDevice.getState();
        LocalDateTime lastActive = trackedDevice.getLastActive();

        VariableMap variables = Variables.createVariables()
                .putValue("currentState", state != null ? state.getName() : "NEW")
                .putValue("currentDate", LocalDateTime.now(clock).toString())
                .putValue("lastActive", lastActive != null ? lastActive.toString() : "");

        try {
            // Calculate new state using DMN decision table
            DmnDecisionTableResult result = dmnEngine.evaluateDecisionTable(stateDecision, variables);

            String newState = result.getSingleResult().getSingleEntry();
            LOG.info("Device [{}] transition [{} -> {}]", currentDevice.getId(),
                    state != null ? state.getName() : "", newState);

            DeviceState deviceState = this.states.get(newState);
            deviceState.registerActivity(currentDevice);

            return deviceState;

        } catch (Exception e) {
            LOG.warn(String.format("Could not manage device [%s]", currentDevice.getId()), e);
            return null;
        }
    }

}
