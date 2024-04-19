package com.justbelieveinmyself.portscanner.core.state;

import com.justbelieveinmyself.portscanner.core.state.StateMachine.Transition;

public interface StateTransitionListener {

    /**
     * Уведомляет о переходе к определенному состоянию
     */
    void transitionTo(ScanningState state, Transition transition);
}
