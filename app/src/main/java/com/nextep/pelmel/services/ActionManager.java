package com.nextep.pelmel.services;

import com.nextep.pelmel.model.Action;

/**
 * The action manager is a central entry point for executing application global actions
 *
 * Created by cfondacci on 29/07/15.
 */
public interface ActionManager {

    interface ActionCallback {
        /**
         * Notifies that the action has completed
         */
        void actionCompleted(boolean isSucess, Object result);
    }

    /**
     * The interface implemented by an action
     */
    interface ActionCommand {
        /**
         * Executes this command
         * @param parameter the parameter of the action execution
         * @return the result of the action
         */
        Object execute(Object parameter);
    }
    /**
     * Executes the given action with the given parameter
     *
     * @param action the action to execute
     * @param parameter the argument of the action
     */
    void executeAction(Action action, Object parameter);

    /**
     * Same as ActionManager#executeAction with a callback argument
     *
     * @param action action to execute
     * @param parameter parameter of action execution
     * @param callback callback notified when done
     */
    void executeAction(Action action, Object parameter,ActionCallback callback);
}
