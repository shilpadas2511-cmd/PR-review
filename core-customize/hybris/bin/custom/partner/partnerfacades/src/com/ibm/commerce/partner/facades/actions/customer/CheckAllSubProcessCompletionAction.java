package com.ibm.commerce.partner.facades.actions.customer;

import com.ibm.commerce.partner.core.actions.PartnerAbstractSimpleDecisionAction;
import com.ibm.commerce.partner.core.model.PartnerStoreFrontCustomerProcessModel;
import de.hybris.platform.processengine.enums.ProcessState;
import de.hybris.platform.task.RetryLaterException;
import org.apache.commons.collections4.CollectionUtils;

/**
 * Validates if All Sub processes have been completed or not
 */
public class CheckAllSubProcessCompletionAction extends
    PartnerAbstractSimpleDecisionAction<PartnerStoreFrontCustomerProcessModel> {

    protected CheckAllSubProcessCompletionAction(final Integer maxRetryAllowed,
        final Integer retryDelay) {
        super(maxRetryAllowed, retryDelay);
    }

    @Override
    public Transition executeAction(final PartnerStoreFrontCustomerProcessModel processModel)
        throws RetryLaterException, Exception {

        if (processModel != null && CollectionUtils.isNotEmpty(processModel.getSubProcesses())
            && processModel.getSubProcesses().stream().allMatch(
            subProcess -> ProcessState.SUCCEEDED.equals(subProcess.getState())
                || ProcessState.ERROR.equals(subProcess.getState()) || ProcessState.FAILED.equals(
                subProcess.getState()))) {
            return Transition.OK;
        }
        return Transition.NOK;
    }
}
