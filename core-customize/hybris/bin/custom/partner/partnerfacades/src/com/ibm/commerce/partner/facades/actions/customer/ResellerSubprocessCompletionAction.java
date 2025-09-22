package com.ibm.commerce.partner.facades.actions.customer;

import com.ibm.commerce.partner.core.actions.PartnerAbstractSimpleDecisionAction;
import com.ibm.commerce.partner.core.constants.PartnercoreConstants;
import com.ibm.commerce.partner.core.model.PartnerResellerSiteBusinessProcessModel;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.task.RetryLaterException;
import org.apache.log4j.Logger;

/**
 * Persiste Reseller Details
 */
public class ResellerSubprocessCompletionAction extends
    PartnerAbstractSimpleDecisionAction<PartnerResellerSiteBusinessProcessModel> {

    private static final Logger LOG = Logger.getLogger(ResellerSubprocessCompletionAction.class);
    private final BusinessProcessService businessProcessService;

    protected ResellerSubprocessCompletionAction(final Integer maxRetryAllowed,
        final Integer retryDelay, final BusinessProcessService businessProcessService) {
        super(maxRetryAllowed, retryDelay);
        this.businessProcessService = businessProcessService;
    }

    @Override

    public Transition executeAction(final PartnerResellerSiteBusinessProcessModel processModel)
        throws RetryLaterException, Exception {

        getBusinessProcessService().triggerEvent(processModel.getParentProcess().getCode() + PartnercoreConstants.UNDERSCORE
                + PartnercoreConstants.RESELLER_SUBPROCESS_COMPLETED_EVENT);
        if (processModel != null && processModel.getUnit() != null) {
            return Transition.OK;
        }
        LOG.error(
            String.format("Error occurred in the reseller business process [%s] ", processModel.getCode()));
        return Transition.NOK;
    }

    public BusinessProcessService getBusinessProcessService() {
        return businessProcessService;
    }
}
