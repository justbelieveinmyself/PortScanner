package com.justbelieveinmyself.portscanner.core.state;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Logger;

/**
 * Хранит текущее состояние и производит переходы с соответствующими методами
 */
public class StateMachine {
    private final Logger LOG = Logger.getLogger(StateMachine.class.getSimpleName());

    public enum Transition {INIT, START, STOP, NEXT, COMPLETE, RESET, RESCAN, CONTINUE}

    private volatile ScanningState state = ScanningState.IDLE;

    private ReentrantReadWriteLock listenersLock = new ReentrantReadWriteLock();
    private List<StateTransitionListener> transitionListeners = new ArrayList<>();

    /**
     * Регистрирует нового слушателя переходов
     */
    public void addTransitionListener(StateTransitionListener listener) {
        try {
            listenersLock.writeLock().lock();
            transitionListeners.add(listener);
        } finally {
            listenersLock.writeLock().unlock();
        }
    }

    /**
     * Удаляет слушателя переходов
     */
    public void removeTransitionListener(StateTransitionListener listener) {
        try {
            listenersLock.writeLock().lock();
            transitionListeners.remove(listener);
        } finally {
            listenersLock.writeLock().unlock();
        }
    }

    /**
     * Переход к определенному состоянию, уведомляет всех слушателей
     */
    void transitionTo(ScanningState newState, Transition transition) {
        if (state != newState) {
            LOG.info("Новое состояние: " + newState);
            state = newState;
            notifyAboutTransition(transition);
        }
    }

    protected void notifyAboutTransition(Transition transition) {
        try {
            listenersLock.readLock().lock();
            for (StateTransitionListener listener : transitionListeners) {
                listener.transitionTo(state, transition);
            }
        } finally {
            listenersLock.readLock().unlock();
        }
    }

    /**
     * Переход к следующему состоянию в последовательности состояний
     */
    public void transitionToNext() {
        // Состояние KILLING не может быть переключено вручную
        if (state != ScanningState.KILLING) {
            transitionTo(state.next(), Transition.NEXT);
        }
    }

    /**
     * Переход к остановленному состоянию
     */
    public void stop() {
        if (state == ScanningState.SCANNING) {
            transitionTo(ScanningState.STOPPING, Transition.STOP);
        } else if (state == ScanningState.STOPPING) {
            // уведомить ещё раз
            notifyAboutTransition(Transition.STOP);
        } else {
            throw new IllegalStateException("Попытка остановки из состояния " + state);
        }
    }

    /**
     * Переход к состоянию бездействия
     */
    public void complete() {
        if (state == ScanningState.STOPPING || state == ScanningState.KILLING) {
            transitionTo(ScanningState.IDLE, Transition.COMPLETE);
        } else {
            throw new IllegalStateException("Попытка завершения из состояния " + state);
        }
    }

    /**
     * Переход к состоянию пересканирования
     */
    public void rescan() {
        if (state == ScanningState.IDLE) {
            transitionTo(ScanningState.RESTARTING, Transition.RESCAN);
        } else {
            throw new IllegalStateException("Попытка пересканирования из состояния: " + state);
        }
    }

    /**
     * Запускает сканирование
     */
    public void startScanning() {
        if (state == ScanningState.STARTING || state == ScanningState.RESTARTING) {
            transitionTo(ScanningState.SCANNING, Transition.START);
        }
    }

    /**
     * Продолжает сканирование
     */
    public void continueScanning() {
        if (state == ScanningState.IDLE) {
            transitionTo(ScanningState.STARTING, Transition.CONTINUE);
        } else {
            throw new IllegalStateException("Попытка продолжить сканирование из состояния " + state);
        }
    }

    /**
     * Инициализирует всех при запуске
     */
    public void init() {
        state = ScanningState.IDLE;
        notifyAboutTransition(Transition.INIT);
    }

    /**
     * Сбрасывает состояние машины к начальному
     */
    public void reset() {
        state = ScanningState.IDLE;
    }

    /**
     * @return true, если текущее состояние соответствует переданному
     */
    public boolean inState(ScanningState state) {
        return this.state == state;
    }

    public ScanningState getState() {
        return state;
    }

}
