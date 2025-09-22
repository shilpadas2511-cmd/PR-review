package com.ibm.commerce.partner.core.actions.order;

import com.ibm.commerce.partner.core.actions.PartnerAbstractSimpleDecisionAction;
import com.ibm.commerce.partner.core.model.IbmPartnerCartModel;
import com.ibm.commerce.partner.core.model.PriceLookUpProcessModel;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.order.CalculationService;
import de.hybris.platform.task.RetryLaterException;
import java.text.MessageFormat;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
public class RemovePricingInformationAction extends
    PartnerAbstractSimpleDecisionAction<PriceLookUpProcessModel> {

    private static final Logger LOG = LoggerFactory.getLogger(RemovePricingInformationAction.class);

    private final CalculationService calculationService;

    protected RemovePricingInformationAction(final Integer maxRetryAllowed,
        final Integer retryDelay, final CalculationService calculationService) {
        super(maxRetryAllowed, retryDelay);
        this.calculationService = calculationService;
    }

    @Override
    public Transition executeAction(final PriceLookUpProcessModel processModel)
        throws RetryLaterException {
        final String msg = MessageFormat.format("In {0} for process code : {1}",
            this.getClass().getSimpleName(), processModel.getCode());
        LOG.debug(msg);
        try {
            final AbstractOrderModel order = processModel.getOrder();
            if (order != null) {
                removeOrderPricingInformation((IbmPartnerCartModel) order);
                saveAndRefresh(order);
                return Transition.OK;
            }
        } catch (Exception e) {
            retryOrFailAction(processModel, msg);
        }
        return Transition.NOK;
    }

    protected void saveAndRefresh(final ItemModel itemModel) {
        getModelService().save(itemModel);
        getModelService().refresh(itemModel);
    }

    /**
     * Removes all pricing information from the given order header.
     *
     * @param order The order from which pricing information will be removed.
     */
    protected void removeOrderPricingInformation(final IbmPartnerCartModel order) {
        order.setTotalMEPPrice(0.0);
        order.setTotalPrice(0.0);
        order.setTotalEntitledPrice(0.0);
        order.setTotalFullPrice(0.0);
        order.setTotalOptimalPrice(0.0);
        order.setTotalBidExtendedPrice(0.0);
        order.setTotalBpExtendedPrice(0.0);
        order.setYtyPercentage(0.0);
        order.setTotalDiscounts(0.0);
        order.setTotalChannelMargin(0.0);
    }

    public CalculationService getCalculationService() {
        return calculationService;
    }
}
