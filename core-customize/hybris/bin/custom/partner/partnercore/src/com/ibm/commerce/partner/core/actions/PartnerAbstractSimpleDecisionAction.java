package com.ibm.commerce.partner.core.actions;

import de.hybris.platform.processengine.action.AbstractSimpleDecisionAction;
import de.hybris.platform.processengine.model.BusinessProcessModel;
import de.hybris.platform.processengine.model.ProcessTaskModel;
import de.hybris.platform.task.RetryLaterException;
import java.util.Optional;

/**
 * Abstract Class for handling logic of Retry on Fail Action
 *
 * @param <T>
 */
public abstract class PartnerAbstractSimpleDecisionAction<T extends BusinessProcessModel> extends
    AbstractSimpleDecisionAction<T> {

    private final Integer maxRetryAllowed;
    private final Integer retryDelay;

    protected PartnerAbstractSimpleDecisionAction(Integer maxRetryAllowed, Integer retryDelay) {
        this.maxRetryAllowed = maxRetryAllowed;
        this.retryDelay = retryDelay;
    }

    protected AbstractSimpleDecisionAction.Transition retryOrFailAction(T processModel,
        String msg) {
        Optional<ProcessTaskModel> optionalCurrentTask = processModel.getCurrentTasks().stream()
            .findFirst();
        if (optionalCurrentTask.isPresent()
            && ((ProcessTaskModel) optionalCurrentTask.get()).getRetry()
            < this.getMaxRetryAllowed()) {
            RetryLaterException retryLaterException = new RetryLaterException(msg);
            retryLaterException.setDelay((long) this.getRetryDelay());
            throw retryLaterException;
        } else {
            return Transition.NOK;
        }
    }

    public Integer getMaxRetryAllowed() {
        return this.maxRetryAllowed;
    }

    public Integer getRetryDelay() {
        return this.retryDelay;
    }
}
