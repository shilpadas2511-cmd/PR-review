package com.ibm.commerce.partner.core.actions.order;

import com.ibm.commerce.partner.core.actions.PartnerAbstractSimpleDecisionAction;
import com.ibm.commerce.partner.core.model.IbmPartnerCartModel;
import com.ibm.commerce.partner.core.model.PriceLookUpProcessModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.task.RetryLaterException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

/**
 * This class is add the error message while getting error from price service
 * for product in cart model
 */
public class AddErrorMessageToCartAction extends
        PartnerAbstractSimpleDecisionAction<PriceLookUpProcessModel> {

    private static final Logger LOG = Logger.getLogger(AddErrorMessageToCartAction.class);
    private static final String ERROR_MESSAGE = "product.price.error.message";
    private static final String DEFAULT_ERROR_MESSAGE = "Due to price service issue, unable to get the price for product. Please retry after a few minutes.";
    private ConfigurationService configurationService;

    protected AddErrorMessageToCartAction(Integer maxRetryAllowed, Integer retryDelay, ConfigurationService configurationService) {
        super(maxRetryAllowed, retryDelay);
        this.configurationService = configurationService;
    }

    /**
     * Executes the action to set NUll to the error message in the cart model.
     *
     * @param priceLookUpProcessModel The process model containing necessary data.
     * @return Transition.OK if successful, Transition.NOK otherwise.
     */
    @Override
    public Transition executeAction(PriceLookUpProcessModel priceLookUpProcessModel) throws RetryLaterException, Exception {
        AbstractOrderModel abstractOrderModel = priceLookUpProcessModel.getOrder();

        if (abstractOrderModel instanceof IbmPartnerCartModel) {
            IbmPartnerCartModel cartModel = (IbmPartnerCartModel) abstractOrderModel;
            final String errorMessage = getConfigurationService().getConfiguration().getString(ERROR_MESSAGE, DEFAULT_ERROR_MESSAGE);
            cartModel.setErrorMesaage(errorMessage);
            cartModel.setCalculated(Boolean.FALSE);
            modelService.save(cartModel);
            LOG.error(
                String.format("Error occurred in the price service business process [%s] ", priceLookUpProcessModel.getCode()));
            return Transition.OK;
        }
        return Transition.NOK;
    }

    public ConfigurationService getConfigurationService() {
        return configurationService;
    }


}
