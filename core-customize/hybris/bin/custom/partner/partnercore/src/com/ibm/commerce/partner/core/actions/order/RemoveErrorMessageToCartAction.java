package com.ibm.commerce.partner.core.actions.order;

import com.ibm.commerce.partner.core.actions.PartnerAbstractSimpleDecisionAction;
import com.ibm.commerce.partner.core.model.IbmPartnerCartModel;
import com.ibm.commerce.partner.core.model.PriceLookUpProcessModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.task.RetryLaterException;

/**
 * This class is clear the error message in cart model
 */
public class RemoveErrorMessageToCartAction extends
    PartnerAbstractSimpleDecisionAction<PriceLookUpProcessModel> {


    protected RemoveErrorMessageToCartAction(Integer maxRetryAllowed, Integer retryDelay) {
        super(maxRetryAllowed, retryDelay);
    }

    /**
     * Executes the action to set NUll to the error message in the cart model.
     *
     * @param priceLookUpProcessModel The process model containing necessary data.
     * @return Transition.OK if successful, Transition.NOK otherwise.
     */
    @Override
    public Transition executeAction(PriceLookUpProcessModel priceLookUpProcessModel)
        throws RetryLaterException, Exception {
        AbstractOrderModel abstractOrderModel = priceLookUpProcessModel.getOrder();
        if (abstractOrderModel instanceof IbmPartnerCartModel) {
            IbmPartnerCartModel cartModel = (IbmPartnerCartModel) abstractOrderModel;
            cartModel.setErrorMesaage(null);
            cartModel.setCalculated(false);
            cartModel.setFullPriceReceived(Boolean.FALSE);
            modelService.save(cartModel);
            return Transition.OK;
        }
        return Transition.NOK;
    }
}
